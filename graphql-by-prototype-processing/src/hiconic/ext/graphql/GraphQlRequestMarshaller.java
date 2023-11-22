package hiconic.ext.graphql;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;

import hiconic.ext.graphql.api.model.GraphQlFieldArguments;
import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;

/**
 * @author Ralf Ulrich
 *
 */

public class GraphQlRequestMarshaller implements Marshaller {

	final String responseAlias = "value"; // alias to be used for all GraphQl resposes

	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {

		GraphQlRequest query = (GraphQlRequest) value;
		EntityType<GenericEntity> queryType = query.entityType();
		PrintWriter writer = new PrintWriter(out);
		// TODO: Writer (OutputStreamWriter)
		// TODO: encoding UTF8, or check with graphql

		String indent = " "; // output formatting

		// initiate the query
		writer.print("query {\n" + indent);
		writer.print(responseAlias + " : " + queryType.getShortName()); // by convention, TODO add alias

		String windent = indent + "    ";
		String args = parseParameter(query, windent);
		if (args != "")
			writer.print("(" + args + ")"); // if there are parameters

		// there must at least be a "select" property, otherwise it is not a GraphQL
		// query
		Property selectProperty = queryType.findProperty("select"); // by convention
		GenericModelType selectType = selectProperty.getType();

		if (selectType.isEntity()) {

			GenericEntity select = selectProperty.get(query);
			writer.print("\n" + indent + "{" + queryEntity(select, indent + " ") + "\n" + indent + "}");

		} else if (selectType.isCollection() && selectType.areCustomInstancesReachable()) {

			Collection<? super GenericEntity> collection = (Collection<? super GenericEntity>) selectProperty
					.get(query);
			int n = collection.size();
			if (n != 1) {
				System.err.println("wrong collection!");
			}
			Object object = collection.iterator().next();
			GenericEntity select = (GenericEntity) object; // TODO add type check
			writer.print("\n" + indent + "{" + queryEntity(select, indent + " ") + "\n" + indent + "}");

		} else {
			System.err.println("NOT supported !!!");
		}

		// TODO: evals-to checken und typen kontrollieren
		// queryType.getEvaluatesTo().isAssignableFrom(select.entityType());

		// the remaining properties are arguments to the top-level query
		// ...

		// parse the entity

		// close the query
		writer.print("\n}\n");
		writer.flush();
	}

	/**
	 * Select GraphQl output fields from entity. The entity may provide args
	 * (GraphQlFieldArguments) for some of its properties.
	 * 
	 * @param entity
	 * @param indent
	 * @return
	 */
	private String queryEntity(GenericEntity entity, String indent) {

		if (entity == null)
			return "";

		if (entity instanceof GraphQlFieldArguments)
			return "";

		// if it is an entity it might have arguments attached for specific properties.
		Map<String, String> args = getArgs(entity, indent + " ");
		// String arguments = loopArgs(entity, indent);

		List<String> entries = loopProperties(entity, args, indent);

		if (entries.isEmpty()) {
			System.err.println("no output selected!");
		}

		// TODO: complete streaming solution
		return entries.stream().collect(Collectors.joining("", "\n" + indent, ""));
	}

	/**
	 * GraphQl types can provide arguments for some of their named properties. In
	 * this case there must be propertyname_args extra property of type
	 * {@link GraphQlFieldArguments} containing the arguments. In a GraphQl schema,
	 * this corresponds to InputTypes.
	 * 
	 * This function reads all GraphQlFieldArguments (in depth) and returns them in
	 * a map to link them to the respective properties.
	 * 
	 * @param entity
	 * @return A map from propertyname to the associated arguments
	 */
	private Map<String, String> getArgs(GenericEntity entity, String indent) {

		if (!(entity instanceof HasGraphQlFieldArguments))
			return null;

		Map<String, String> args = new HashMap<>();

		EntityType<GenericEntity> et = entity.entityType();
		for (Property p : et.getProperties()) {

			Object value = p.get(entity);
			if (value == null)
				continue;

			GenericModelType type = p.getType();

			// arguments must be entities
			if (!type.isEntity())
				continue;

			if (!(value instanceof GraphQlFieldArguments))
				continue;

			String name = p.getName(); // convention: propertyname_args

			// remove the trailing "_arg[s]"
			if (name.contains("_arg"))
				name = name.substring(0, name.lastIndexOf("_arg"));

			String parseInputType = parseParameter((GenericEntity) value, indent);
			args.put(name, "(" + parseInputType + ")");
		}

		return args;
	}

	/**
	 * Loops all properties of entity under the assumption of it being
	 * {@link GraphQlFieldArguments} (see also GraphQl InputType) OR a GraphQl
	 * request. GraphQlRequest.
	 * 
	 * @param entity
	 * @param indent
	 * @return
	 */
	private String parseParameter(GenericEntity entity, String indent) {
		// input types cannot have collections inside. See GraphQl documentation.

		// boolean isInputType = entity instanceof GraphQlFieldArguments;
		boolean isRequest = entity instanceof GraphQlRequest;
		// if (!isRequest && !isInputType)
		// return ""; // incompatible input data

		String arg = "";
		String comma = "";

		EntityType<GenericEntity> et = entity.entityType();
		for (Property p : et.getProperties()) {

			Object value = p.get(entity);
			if (value == null) // no data
				continue;

			GenericModelType type = p.getType();
			String name = p.getName();

			if (isRequest) {
				if (name.equals("select") || name.equals("metaData"))
					continue;
			}

			// entity must be GraphQlFieldArguments
			if (type.isEntity()) {
				if (!(value instanceof GraphQlFieldArguments)) {
					System.err.println("property of \"" + et.getShortName() + "\" has type " + type.getTypeName()
							+ " is not an GraphQlFieldArguments. Check \"inputtypes\" in GraphQl.");
					continue;
				}
				arg += comma + "\n" + indent + name + " : { "
						+ parseParameter((GraphQlFieldArguments) value, indent + " ") + "}";

			} else if (type.isCollection()) {

				// this must be a filled collection of simple types. See GraphQl inputTypes.

				Collection<?> collection = (Collection<?>) p.get(entity);
				if (collection.isEmpty())
					continue;
				arg += comma + "\n" + indent + name + " : [";
				String commaArg = "";
				String windent = indent + " ";
				for (Object object : collection) {

					String typeName = object.getClass().getTypeName();
					arg += commaArg + "\n" + windent + simpleType(object, typeName);
					commaArg = ",";
				}
				arg += "]";

			} else if (type.isSimple()) {

				arg += comma + "\n" + indent + name + " : ";
				String typeName = type.getTypeName();
				arg += simpleType(value, typeName);

			} else {

				System.err
						.println("Unknown property of GraphQlFieldArgument: \"" + name + "\" value \"" + value + "\"");
			}
			comma = ",";
		}

		return arg;
	}

	/**
	 * Actual output marshalling of simple types.
	 * 
	 * @param value
	 * @param typeName
	 * @return
	 */
	private String simpleType(Object value, String typeName) {

		String arg = "";

		if (typeName == "date") {
			Date date = (Date) value;
			String timestamp = new SimpleDateFormat("\"yyyy-MM-dd'T'h:m:ssZ\"").format(date);
			arg += timestamp;

		} else if (typeName == "string" || typeName == "java.lang.String") {

			String str = (String) value;
			arg += "\"" + escape(str) + "\"";

		} else if (typeName == "boolean") {

			if ((boolean) value)
				arg += "true";
			else
				arg += "false";

		} else {
			arg += value;
		}
		return arg;
	}

	/**
	 * Loops over all properties of entity (in-depth), adds provided arguments from map and
	 * marshalls to a query output.
	 * 
	 * @param entity
	 * @param args
	 * @param indent
	 * @return
	 */
	private List<String> loopProperties(GenericEntity entity, Map<String, String> args, String indent) {
		List<String> entries = new ArrayList<>();

		EntityType<GenericEntity> et = entity.entityType();
		for (Property p : et.getProperties()) {

			Object value = p.get(entity);
			if (value == null)
				continue;

			GenericModelType type = p.getType();

			String name = p.getName();
			String arg = args == null ? "" : (args.containsKey(name) ? args.get(name) : "");

			if (type.isCollection()) {

				Collection<? super GenericEntity> collection = ((Collection<? super GenericEntity>) value);
				int n = collection.size();
				if (n == 0)
					continue;
				if (n != 1) {
					System.err.println("wrong collection!");
				}

				if (type.areEntitiesReachable()) {

					Object object = collection.iterator().next();
					GenericEntity nextEntity = (GenericEntity) object; // TODO add type check

					String queryEntity = queryEntity(nextEntity, indent + " ");
					if (queryEntity != "")
						entries.add(name + arg + "{" + queryEntity + "\n" + indent + "}");

				} else {

					entries.add(name + arg);
				}
			} else if (type.isEntity()) {

				GenericEntity properyEntity = p.get(entity);
				String queryEntity = queryEntity(properyEntity, indent + " ");
				if (queryEntity != "")
					entries.add(name + arg + "{\n" + queryEntity + "\n" + indent + "}");

			} else {

				entries.add(name + arg);
			}
		}
		return entries;
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
