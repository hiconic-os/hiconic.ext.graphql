package hiconic.ext.graphql;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import com.braintribe.codec.marshaller.api.OutputPrettiness;
import com.braintribe.codec.marshaller.json.JsonStreamMarshaller;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.utils.StringTools;

import hiconic.ext.graphql.api.model.GraphQlFieldArguments;
import hiconic.ext.graphql.api.model.GraphQlMutationRequest;
import hiconic.ext.graphql.api.model.GraphQlQuery;
import hiconic.ext.graphql.api.model.GraphQlQueryRequest;
import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;

/**
 * @author Ralf Ulrich
 *
 */

public class GraphQlRequestMarshaller implements Marshaller {

	final String responseAlias = "value"; // alias to be used for all GraphQl responses

	private JsonStreamMarshaller jsonMarshaller = new JsonStreamMarshaller(); // TODO: is this the right place for this?

	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {

		GraphQlRequest query = (GraphQlRequest) value;
		EntityType<GenericEntity> queryType = query.entityType();
		StringWriter writer = new StringWriter();

		String indent = " "; // output formatting

		String requestName = queryType.getShortName();

		// remove trailing Request - naming convention
		int index = requestName.lastIndexOf("Request");
		if (index > 0) {
			requestName = StringTools.uncapitalize(requestName.substring(0, index));
		}

		try {
			generateRequest(query, queryType, writer, indent, requestName);
			writer.close();
		} catch (IOException ioe) {
			String error = "During generation of GraphQl query for GraphQlRequest \"" + requestName + "\" an IO exception occured \"" + ioe.toString()
					+ "\"";
			throw new MarshallException(error);
		}

		String graphQlQueryContent = writer.toString();

		// wrap query string into json and return, TODO is this the right place to do
		// this?

		GraphQlQuery gqlQuery = GraphQlQuery.T.create();
		gqlQuery.setQuery(graphQlQueryContent);

		StringWriter jsonContentWriter = new StringWriter();
		jsonMarshaller.marshall(jsonContentWriter, gqlQuery, GmSerializationOptions.deriveDefaults().outputPrettiness(OutputPrettiness.none).build());
		String jsonContent = jsonContentWriter.toString();
		jsonContent = jsonContent.replace("\"_type\": \"hiconic.ext.graphql.api.model.GraphQlQuery\", \"_id\": \"0\",", "");

		PrintWriter outWriter = new PrintWriter(out);
		outWriter.print(jsonContent);
		outWriter.flush();
	}

	private void generateRequest(GraphQlRequest request, EntityType<GenericEntity> queryType, Appendable writer, String indent, String requestName)
			throws IOException {

		// initiate the query
		if (request instanceof GraphQlMutationRequest) {
			writer.append("mutation {\n" + indent);
		} else if (request instanceof GraphQlQueryRequest) {
			writer.append("query {\n" + indent);
		} else {
			throw new IllegalStateException("Unsupported GraphQL request type " + request);
		}
		writer.append(responseAlias + " : " + requestName); // by convention, TODO add alias
		String windent = indent + "    ";
		String args = parseParameter(request, windent);
		if (!args.isEmpty())
			writer.append("(" + args + ")"); // if there are parameters

		writer.append("{\n");

		// there must at least be a "select" property, otherwise it is not a GraphQL
		// query
		Property selectProperty = queryType.findProperty("select"); // by convention
		GenericModelType selectType = selectProperty.getType();

		if (selectType.isEntity()) {

			GenericEntity select = selectProperty.get(request);
			writer.append(indent + queryEntity(select, indent + " ") + "\n" + indent + "}");

		} else if (selectType.isCollection() && selectType.areCustomInstancesReachable()) {

			Collection<? super GenericEntity> collection = (Collection<? super GenericEntity>) selectProperty.get(request);
			int n = collection.size();
			if (n != 1) {
				String error = "Malformed GraphQlRequest with collection \"" + selectType.toString() + "\" contains " + n
						+ " elements. Must be exactly one.";
				throw new MarshallException(error);
			}
			Object object = collection.iterator().next();
			if (!(object instanceof GenericEntity)) {
				String error = "Malformed GraphQlRequest. Element type of collection \"" + selectType.toString() + "\" is \""
						+ object.getClass().getTypeName() + "\"";
				throw new MarshallException(error);
			}
			GenericEntity select = (GenericEntity) object;
			writer.append(indent + queryEntity(select, indent + " ") + "\n" + indent + "}");

		} else {
			String error = "The \"select\" property of the GraphQlRequest \"" + requestName + "\" has unsupported type \"" + selectType + "\"";
			throw new MarshallException(error);
		}

		// TODO: evals-to checken und typen kontrollieren
		// queryType.getEvaluatesTo().isAssignableFrom(select.entityType());

		// close the query
		writer.append("\n}\n");
	}

	/**
	 * Select GraphQl output fields from entity. The entity may provide args (GraphQlFieldArguments) for some of its
	 * properties.
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

		List<String> entries = loopProperties(entity, args, indent);

		if (entries.isEmpty()) {
			String error = "Malformed GraphQlRequest with no selected return fields";
			throw new MarshallException(error);
		}

		// TODO: is this the right place for "id" mapping ?
		return entries.stream().map(e -> {
			if (e != null && e.equals("id_")) {
				return "id";
			}
			return e;
		}).collect(Collectors.joining("\n" + indent, indent, ""));
	}

	/**
	 * GraphQl types can provide arguments for some of their named properties. In this case there must be propertyname_args
	 * extra property of type {@link GraphQlFieldArguments} containing the arguments. In a GraphQl schema, this corresponds
	 * to InputTypes.
	 * 
	 * This function reads all GraphQlFieldArguments (in depth) and returns them in a map to link them to the respective
	 * properties.
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

			// remove the trailing "_args_"
			if (name.contains("_args_"))
				name = name.substring(0, name.lastIndexOf("_args_"));

			String parseInputType = parseParameter((GenericEntity) value, indent);
			args.put(name, "(" + parseInputType + ")");
		}

		return args;
	}

	/**
	 * Loops all properties of entity under the assumption of it being {@link GraphQlFieldArguments} (see also GraphQl
	 * InputType) OR a GraphQl request. GraphQlRequest.
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

		List<String> arg = new ArrayList<String>();

		EntityType<GenericEntity> et = entity.entityType();
		for (Property p : et.getProperties()) {

			Object value = p.get(entity);
			if (value == null) // no data
				continue;

			GenericModelType type = p.getType();
			String name = p.getName();

			if (isRequest) {
				// TODO : improve
				if (name.equals("select") || name.equals("metaData") || name.equals("authorization") || name.equals("contentType")
						|| name.equals("domainId"))
					continue;
			}

			// entity must be GraphQlFieldArguments
			if (type.isEntity()) {
				// if (!(value instanceof GraphQlFieldArguments)) {
				//
				// String error = "Property of \"" + et.getShortName() + "\" has type " + type.getTypeName()
				// + " is not an GraphQlFieldArguments but must be.";
				// throw new MarshallException(error);
				// }
				arg.add(name + " : { " + parseParameter((GenericEntity) value, indent + " ") + "}");

			} else if (type.isCollection()) {

				// this must be a filled collection of simple types. See GraphQl inputTypes.

				Collection<?> collection = (Collection<?>) p.get(entity);
				if (collection.isEmpty())
					continue;
				String argList = "";
				String commaArgList = "";
				String windent = indent + " ";
				for (Object object : collection) {

					if (object instanceof GenericEntity) {
						GenericEntity genericEntity = (GenericEntity) object;
						argList += commaArgList + "{ " + parseParameter(genericEntity, windent) + " }";
					} else {
						String typeName = object.getClass().getTypeName();
						argList += commaArgList + simpleType(object, typeName);
					}

					commaArgList = ", ";
				}
				arg.add(name + " : [" + argList + "]");

			} else if (type.isSimple()) {

				if (name.equals("id_")) {
					name = "id";
				}

				String typeName = type.getTypeName();
				arg.add(name + " : " + simpleType(value, typeName));

			} else if (type.isEnum()) {

				arg.add(name + " : " + ((Enum<?>) value).name());

			} else {

				String error = "Unknown property of GraphQlFieldArgument: \"" + name + "\" value \"" + value + "\"";
				throw new MarshallException(error);
			}
		}

		return arg.stream().collect(Collectors.joining(", "));
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
	 * Loops over all properties of entity (in-depth), adds provided arguments from map and marshalls to a query output.
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

			if (p.getDeclaringType().equals(GenericEntity.T)) {
				continue;
			}

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
					String error = "Malformed GraphQl-request-field with collection \"" + type.toString() + "\" contains " + n
							+ " elements. Must be exactly one.";
					throw new MarshallException(error);
				}

				if (type.areEntitiesReachable()) {

					Object object = collection.iterator().next();
					if (!(object instanceof GenericEntity)) {
						String error = "Element type of collection \"" + type.toString() + "\" is \"" + object.getClass().getTypeName() + "\"";
						throw new MarshallException(error);
					}

					GenericEntity nextEntity = (GenericEntity) object;

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
	 * @param s
	 *            The input String.
	 * @return The output String.
	 **/
	public static String escape(String s) {
		//@formatter:off
		return s.replace("\\", "\\\\")
				.replace("\t", "\\t")
				.replace("\b", "\\b")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\f", "\\f")
				.replace("\"", "\\\"");
		//@formatter:on
	}
}
