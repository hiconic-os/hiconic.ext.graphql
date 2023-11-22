package hiconic.ext.graphql.schema.gm;

import static com.braintribe.utils.lcd.CollectionTools2.newList;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static com.braintribe.utils.lcd.CollectionTools2.newSet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmListType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmType;
import com.braintribe.utils.StringTools;

import graphql.language.BooleanValue;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumValue;
import graphql.language.EnumValueDefinition;
import graphql.language.FieldDefinition;
import graphql.language.FloatValue;
import graphql.language.ImplementingTypeDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.IntValue;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ScalarValue;
import graphql.language.StringValue;
import graphql.language.Type;
import graphql.language.TypeDefinition;
import graphql.language.TypeName;
import graphql.language.UnionTypeDefinition;
import graphql.language.Value;
import graphql.schema.idl.TypeDefinitionRegistry;
import hiconic.ext.graphql._GraphqlApiModel_;
import hiconic.ext.graphql.api.model.GraphQlFieldArguments;
import hiconic.ext.graphql.api.model.GraphQlInputType;
import hiconic.ext.graphql.api.model.GraphQlMutationRequest;
import hiconic.ext.graphql.api.model.GraphQlQueryRequest;
import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;
import hiconic.ext.graphql.api.model.result.GraphQlResult;
import hiconic.gm.model.builder.GmCustomTypeBuilder;
import hiconic.gm.model.builder.GmEntityTypeBuilder;
import hiconic.gm.model.builder.GmEnumTypeBuilder;
import hiconic.gm.model.builder.GmModelBuilder;
import hiconic.gm.model.builder.GmPropertyBuilder;

/**
 * <h3>Special property names and underscores</h3>
 * 
 * Since we have some properties already used - <b>id</b>, <b>partition</b>, <b>globalId</b> (and <b>select</b>, <b>domainId</b> for <b>Requests</b>),
 * when encountering such fields, we add a "_" suffix.
 * <p>
 * To avoid possible conflicts, every field who's name ends with "_" is converted to a property with an extra "_".
 * <p>
 * <b>Example:</b>
 * 
 * <pre>
 * type IdOwner {
 *   id: ID!
 *   number_: Integer
 * }
 * </pre>
 * 
 * We'd create a type with properties <b>id_</b> of type String (IDs are Strings) and <b>number__</b> of type Integer.
 * <p>
 * This is also the reason why {@link HasGraphQlFieldArguments} sub-types have those special properties ending with <b>_args_</b>. The trailing "_"
 * ensures it does not conflict with actual properties.
 * 
 * @see "https://spec.graphql.org/draft/"
 * 
 * @author peter.gazdik
 */
public class GraphQlSchemaToGmModel {

	public static GmModels createModels(String artifactIdBase, String groupId, String version, String packageBase, TypeDefinitionRegistry tdr) {
		return new GraphQlSchemaToGmModel(artifactIdBase, groupId, version, packageBase, tdr).createModels();
	}

	private final TypeDefinitionRegistry typeDefinitionRegistry;

	private final GmModelBuilder dataBuilder;
	private final GmModelBuilder apiBuilder;

	private final String packageBase;
	private final String requestBaseTypeSimpleName;
	private final String queryRequestBaseTypeSimpleName;
	private final String mutationRequestBaseTypeSimpleName;

	// This is only used to tell if an Enum is part of data model, or just API model
	private final Set<String> dataFieldTypes = newSet();

	private final Map<String, GmType> schemaTypes = newMap();
	private final Map<String, GmEntityTypeBuilder> schemaTypeEntityBuilders = newMap();
	private final Map<Type<?>, GmType> gqlTypeToGmType = newMap();

	private final Map<GmEntityType, Set<GmEntityType>> schemaTypeToSubType = newMap();
	private final Map<GmEntityType, GmEntityTypeBuilder> schemaTypeToFaBuilder = newMap();
	private final List<GmEntityType> hasFaTypes = newList();

	private final List<InputObjectTypeDefinition> inputObjectTypeDefinitions = newList();
	private final List<InterfaceTypeDefinition> interfaceTypeDefinitions = newList();
	private final List<EnumTypeDefinition> enumTypeDefinitions = newList();
	private final List<ObjectTypeDefinition> objectTypeDefinitions = newList(); // all Types
	private final List<ObjectTypeDefinition> dataTypeDefinitions = newList(); // Types except query, mutation and subscription
	private final List<UnionTypeDefinition> unionTypeDefinitions = newList();

	private final Set<String> GE_PROPERTY_NAMES = GenericEntity.T.getProperties().stream() //
			.map(Property::getName) //
			.collect(Collectors.toSet());

	private final Set<String> RESERVED_PROPERTY_NAMES = newSet();

	private ObjectTypeDefinition queryTypeDefinition;
	private ObjectTypeDefinition mutationTypeDefinition;

	private GraphQlSchemaToGmModel(String artifactIdBase, String groupId, String version, String packageBase, TypeDefinitionRegistry tdr) {
		String requestBaseNameBase = toPascalCase(artifactIdBase);
		this.requestBaseTypeSimpleName = requestBaseNameBase + "Request";
		this.queryRequestBaseTypeSimpleName = requestBaseNameBase + "QueryRequest";
		this.mutationRequestBaseTypeSimpleName = requestBaseNameBase + "MutationRequest";

		this.packageBase = packageBase;
		this.typeDefinitionRegistry = tdr;

		this.dataBuilder = GmModelBuilder.create(groupId + ":" + artifactIdBase + "-data-model", version);
		this.apiBuilder = GmModelBuilder.create(groupId + ":" + artifactIdBase + "-api-model", version, dataBuilder.newModel,
				GmModelBuilder.findModel(_GraphqlApiModel_.reflection));
	}

	private String toPascalCase(String artifactIdBase) {
		return Stream.of(artifactIdBase.split("-")) //
				.map(StringTools::capitalize) //
				.collect(Collectors.joining());
	}

	private GmModels createModels() {
		checkNoExtensions();

		indexTypeDefinitions();
		initTypes();
		indexScalars();
		collectDataFieldTypes();

		createEnumTypes();

		createTypeStubs();
		assignSuperTypes();
		createProperties();

		createFieldArgs();
		finalizerFieldArgsSuperTypes();

		createRequests();

		GmMetaModel dataModel = dataBuilder.buildAndVerify();
		GmMetaModel apiModel = apiBuilder.buildAndVerify();

		return new GmModels(apiModel, dataModel);
	}

	// ##############################################
	// ## . . . . . . . . Analysis . . . . . . . . ##
	// ##############################################

	private void checkNoExtensions() {
		checkNoExtension(typeDefinitionRegistry.enumTypeExtensions(), "enum");
		checkNoExtension(typeDefinitionRegistry.inputObjectTypeExtensions(), "inputObject");
		checkNoExtension(typeDefinitionRegistry.interfaceTypeExtensions(), "interface");
		checkNoExtension(typeDefinitionRegistry.objectTypeExtensions(), "object");
		checkNoExtension(typeDefinitionRegistry.scalarTypeExtensions(), "scalar");
		checkNoExtension(typeDefinitionRegistry.unionTypeExtensions(), "union");
	}

	private void checkNoExtension(Map<?, ? extends List<?>> extensions, String extType) {
		if (!extensions.isEmpty())
			throw new IllegalArgumentException(
					"Cannot convert schema to model, extensions are not supported, but " + extType + "TypeExtensions were found.");
	}

	@SuppressWarnings("rawtypes")
	private void indexTypeDefinitions() {
		for (TypeDefinition td : typeDefinitionRegistry.types().values()) {
			if (td instanceof InputObjectTypeDefinition iotd)
				inputObjectTypeDefinitions.add(iotd);

			else if (td instanceof InterfaceTypeDefinition itd)
				interfaceTypeDefinitions.add(itd);

			else if (td instanceof ObjectTypeDefinition otd)
				indexObjectTypeDefinition(otd);

			else if (td instanceof UnionTypeDefinition utd)
				unionTypeDefinitions.add(utd);

			else if (td instanceof EnumTypeDefinition etd)
				enumTypeDefinitions.add(etd);
		}
	}

	private void indexObjectTypeDefinition(ObjectTypeDefinition otd) {
		objectTypeDefinitions.add(otd);

		switch (otd.getName()) {
			case "Query":
				queryTypeDefinition = otd;
				return;
			case "Mutation":
				mutationTypeDefinition = otd;
				return;
			case "Subscription":
				throw new UnsupportedOperationException("Subscription is not supported!");
			default:
				dataTypeDefinitions.add(otd);
				return;
		}
	}

	private void initTypes() {
		schemaTypes.put("ID", dataBuilder.stringType());
		schemaTypes.put("String", dataBuilder.stringType());
		schemaTypes.put("Int", dataBuilder.integerType());
		schemaTypes.put("Float", dataBuilder.doubleType());
		schemaTypes.put("Boolean", dataBuilder.booleanType());
	}

	private void indexScalars() {
		for (String scalar : typeDefinitionRegistry.scalars().keySet())
			schemaTypes.putIfAbsent(scalar, dataBuilder.stringType());
	}

	private void collectDataFieldTypes() {
		collectDataTypes(interfaceTypeDefinitions);
		collectDataTypes(dataTypeDefinitions);
		collectDataTypesFromInputs(inputObjectTypeDefinitions);
	}

	private void collectDataTypes(List<? extends ImplementingTypeDefinition<?>> typeDefs) {
		for (ImplementingTypeDefinition<?> typeDef : typeDefs)
			for (FieldDefinition fd : typeDef.getFieldDefinitions())
				noticeDataType(fd.getType());
	}

	private void collectDataTypesFromInputs(List<InputObjectTypeDefinition> typeDefs) {
		for (InputObjectTypeDefinition typeDef : typeDefs)
			for (InputValueDefinition ivd : typeDef.getInputValueDefinitions())
				noticeDataType(ivd.getType());
	}

	private void noticeDataType(Type<?> type) {
		if (type instanceof ListType lt)
			noticeDataType(lt.getType());

		else if (type instanceof NonNullType nnt)
			noticeDataType(nnt.getType());

		else if (type instanceof TypeName tn)
			dataFieldTypes.add(tn.getName());
	}

	// ##############################################
	// ## . . . . . . . . . Enums . . . . . . . . .##
	// ##############################################

	private void createEnumTypes() {
		for (EnumTypeDefinition etd : enumTypeDefinitions) {
			String simpleName = etd.getName();

			boolean isDataType = isDataType(simpleName);

			String pckg = isDataType ? "data" : "api.input";
			String signature = packageBase + "." + pckg + "." + simpleName;

			GmModelBuilder modelBuilder = isDataType ? dataBuilder : apiBuilder;
			GmEnumTypeBuilder enumTypeBuilder = modelBuilder.acquireEnumTypeBuilder(signature);

			for (EnumValueDefinition constantDef : etd.getEnumValueDefinitions())
				enumTypeBuilder.createConstant(constantDef.getName());

			indexNewSchemaType(simpleName, enumTypeBuilder);
		}
	}

	private boolean isDataType(String name) {
		return dataFieldTypes.contains(name);
	}

	private void createRequests() {
		RESERVED_PROPERTY_NAMES.addAll(Arrays.asList("select", "domainId"));

		// Request Base
		GmEntityTypeBuilder requestBaseBuilder = createBaseRequestTypeStub(requestBaseTypeSimpleName);

		GmEntityType requestBaseGmType = requestBaseBuilder.it;
		requestBaseGmType.getSuperTypes().add(apiBuilder.getType(GraphQlRequest.T));

		// Query Request Base
		GmEntityTypeBuilder queryRequestBaseBuilder = createRequestTypeStub(queryRequestBaseTypeSimpleName, ReqType.query, true);

		GmEntityType queryRequestBaseGmType = queryRequestBaseBuilder.it;
		queryRequestBaseGmType.getSuperTypes().add(requestBaseGmType);
		queryRequestBaseGmType.getSuperTypes().add(apiBuilder.getType(GraphQlQueryRequest.T));

		createRequests(queryRequestBaseGmType, ReqType.query, queryTypeDefinition);

		if (mutationTypeDefinition != null) {
			GmEntityTypeBuilder mutationRequestBaseBuilder = createRequestTypeStub(mutationRequestBaseTypeSimpleName, ReqType.mutation, true);

			GmEntityType mutationRequestBaseGmType = mutationRequestBaseBuilder.it;
			mutationRequestBaseGmType.getSuperTypes().add(requestBaseGmType);
			mutationRequestBaseGmType.getSuperTypes().add(apiBuilder.getType(GraphQlMutationRequest.T));

			createRequests(mutationRequestBaseGmType, ReqType.mutation, mutationTypeDefinition);
		}

		RESERVED_PROPERTY_NAMES.clear();
	}

	/**
	 * @param reqType
	 *            one of "query", "mutation" and "subscription"
	 */
	private void createRequests(GmEntityType requestBaseGmType, ReqType reqType, ObjectTypeDefinition td) {
		for (FieldDefinition fd : td.getFieldDefinitions()) {
			GmEntityTypeBuilder builder = createRequest(requestBaseGmType, reqType, fd);

			builder.it.setEvaluatesTo(apiBuilder.getType(GraphQlResult.T));

			if (reqType != ReqType.subscription) {
				GmType selectType = resolveGmType(fd.getType());
				builder.createProperty("select", selectType).markMandatory();
			}
		}
	}

	private GmEntityTypeBuilder createRequest(GmEntityType superType, ReqType reqType, FieldDefinition fd) {
		String simpleName = toRequestName(fd.getName());

		GmEntityTypeBuilder builder = createRequestTypeStub(simpleName, reqType, false);

		builder.it.getSuperTypes().add(superType);

		createInputProperties(builder, fd.getInputValueDefinitions());

		return builder;
	}

	private static String toRequestName(String fieldName) {
		return StringTools.capitalize(fieldName) + "Request";
	}

	// ##############################################
	// ## . . . . . . . Entity Stubs . . . . . . . ##
	// ##############################################

	private void createTypeStubs() {
		// Data
		for (InterfaceTypeDefinition td : interfaceTypeDefinitions)
			createDataTypeStub(td.getName(), true);
		for (ObjectTypeDefinition td : dataTypeDefinitions)
			createDataTypeStub(td.getName(), false);
		for (UnionTypeDefinition td : unionTypeDefinitions)
			createDataTypeStub(td.getName(), true);

		// API
		for (InputObjectTypeDefinition td : inputObjectTypeDefinitions)
			createInputTypesTypeStub(td.getName(), false);
	}

	private void createDataTypeStub(String simpleName, boolean isAbstract) {
		createTypeStub(simpleName, isAbstract, "data", dataBuilder, true);
	}

	private void createInputTypesTypeStub(String simpleName, boolean isAbstract) {
		createTypeStub(simpleName, isAbstract, "api.input", apiBuilder, true);
	}

	private GmEntityTypeBuilder createBaseRequestTypeStub(String simpleName) {
		return createTypeStub(simpleName, true, "api", apiBuilder, false);
	}

	private GmEntityTypeBuilder createFieldArgsTypeStub(String simpleName, boolean isAbstract) {
		return createTypeStub(simpleName, isAbstract, "api.args", apiBuilder, false);
	}

	private GmEntityTypeBuilder createRequestTypeStub(String simpleName, ReqType reqType, boolean isAbstract) {
		return createTypeStub(simpleName, isAbstract, "api." + reqType.name(), apiBuilder, false);
	}

	/**
	 * @param isSchemaType
	 *            true for types that are directly declared in schema, and are their simple names are thus unique. Some types are generated on type,
	 *            e.g. types for Queries or {@link HasGraphQlFieldArguments}
	 */
	private GmEntityTypeBuilder createTypeStub(String simpleName, boolean isAbstract, String subPackage, GmModelBuilder modelBuilder,
			boolean isSchemaType) {

		String signature = packageBase + "." + subPackage + "." + simpleName;

		GmEntityTypeBuilder builder = modelBuilder.acquireEntityTypeBuilder(signature);
		builder.markAbstract(isAbstract);

		if (isSchemaType) {
			schemaTypeEntityBuilders.put(simpleName, builder);
			indexNewSchemaType(simpleName, builder);
		}

		schemaTypeToSubType.put(builder.it, newSet());

		return builder;
	}

	private void indexNewSchemaType(String simpleName, GmCustomTypeBuilder<?> builder) {
		schemaTypes.put(simpleName, builder.it);
	}

	// ##############################################
	// ## . . . . . . . SuperTypes . . . . . . . . ##
	// ##############################################

	private void assignSuperTypes() {
		for (InterfaceTypeDefinition td : interfaceTypeDefinitions)
			assignSuperTypes(td.getName(), td.getImplements());
		for (ObjectTypeDefinition td : dataTypeDefinitions)
			assignSuperTypes(td.getName(), td.getImplements());

		for (UnionTypeDefinition td : unionTypeDefinitions)
			assignSubTypes(td.getName(), td.getMemberTypes());

		GmEntityType inputTypeType = apiBuilder.getType(GraphQlInputType.T.getTypeSignature());
		for (InputObjectTypeDefinition td : inputObjectTypeDefinitions) {
			GmEntityType subType = getSchemaGmType(td.getName());
			subType.getSuperTypes().add(inputTypeType);
		}
	}

	@SuppressWarnings("rawtypes")
	private void assignSuperTypes(String simpleName, List<Type> supers) {
		GmEntityType subType = getSchemaGmType(simpleName);
		List<GmEntityType> superTypes = subType.getSuperTypes();

		for (Type sup : supers) {
			GmEntityType superType = getSchemaGmType(sup);
			superTypes.add(superType);
			schemaTypeToSubType.get(superType).add(subType);
		}
	}

	@SuppressWarnings("rawtypes")
	private void assignSubTypes(String simpleName, List<Type> subs) {
		GmEntityType superType = getSchemaGmType(simpleName);

		for (Type sub : subs) {
			GmEntityType subType = getSchemaGmType(sub);
			subType.getSuperTypes().add(superType);
			schemaTypeToSubType.get(superType).add(subType);
		}
	}

	// ##############################################
	// ## . . . . . . . Properties . . . . . . . . ##
	// ##############################################

	private void createProperties() {
		createProperties(dataTypeDefinitions);
		createProperties(interfaceTypeDefinitions);

		for (InputObjectTypeDefinition td : inputObjectTypeDefinitions) {
			GmEntityTypeBuilder builder = schemaTypeEntityBuilders.get(td.getName());
			createInputProperties(builder, td.getInputValueDefinitions());
		}
	}

	private void createProperties(List<? extends ImplementingTypeDefinition<?>> tds) {
		for (ImplementingTypeDefinition<?> td : tds)
			createProperties(td.getName(), td.getFieldDefinitions());
	}

	private void createProperties(String simpleName, List<FieldDefinition> fds) {
		GmEntityTypeBuilder builder = schemaTypeEntityBuilders.get(simpleName);

		for (FieldDefinition fd : fds)
			createProperty(builder, fd.getName(), fd.getType());
	}

	private void createInputProperties(GmEntityTypeBuilder builder, List<InputValueDefinition> ivds) {
		for (InputValueDefinition fd : ivds) {
			GmPropertyBuilder propBuilder = createProperty(builder, fd.getName(), fd.getType());

			Object initialier = resolveInitializer(propBuilder, fd.getDefaultValue());
			propBuilder.it.setInitializer(initialier);
		}
	}

	private GmPropertyBuilder createProperty(GmEntityTypeBuilder builder, String name, Type<?> propType) {
		GmType gmType = resolveGmType(propType);
		String propName = sanitizePropertyName(name);
		GmPropertyBuilder propBuilder = builder.createProperty(propName, gmType);

		if (propType instanceof NonNullType)
			propBuilder.markMandatory();

		return propBuilder;
	}

	private Object resolveInitializer(GmPropertyBuilder propBuilder, Value<?> defaultValue) {
		if (defaultValue == null)
			return null;

		if (defaultValue instanceof ScalarValue) {
			if (defaultValue instanceof StringValue sv)
				return sv.getValue();
			else if (defaultValue instanceof BooleanValue bv)
				return bv.isValue();
			else if (defaultValue instanceof IntValue iv)
				return iv.getValue().intValue();
			else if (defaultValue instanceof FloatValue fv)
				return fv.getValue().doubleValue();
		}

		if (defaultValue instanceof EnumValue ev) {
			GmType propGmType = propBuilder.it.getType();

			EnumReference result = EnumReference.T.createPlain();
			result.setTypeSignature(propGmType.getTypeSignature());
			result.setConstant(ev.getName());

			return result;
		}

		throw new IllegalArgumentException("Unknown default value: " + defaultValue);
	}

	// ##############################################
	// ## . . . . . . . . Helpers . . . . . . . . .##
	// ##############################################

	private void createFieldArgs() {
		createFieldArgs(dataTypeDefinitions);
		createFieldArgs(interfaceTypeDefinitions);
	}

	private void createFieldArgs(List<? extends ImplementingTypeDefinition<?>> tds) {
		for (ImplementingTypeDefinition<?> td : tds)
			createFieldArgs(td.getName(), td.getFieldDefinitions());
	}

	private void createFieldArgs(String simpleName, List<FieldDefinition> fds) {
		List<FieldDefinition> fdsWithArgs = fds.stream() //
				.filter(fd -> !fd.getInputValueDefinitions().isEmpty()) //
				.collect(Collectors.toList());

		if (fdsWithArgs.isEmpty())
			return;

		GmEntityType schemaGmType = getSchemaGmType(simpleName);

		GmEntityTypeBuilder hasFaBuilder = ensureHasFaBuilder(schemaGmType);

		for (FieldDefinition fd : fdsWithArgs)
			createdFieldArgProperty(simpleName, hasFaBuilder, fd);
	}

	private GmEntityTypeBuilder ensureHasFaBuilder(GmEntityType schemaGmType) {
		GmEntityTypeBuilder hasFaBuilder = schemaTypeToFaBuilder.get(schemaGmType);
		if (hasFaBuilder != null)
			return hasFaBuilder;

		String faSimpleName = getSimpleName(schemaGmType) + "_withArgs";

		hasFaBuilder = createFieldArgsTypeStub(faSimpleName, schemaGmType.getIsAbstract());
		hasFaBuilder.it.getSuperTypes().add(schemaGmType);

		hasFaTypes.add(hasFaBuilder.it);

		for (GmEntityType subType : schemaTypeToSubType.get(schemaGmType))
			ensureHasFaBuilder(subType).addSuperType(hasFaBuilder.it);

		return hasFaBuilder;
	}

	private void createdFieldArgProperty(String simpleName, GmEntityTypeBuilder hasFaBuilder, FieldDefinition fd) {
		String propNamePlusArgs = fd.getName() + "_" + "args";
		// see Class Javadoc for underscores
		String propertyName = propNamePlusArgs + "_";
		String faSimpleName = simpleName + "_" + propNamePlusArgs;

		GmEntityTypeBuilder faBuilder = createFieldArgsTypeStub(faSimpleName, false);
		faBuilder.it.getSuperTypes().add(apiBuilder.getType(GraphQlFieldArguments.T));

		createInputProperties(faBuilder, fd.getInputValueDefinitions());

		hasFaBuilder.createProperty(propertyName, faBuilder.it);
	}

	private void finalizerFieldArgsSuperTypes() {
		GmEntityType hasFaGmType = apiBuilder.getType(HasGraphQlFieldArguments.T);

		for (GmEntityType hasFaType : hasFaTypes)
			if (hasFaType.getSuperTypes().size() == 1)
				hasFaType.getSuperTypes().add(hasFaGmType);
	}

	// ##############################################
	// ## . . . . . . . . Helpers . . . . . . . . .##
	// ##############################################

	private String sanitizePropertyName(String name) {
		if (GE_PROPERTY_NAMES.contains(name) || RESERVED_PROPERTY_NAMES.contains(name) || name.endsWith("_"))
			return name + "_";
		else
			return name;
	}

	private GmType resolveGmType(Type<?> type) {
		if (type instanceof NonNullType nnt)
			type = nnt.getType();

		GmType gmType = gqlTypeToGmType.get(type);
		if (gmType != null)
			return gmType;

		if (type instanceof ListType lt)
			gmType = resolveGmListType(lt);
		else
			gmType = getSchemaGmType(((TypeName) type).getName());

		gqlTypeToGmType.put(type, gmType);
		return gmType;
	}

	private GmType resolveGmListType(ListType lt) {
		GmType elementType = resolveGmType(lt.getType());

		String typeSignature = "list<" + elementType.getTypeSignature() + ">";

		GmListType listType = GmListType.T.create();
		listType.setElementType(elementType);
		listType.setTypeSignature(typeSignature);
		listType.setGlobalId("type:" + typeSignature);
		listType.setDeclaringModel(elementType.getDeclaringModel());

		return listType;
	}

	@SuppressWarnings("rawtypes")
	private <T extends GmType> T getSchemaGmType(Type graphQlType) {
		if (graphQlType instanceof TypeName tn)
			return getSchemaGmType(tn.getName());

		throw new IllegalArgumentException("Only TypeName was expected, not " + graphQlType);
	}

	private <T extends GmType> T getSchemaGmType(String simpleName) {
		GmType type = schemaTypes.get(simpleName);
		if (type == null)
			throw new NullPointerException("Type not found: " + simpleName);

		return (T) type;
	}

	private enum ReqType {
		query,
		mutation,
		subscription
	}

	private static String getSimpleName(GmType gmType) {
		return StringTools.getSubstringAfterLast(gmType.getTypeSignature(), ".");
	}

}
