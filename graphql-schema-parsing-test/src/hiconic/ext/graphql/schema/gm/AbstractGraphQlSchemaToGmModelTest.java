package hiconic.ext.graphql.schema.gm;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.testing.junit.assertions.gm.assertj.core.api.GmAssertions.assertThat;
import static com.braintribe.utils.lcd.CollectionTools2.index;
import static com.braintribe.utils.lcd.CollectionTools2.newMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.braintribe.gm._RootModel_;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.value.EnumReference;
import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmEnumConstant;
import com.braintribe.model.meta.GmEnumType;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.data.constraint.Mandatory;
import com.braintribe.model.processing.itw.analysis.JavaTypeAnalysis;
import com.braintribe.testing.junit.assertions.assertj.core.api.ExtendedStringAssert;

import graphql.schema.idl.TypeDefinitionRegistry;
import hiconic.ext.graphql.ConvertGraphqlSchemaToModelsProcessor;
import hiconic.ext.graphql._GraphqlApiModel_;
import hiconic.ext.graphql.api.model.GraphQlInputType;
import hiconic.ext.graphql.api.model.GraphQlMutationRequest;
import hiconic.ext.graphql.api.model.GraphQlQueryRequest;
import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.api.model.HasGraphQlTypeConditions;
import hiconic.ext.graphql.api.model.result.GraphQlResult;

/**
 * Tests for {@link GraphQlSchemaToGmModel}
 * 
 * @author peter.gazdik
 */
public abstract class AbstractGraphQlSchemaToGmModelTest {

	private static final String MODEL_GROUP_ID = "hiconic.ext.graphql";
	private static final String MODEL_ARTIFACT_ID_BASE = "graphql-schema-test";
	private static final String MODEL_ARTIFACT_ID_API = MODEL_ARTIFACT_ID_BASE + "-api-model";
	private static final String MODEL_ARTIFACT_ID_DATA = MODEL_ARTIFACT_ID_BASE + "-data-model";
	private static final String MODEL_VERSION = "1.42";

	private static final String MODEL_PACKAGE_BASE = "hiconic.ext.graphql.schema.model";
	private static final String API_PACKAGE_BASE = MODEL_PACKAGE_BASE + ".api.";
	private static final String INPUT_PACKAGE_BASE = API_PACKAGE_BASE + "input.";
	private static final String FIELD_ARGS_PACKAGE_BASE = API_PACKAGE_BASE + "args.";
	private static final String TYPE_CONDITIONS_PACKAGE_BASE = API_PACKAGE_BASE + "typeconditions.";
	private static final String QUERY_PACKAGE_BASE = API_PACKAGE_BASE + "query.";
	private static final String MUTATION_PACKAGE_BASE = API_PACKAGE_BASE + "mutation.";
	private static final String DATA_PACKAGE_BASE = MODEL_PACKAGE_BASE + ".data.";

	protected static final String GraphQlResultSIG = GraphQlResult.T.getTypeSignature();
	protected static final String GraphQlRequestSIG = GraphQlRequest.T.getTypeSignature();
	protected static final String GraphQlQueryRequestSIG = GraphQlQueryRequest.T.getTypeSignature();
	protected static final String GraphQlMutationRequestSIG = GraphQlMutationRequest.T.getTypeSignature();
	protected static final String GraphQlInputTypeSIG = GraphQlInputType.T.getTypeSignature();
	protected static final String HasGraphQlTypeConditionsSIG = HasGraphQlTypeConditions.T.getTypeSignature();

	protected static final String BaseRequestSIG = apiSig("GraphqlSchemaTestRequest");
	protected static final String BaseQueryRequestSIG = queryReqSig("GraphqlSchemaTestQueryRequest");
	protected static final String BaseMutationRequestSIG = mutationReqSig("GraphqlSchemaTestMutationRequest");

	protected final Map<String, GmType> gmTypes = newMap();

	protected GmEntityType entityType;
	protected Map<String, GmProperty> properties;

	protected void checkEnum(String typeSignature, String... expectedNames) {
		GmEnumType enumType = loadType(typeSignature, GmEnumType.T);

		for (GmEnumConstant ec : enumType.getConstants())
			assertThat(ec.getDeclaringType()).isSameAs(enumType);

		Set<String> actualNames = enumType.getConstants().stream() //
				.map(GmEnumConstant::getName) //
				.collect(Collectors.toSet());

		assertThat(actualNames).containsExactly(expectedNames);
	}

	protected void checkNoType(String shortName) {
		String suffix = "." + shortName;
		for (GmType gmType : gmTypes.values())
			assertThat(gmType.getTypeSignature()).doesNotEndWith(suffix);
	}

	protected void loadEntityType(String typeSignature, boolean isAbstract, String... superTypes) {
		entityType = loadType(typeSignature, GmEntityType.T);
		assertThat(entityType.getIsAbstract()).as(() -> "Wrong abstract falg for: " + typeSignature).isEqualTo(isAbstract);

		Set<String> actualSuperTypes = entityType.getSuperTypes().stream() //
				.map(GmType::getTypeSignature) //
				.collect(Collectors.toSet());

		if (superTypes.length == 0)
			superTypes = new String[] { GenericEntity.T.getTypeSignature() };

		assertThat(actualSuperTypes).as("Wrong supertypes of: " + typeSignature).containsExactlyInAnyOrder(superTypes);

		properties = index(entityType.getProperties()) //
				.by(GmProperty::getName) //
				.unique();
	}

	protected void assertProperty(String name, GenericModelType expectedType, boolean mandatory) {
		assertProperty(name, expectedType.getTypeSignature(), mandatory, null);
	}

	protected void assertProperty(String name, String expectedTs, boolean mandatory) {
		assertProperty(name, expectedTs, mandatory, null);
	}

	protected void assertInitProperty(String name, GenericModelType expectedType, Object initializer) {
		assertProperty(name, expectedType.getTypeSignature(), false, initializer);
	}

	protected void assertEnumInitProperty(String name, String expectedTs, String constantName) {
		GmProperty property = assertPropertyBase(name, expectedTs, false);
		assertThat(property.getInitializer()).isInstanceOf(EnumReference.class);

		EnumReference ref = (EnumReference) property.getInitializer();
		assertThat(ref.getTypeSignature()).isEqualTo(expectedTs);
		assertThat(ref.getConstant()).isEqualTo(constantName);
	}

	protected void assertProperty(String name, String expectedTs, boolean mandatory, Object initializer) {
		GmProperty property = assertPropertyBase(name, expectedTs, mandatory);
		assertThat(property.getInitializer()).isEqualTo(initializer);
	}

	private GmProperty assertPropertyBase(String name, String expectedTs, boolean mandatory) {
		String propertyFullName = entityType.getTypeSignature() + "#" + name;

		GmProperty property = properties.get(name);

		assertThat(property).as(() -> "Propert not found: " + propertyFullName).isNotNull();
		assertThat(property.getDeclaringType()).isSameAs(entityType);

		GmType type = property.getType();
		assertThat(type).isNotNull();
		assertThat(type.getTypeSignature()).isEqualTo(expectedTs);

		assertThat(isMandatory(property)).as(() -> "Property expected to be mandatory: " + propertyFullName).isEqualTo(mandatory);

		return property;
	}

	private boolean isMandatory(GmProperty property) {
		return property.getMetaData().stream() //
				.filter(md -> md instanceof Mandatory) //
				.findAny() //
				.isPresent();
	}

	protected void assertEvalsToAndSelect(String expectedSig) {
		assertEvalsTo(expectedSig);
		assertProperty("select", expectedSig, true);
	}

	protected void assertSelect(String expectedSig) {
		assertProperty("select", expectedSig, true);
	}

	protected void assertEvalsTo(String expectedSig) {
		GmType evaluatesTo = entityType.getEvaluatesTo();
		assertThat(evaluatesTo).isNotNull();

		assertThat(evaluatesTo.getTypeSignature()).as("Wrong evaluatesTo for: " + entityType.getTypeSignature()).isEqualTo(expectedSig);
	}

	protected <T extends GmType> T loadType(String typeSignature, EntityType<T> typeType) {
		assertThat(gmTypes.keySet()).contains(typeSignature);

		GmType gmType = gmTypes.get(typeSignature);
		assertThat(gmType) //
				.as(() -> "Wrong type for " + typeType.getShortName() + ": " + typeSignature) //
				.isInstanceOf(typeType);

		return (T) gmType;
	}

	protected void parseAndConvert(String fileName) {
		TypeDefinitionRegistry tdr = parse(new File("res/" + fileName));

		GmModels models = GraphQlSchemaToGmModel.createModels( //
				MODEL_ARTIFACT_ID_BASE, MODEL_GROUP_ID, MODEL_VERSION, //
				MODEL_PACKAGE_BASE, //
				tdr);

		verify(models.apiModel(), MODEL_ARTIFACT_ID_API, modelName(MODEL_ARTIFACT_ID_DATA), _GraphqlApiModel_.reflection.name());

		verify(models.dataModel(), MODEL_ARTIFACT_ID_DATA, _RootModel_.reflection.name());

		indexTypes(models.dataModel(), DATA_PACKAGE_BASE);
		indexTypes(models.apiModel(), API_PACKAGE_BASE);
	}

	public static TypeDefinitionRegistry parse(File schemaFile) {
		return ConvertGraphqlSchemaToModelsProcessor.parse(schemaFile);
	}

	private void verify(GmMetaModel model, String artifactId, String... deps) {
		assertThat(model).isNotNull();
		assertThat(model.getName()).isEqualTo(modelName(artifactId));
		assertThat(model.getVersion()).isEqualTo(MODEL_VERSION);

		assertThat(model.getDependencies()).hasSize(deps.length);

		Set<String> depNames = model.getDependencies().stream() //
				.map(GmMetaModel::getName) //
				.collect(Collectors.toSet());

		assertThat(depNames).containsExactlyInAnyOrder(deps);

		assertThat(model.getTypeOverrides()).isEmpty();
	}

	private void indexTypes(GmMetaModel model, String packageBase) {
		for (GmType gmType : model.getTypes()) {
			assertThat(gmType.getDeclaringModel()).isSameAs(model);
			assertThat(gmType.getTypeSignature()).startsWith(packageBase);
			assertGmTypeGlobalId(gmType);

			gmTypes.put(gmType.getTypeSignature(), gmType);
		}
	}

	private ExtendedStringAssert assertGmTypeGlobalId(GmType gmType) {
		return assertThat(gmType.getGlobalId()).isEqualTo(JavaTypeAnalysis.typeGlobalId(gmType.getTypeSignature()));
	}

	private static String modelName(String artifactId) {
		return MODEL_GROUP_ID + ":" + artifactId;
	}

	protected static String apiSig(String simpleName) {
		return API_PACKAGE_BASE + simpleName;
	}

	protected static String apiTypesSig(String simpleName) {
		return INPUT_PACKAGE_BASE + simpleName;
	}

	protected static String queryReqSig(String simpleName) {
		return QUERY_PACKAGE_BASE + simpleName;
	}

	protected static String mutationReqSig(String simpleName) {
		return MUTATION_PACKAGE_BASE + simpleName;
	}

	protected static String dataSig(String simpleName) {
		return DATA_PACKAGE_BASE + simpleName;
	}

	protected static String fieldArgsSig(String simpleName) {
		return FIELD_ARGS_PACKAGE_BASE + simpleName;
	}

	protected static String typeConditionsSig(String simpleName) {
		return TYPE_CONDITIONS_PACKAGE_BASE + simpleName;
	}
	
}
