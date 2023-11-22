package hiconic.ext.graphql.schema.gm;

import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_BOOLEAN;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_DOUBLE;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_INTEGER;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_STRING;

import org.junit.Test;

import com.braintribe.model.generic.GMF;

/**
 * Tests for {@link GraphQlSchemaToGmModel}
 * 
 * @author peter.gazdik
 */
public class GraphQlToGmModel_BasicTest extends AbstractGraphQlSchemaToGmModelTest {

	private static final String DataEnumSIG = dataSig("DataEnum");

	private static final String SuperIfaceSIG = dataSig("SuperIface");
	private static final String IfaceSIG = dataSig("Iface");
	private static final String IfaceTypeSIG = dataSig("IfaceType");

	private static final String TypeU1SIG = dataSig("TypeU1");
	private static final String TypeU2SIG = dataSig("TypeU2");
	private static final String UnionTypeSIG = dataSig("UnionType");

	private static final String QueryResultTypeSIG = dataSig("QueryResult");
	private static final String MutationResultTypeSIG = dataSig("MutationResult");
	private static final String JoatTypeSIG = dataSig("JoatType");
	private static final String UnderscorePropsTypeSIG = dataSig("UnderscorePropsType");

	// API
	private static final String ApiEnumSIG = apiTypesSig("ApiEnum");

	private static final String InputTypeSIG = apiTypesSig("InputType");
	private static final String UnderscorePropsInputTypeSIG = apiTypesSig("UnderscorePropsInputType");

	// Requests

	private static final String QueryRequestSIG = queryReqSig("QueryRequest");
	private static final String QueryByIdRequestSIG = queryReqSig("QueryByIdRequest");
	private static final String QueryByIdWithDefaultParamRequestSIG = queryReqSig("QueryByIdWithDefaultParamRequest");
	private static final String QueryByInputRequestSIG = queryReqSig("QueryByInputRequest");
	private static final String QueryByReservedWordsSIG = queryReqSig("QueryByReservedWordsRequest");

	private static final String MutateRequestSIG = mutationReqSig("MutateRequest");

	@Test
	public void testBasicSchema() throws Exception {
		parseAndConvert("basic-schema.graphql");

		checkEnum(ApiEnumSIG, "read", "write");
		checkEnum(DataEnumSIG, "red", "green", "blue");

		checkNoType("Query");
		checkNoType("Mutation");
		checkNoType("Subscription");

		checkDataTypes();
		checkInputTypes();
		checkRequestTypes();
	}

	private void checkDataTypes() {
		loadEntityType(JoatTypeSIG, false);
		assertProperty("int", TYPE_INTEGER, true);
		assertProperty("float", TYPE_DOUBLE, true);
		assertProperty("string", TYPE_STRING, true);
		assertProperty("boolean", TYPE_BOOLEAN, true);
		assertProperty("intList", GMF.getTypeReflection().getListType(TYPE_INTEGER), false);
		assertProperty("dataEnum", DataEnumSIG, false);
		assertProperty("self", JoatTypeSIG, false);
		assertProperty("currency", TYPE_STRING, false);

		loadEntityType(UnderscorePropsTypeSIG, false);
		assertUnderscoreProps();

		loadEntityType(SuperIfaceSIG, true);
		assertProperty("superIfaceName", TYPE_STRING, false);

		loadEntityType(IfaceSIG, true, SuperIfaceSIG);
		assertProperty("ifaceName", TYPE_STRING, false);

		loadEntityType(IfaceTypeSIG, false, IfaceSIG);
		assertProperty("ifaceTypeName", TYPE_STRING, false);

		loadEntityType(TypeU1SIG, false, UnionTypeSIG);
		loadEntityType(TypeU2SIG, false, UnionTypeSIG);
		loadEntityType(UnionTypeSIG, true);
	}

	private void checkInputTypes() {
		loadEntityType(UnderscorePropsInputTypeSIG, false, GraphQlInputTypeSIG);
		assertUnderscoreProps();

		loadEntityType(InputTypeSIG, false, GraphQlInputTypeSIG);
		assertProperty("inputName", TYPE_STRING, false);
		assertInitProperty("stringWithDefault", TYPE_STRING, "Default Name");
		assertInitProperty("booleanWithDefault", TYPE_BOOLEAN, Boolean.TRUE);
		assertInitProperty("intWithDefault", TYPE_INTEGER, 50);
		assertInitProperty("floatWithDefault", TYPE_DOUBLE, 123.45d);
		assertEnumInitProperty("enumWithDefault", DataEnumSIG, "green");
	}

	private void assertUnderscoreProps() {
		assertProperty("id_", TYPE_STRING, true);
		assertProperty("globalId_", TYPE_STRING, false);
		assertProperty("partition_", TYPE_STRING, false);
		assertProperty("id__", TYPE_INTEGER, false);
		assertProperty("underscore__", TYPE_INTEGER, false);
	}

	private void checkRequestTypes() {
		loadEntityType(BaseRequestSIG, true, GraphQlRequestSIG);
		loadEntityType(BaseQueryRequestSIG, true, BaseRequestSIG, GraphQlQueryRequestSIG);
		loadEntityType(BaseMutationRequestSIG, true, BaseRequestSIG, GraphQlMutationRequestSIG);

		loadEntityType(QueryRequestSIG, false, BaseQueryRequestSIG);
		assertSelect(QueryResultTypeSIG);

		loadEntityType(QueryByIdRequestSIG, false, BaseQueryRequestSIG);
		assertProperty("id_", TYPE_STRING, true);
		assertSelect(QueryResultTypeSIG);

		loadEntityType(QueryByInputRequestSIG, false, BaseQueryRequestSIG);
		assertProperty("in", InputTypeSIG, true);
		assertSelect(QueryResultTypeSIG);

		loadEntityType(QueryByIdWithDefaultParamRequestSIG, false, BaseQueryRequestSIG);
		assertProperty("id_", TYPE_STRING, true);
		assertInitProperty("number", TYPE_INTEGER, 100);
		assertSelect(QueryResultTypeSIG);

		loadEntityType(QueryByReservedWordsSIG, false, BaseQueryRequestSIG);
		assertProperty("select_", TYPE_STRING, false);
		assertProperty("domainId_", TYPE_STRING, false);
		assertSelect(QueryResultTypeSIG);

		loadEntityType(MutateRequestSIG, false, BaseMutationRequestSIG);
		assertSelect(MutationResultTypeSIG);
		assertEvalsToGqlResult();

	}

}
