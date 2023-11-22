package hiconic.ext.graphql;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.ValueDescriptor;

import hiconic.ext.graphql.api.model.GraphQlInfo;
import hiconic.ext.graphql.api.model.QueryByPrototype;

/**
 * Converts {@link QueryByPrototype} request into valid but basic GraphQL
 * queries. The following conventions are used
 * 
 * <ul>
 * <li>If prototype property setter was called -> used for conditional event
 * selection
 * <li>If prototype property getter was called -> used for output selection
 * </ul>
 * 
 * The following limitations also apply
 * <ul>
 * <li>GenericEntities are never used as input for conditions, only properties
 * of entities.
 * <li>Only GenericEntities can be decorated with "where" statements -- based on
 * their properties.
 * <li>Only "eq_" comparators, thus identity, are possible.
 * <li>Only logical "and_" statements can be constructed.
 * </ul>
 * 
 * @author Ralf Ulrich
 *
 */

public class GraphQlByPrototypeMarshaller implements Marshaller {

	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {
		QueryByPrototype query = (QueryByPrototype) value;
		PrintWriter writer = new PrintWriter(out);

		GenericEntity prototype = query.getPrototype();
		EntityType<GenericEntity> et = prototype.entityType();

		String indent = " ";

		// initiate the query
		writer.print("query {\n" + indent);
		writer.print(et.getShortName());

		// parse the entity
		writer.print(queryEntity(prototype, indent + " "));

		// close the query
		writer.print("\n}\n");
		writer.flush();
	}

	private String queryEntity(GenericEntity entity, String indent) {

		if (entity == null)
			return "";

		StringBuilder output = new StringBuilder();

		// add conditionals selection
		String conditions = conditionsEntity(entity, indent + " ");
		if (!conditions.isEmpty())
			output.append(conditions);

		// and last, go through all properties and siblings

		List<String> entries = new ArrayList<>();

		EntityType<GenericEntity> et = entity.entityType();
		for (Property p : et.getProperties()) {

			if (p.isAbsent(entity))
				continue;

			GenericModelType type = p.getType();

			if (type.isCollection() && type.areEntitiesReachable()) {
				EntityType<?> declaringType = p.getDeclaringType();
				EntityType<?> firstDeclaringType = p.getFirstDeclaringType();
				GenericModelType[] parameterization = type.getParameterization();
				GenericModelType p0 = parameterization[0];
				GenericModelType cast = type.cast();				
			}

			if (type.isEntity()) {
				GenericEntity properyEntity = p.get(entity);
				entries.add(p.getName() + queryEntity(properyEntity, indent + " "));

			} else if (type.isCollection() && type.areEntitiesReachable()) {
				// todo
			} else {

				ValueDescriptor vd = p.getVd(entity);
				if (!(vd instanceof GraphQlInfo)) {
					throw new MarshallException("Invalid input data.");
				}

				GraphQlInfo gqi = (GraphQlInfo) vd;
				if (gqi.getSelect()) {
					entries.add(p.getName());
				}
			}
		}

		output.append(" {\n" + indent + " ");
		output.append(String.join("\n" + indent + " ", entries));
		output.append("\n" + indent + "}");
		return output.toString();
	}

	private String conditionsEntity(GenericEntity entity, String indent) {

		if (entity == null)
			return "";

		EntityType<GenericEntity> et = entity.entityType();

		List<String> entries = new ArrayList();

		for (Property p : et.getProperties()) {

			if (p.isAbsent(entity))
				continue;

			ValueDescriptor vd = p.getVd(entity);
			if (!(vd instanceof GraphQlInfo)) {
				throw new MarshallException("Invalid input data.");
			}

			GraphQlInfo gqi = (GraphQlInfo) vd;
			Object value = gqi.getValue();
			if (value == null)
				continue;

			GenericModelType type = p.getType();
			if (type.isEntity()) {
				String innerEntity = conditionsEntity(p.get(entity), indent + "    ");
				if (innerEntity != null && innerEntity != "") {
					entries.add("{" + p.getName() + innerEntity + "}");
				}

			} else if (type.isSimple()) {

				StringBuilder property = new StringBuilder();
				property.append("{" + p.getName() + " : {eq_ : ");

				String typeName = type.getTypeName();
				if (typeName == "date") {
					Date date = (Date) value;
					String timestamp = new SimpleDateFormat("\"yyyy-MM-dd'T'h:m:ssZ\"").format(date);
					property.append(timestamp);

				} else if (typeName == "string") {

					String str = (String) value;
					property.append("\"" + escape(str) + "\"");

				} else if (typeName == "boolean") {

					if ((boolean) value)
						property.append("true");
					else
						property.append("false");

				} else {
					property.append(value);

				}
				property.append("}}");
				entries.add(property.toString());
			}
		}
		if (entries == null || entries.isEmpty())
			return "";

		StringBuilder cond = new StringBuilder();
		indent = indent + " ";
		String windent = indent;
		cond.append(" (\n" + windent + "where: ");
		if (entries.size() == 1)
			cond.append(entries.get(0));
		else {
			indent = indent + " ";
			cond.append(" {and_: [\n" + indent);
			cond.append(String.join(", \n" + indent, entries));
			cond.append("\n" + windent + "]}");
		}
		cond.append(")");
		return cond.toString();
	}

	// We do not need GraphQL -> QueryByPrototype marshalling
	@Override
	public Object unmarshall(InputStream in, GmDeserializationOptions options) throws MarshallException {
		throw new UnsupportedOperationException();
	}

	/**
	 * escape()
	 *
	 * Escape a give String to make it safe to be printed or stored.
	 *
	 * @param s The input String.
	 * @return The output String.
	 **/
	public static String escape(String s) {
		return s.replace("\\", "\\\\").replace("\t", "\\t").replace("\b", "\\b").replace("\n", "\\n")
				.replace("\r", "\\r").replace("\f", "\\f").replace("\'", "\\'") // <== not necessary
				.replace("\"", "\\\"");
	}

}
