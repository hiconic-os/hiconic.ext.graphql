// ============================================================================
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package hiconic.ext.graphql;

import static com.braintribe.utils.lcd.CollectionTools2.first;
import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.StringTools.removeSuffixIfEligible;
import static com.braintribe.utils.lcd.StringTools.uncapitalize;
import static hiconic.ext.graphql.api.model.GraphQlQueryRequest.SELECTION_PROPERTY_NAME;
import static java.util.stream.Collectors.joining;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import com.braintribe.codec.marshaller.api.GmDeserializationOptions;
import com.braintribe.codec.marshaller.api.GmSerializationOptions;
import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.api.OutputPrettiness;
import com.braintribe.codec.marshaller.json.JsonStreamMarshaller;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.BaseType;
import com.braintribe.model.generic.reflection.CollectionType;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TypeCode;

import hiconic.ext.graphql.api.model.GraphQlFieldArguments;
import hiconic.ext.graphql.api.model.GraphQlMutationRequest;
import hiconic.ext.graphql.api.model.GraphQlQuery;
import hiconic.ext.graphql.api.model.GraphQlQueryRequest;
import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;
import hiconic.ext.graphql.api.model.HasGraphQlTypeConditions;

public class GraphQlRequestMarshaller implements Marshaller {

	final String RESPONSE_ALIAS = "value"; // alias to be used for all GraphQl responses

	private final JsonStreamMarshaller jsonMarshaller = new JsonStreamMarshaller(); // TODO: is this the right place for this?

	@Override
	public Object unmarshall(InputStream in, GmDeserializationOptions options) throws MarshallException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void marshall(OutputStream out, Object value, GmSerializationOptions options) throws MarshallException {
		String queryString = encodeRequestToQueryString(value);

		String jsonContent = wrapToJson(options, queryString);

		PrintWriter outWriter = new PrintWriter(out);
		outWriter.print(jsonContent);
		outWriter.flush();
	}

	private String wrapToJson(GmSerializationOptions options, String queryString) {
		options = GmSerializationOptions.deriveDefaults().outputPrettiness(OutputPrettiness.none).build();

		GraphQlQuery queryEntity = GraphQlQuery.T.create();
		queryEntity.setQuery(queryString);

		StringWriter sw = new StringWriter();
		jsonMarshaller.marshall(sw, queryEntity, options);
		String jsonContent = sw.toString();
		return jsonContent.replace("\"_type\": \"" + GraphQlQuery.T.getTypeSignature() + "\", \"_id\": \"0\",", "");
	}

	private String encodeRequestToQueryString(Object value) {
		if (value == null)
			throw new IllegalArgumentException("Cannot encode null as a GraphQL query string.");

		if (!(value instanceof GraphQlRequest))
			throw new IllegalArgumentException("Unsupported GraphQL request " + value + " of type " + value.getClass().getName());

		GraphQlRequest query = (GraphQlRequest) value;
		String requestName = resolveRequestName(query);

		String indent = " "; // output formatting
		return generateRequest(query, indent, requestName);
	}

	private String resolveRequestName(GraphQlRequest query) {
		String requestName = query.entityType().getShortName();
		String queryTypeName = removeSuffixIfEligible(requestName, "Request");
		return uncapitalize(queryTypeName);
	}

	private String generateRequest(GraphQlRequest request, String indent, String requestName) {
		StringBuilder writer = new StringBuilder();

		// initiate the query
		if (request instanceof GraphQlMutationRequest)
			writer.append("mutation {\n" + indent);
		else if (request instanceof GraphQlQueryRequest)
			writer.append("query {\n" + indent);
		else
			throw new IllegalStateException("Unsupported GraphQL request type " + request);

		writer.append(RESPONSE_ALIAS + ": " + requestName); // by convention, TODO add alias
		String args = encodeArguments(request);
		if (!args.isEmpty())
			writer.append("(" + args + ")"); // if there are parameters

		writer.append(" {\n");

		GenericEntity select = resolveSelect(request, requestName);
		writer.append(encodeEntitySelection(select, indent + " ") + "\n" + indent + "}");

		writer.append("\n}\n");

		return writer.toString();
	}

	private GenericEntity resolveSelect(GraphQlRequest request, String requestName) {
		Property selectProperty = request.entityType().getProperty(SELECTION_PROPERTY_NAME);

		GenericModelType selectType = selectProperty.getType();
		if (!selectType.isEntity())
			throw new MarshallException("'select' property of the GraphQlRequest '" + requestName + "' has unsupported type '" + selectType + "'");

		return selectProperty.get(request);
	}

	private String encodeEntitySelection(GenericEntity entity, String indent) {
		if (entity == null)
			return "";

		if (entity instanceof GraphQlFieldArguments)
			return "";

		// if it is an entity it might have arguments attached for specific properties.
		Map<String, String> args = resolveFieldArgs(entity);

		List<String> entries = encodePropertySelections(entity, args, indent);
		if (entries.isEmpty())
			throw new MarshallException("Malformed GraphQlRequest with no selected return fields on entity: " + entity);

		return entries.stream() //
				.collect(joining("\n" + indent, indent, ""));
	}

	/**
	 * @return A map from property name to the encoded arguments
	 * 
	 * @see GraphQlFieldArguments
	 */
	private Map<String, String> resolveFieldArgs(GenericEntity entity) {
		if (!(entity instanceof HasGraphQlFieldArguments))
			return null;

		Map<String, String> args = newMap();

		for (Property p : entity.entityType().getDeclaredProperties()) {
			// arguments must be entities
			Object value = p.get(entity);
			if (value == null)
				continue;

			String name = p.getName(); // convention: propertyname_args
			name = removeSuffixIfEligible(name, "_args_");

			String arguments = encodeArguments((GenericEntity) value);
			args.put(name, "(" + arguments + ")");
		}

		return args;
	}

	private String encodeArguments(GenericEntity entity) {
		boolean isRequest = entity instanceof GraphQlRequest;
		EntityType<GenericEntity> entityType = entity.entityType();

		List<String> args = newList();

		EntityType<GenericEntity> et = entity.entityType();
		for (Property p : et.getProperties()) {
			EntityType<?> firstDeclaringType = p.getFirstDeclaringType();
			if (firstDeclaringType == GenericEntity.T)
				continue;

			Object value = p.get(entity);
			if (value == null) // no data
				continue;

			String name = p.getName();

			if (isRequest && (firstDeclaringType != entityType || name.equals(SELECTION_PROPERTY_NAME)))
				continue;

			String arg = encodeArgument(entity, p.getType(), value);
			if (arg != null)
				args.add(removeTrailingUnderscore(name) + ": " + arg);
		}

		return args.stream().collect(Collectors.joining(", "));
	}

	private String encodeArgument(GenericEntity entity, GenericModelType type, Object value) {
		GenericModelType propType = type;

		if (propType.isBase())
			propType = BaseType.INSTANCE.getActualType(value);

		if (propType.isEntity())
			return "{" + encodeArguments((GenericEntity) value) + "}";

		if (propType.isSimple())
			return simpleType(value, propType.getTypeCode());

		if (propType.isEnum())
			return ((Enum<?>) value).name();

		if (propType.isCollection()) {
			Collection<?> collection = (Collection<?>) value;
			if (collection.isEmpty())
				return null;

			GenericModelType elementType = ((CollectionType) propType).getCollectionElementType();

			StringJoiner sj = new StringJoiner(", ", "[", "]");
			for (Object element : collection)
				if (element != null)
					sj.add(encodeArgument(entity, elementType, element));

			return sj.toString();
		}

		return null;
	}

	private String simpleType(Object value, TypeCode typeCode) {
		switch (typeCode) {
			case dateType:
				return new SimpleDateFormat("\"yyyy-MM-dd'T'h:m:ssZ\"").format((Date) value);
			case stringType:
				return "\"" + escape((String) value) + "\"";
			case booleanType:
				return (boolean) value ? "true" : "false";
			default:
				return "" + value;
		}
	}

	private List<String> encodePropertySelections(GenericEntity entity, Map<String, String> args, String indent) {
		List<String> entries = newList();

		boolean hasTypeConditions = entity instanceof HasGraphQlTypeConditions;
		Collection<?> typeConditions = null;

		for (Property p : entity.entityType().getProperties()) {
			if (p.getDeclaringType() == GenericEntity.T)
				continue;

			Object value = p.get(entity);
			if (value == null)
				continue;

			if (hasTypeConditions && HasGraphQlTypeConditions.TYPE_CONDITIONS_PROPERTY_NAME.equals(p.getName())) {
				if (value instanceof Collection)
					typeConditions = (Collection<?>) value;
				continue;
			}

			GenericModelType type = p.getType();

			String name = p.getName();
			String arg = args != null && args.containsKey(name) ? args.get(name) : "";

			String propertySelection = encodePropertySelection(name, type, value, indent);
			if (propertySelection != null)
				entries.add(removeTrailingUnderscore(name) + arg + propertySelection);
		}

		if (typeConditions != null) {
			for (Object object : typeConditions) {
				if (!(object instanceof GenericEntity))
					continue;
				
				GenericEntity typeConditionEntity = (GenericEntity) object;
				String innerSelection = encodeEntitySelection(typeConditionEntity, indent + " ");

				entries.add("... on " + typeConditionEntity.entityType().getShortName() + " {");
				entries.add(innerSelection.substring(indent.length()));
				entries.add("}");
			}
			
		}
		
		return entries;
	}

	private String encodePropertySelection(String name, GenericModelType type, Object value, String indent) {
		if (type.isEntity()) {
			GenericEntity properyEntity = (GenericEntity) value;
			String queryEntity = encodeEntitySelection(properyEntity, indent + " ");
			if (queryEntity != "")
				return " {\n" + queryEntity + "\n" + indent + "}";

		} else if (type.isCollection()) {
			Collection<?> collection = ((Collection<?>) value);
			if (collection.isEmpty())
				return null;

			if (collection.size() != 1)
				throw new MarshallException("Malformed GraphQl-request-field, collection " + type.getTypeSignature() + "#" + name + " contains "
						+ collection.size() + " elements. Must be exactly 1.");

			if (!type.areEntitiesReachable())
				return "";

			Object object = first(collection);
			if (!(object instanceof GenericEntity))
				throw new MarshallException(
						"Element of collection " + type.getTypeSignature() + "#" + name + " is not an entity, but: " + object.getClass().getName());

			String entitySelection = encodeEntitySelection((GenericEntity) object, indent + " ");
			if (entitySelection != "")
				return " {\n" + entitySelection + "\n" + indent + "}";

		} else {
			return "";
		}

		return null;
	}

	private String removeTrailingUnderscore(String name) {
		return removeSuffixIfEligible(name, "_");
	}

	/** Escape a give String to make it safe to be printed or stored. **/
	private static String escape(String s) {
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
