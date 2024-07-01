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
package hiconic.ext.graphql.schema.gm;

import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_BOOLEAN;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_DOUBLE;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_INTEGER;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_STRING;

import org.junit.Test;

import com.braintribe.model.generic.GMF;

import hiconic.ext.graphql.api.model.HasGraphQlTypeConditions;

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
	private static final String List_Of_QueryResultTypeSIG = "list<" + dataSig("QueryResult") + ">";
	private static final String MutationResultTypeSIG = dataSig("MutationResult");
	private static final String JoatTypeSIG = dataSig("JoatType");
	private static final String UnderscorePropsTypeSIG = dataSig("UnderscorePropsType");

	// API
	private static final String ApiEnumSIG = apiTypesSig("ApiEnum");

	private static final String InputTypeSIG = apiTypesSig("InputType");
	private static final String UnderscorePropsInputTypeSIG = apiTypesSig("UnderscorePropsInputType");

	private static final String UnionType_TypeConditionsSIG = typeConditionsSig("UnionType_TypeConditions");
	private static final String SuperIface_TypeConditionsSIG = typeConditionsSig("SuperIface_TypeConditions");

	// Requests

	private static final String QueryRequestSIG = queryReqSig("QueryRequest");
	private static final String QueryByIdRequestSIG = queryReqSig("QueryByIdRequest");
	private static final String QueryByIdWithDefaultParamRequestSIG = queryReqSig("QueryByIdWithDefaultParamRequest");
	private static final String QueryByInputRequestSIG = queryReqSig("QueryByInputRequest");
	private static final String QueryByReservedWordsSIG = queryReqSig("QueryByReservedWordsRequest");
	private static final String QueryThatReturnsListSIG = queryReqSig("QueryThatReturnsListRequest");

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
		checkTypeConditionTypes();
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

	private void checkTypeConditionTypes() {
		loadEntityType(UnionType_TypeConditionsSIG, false, UnionTypeSIG, HasGraphQlTypeConditionsSIG);
		assertProperty(HasGraphQlTypeConditions.TYPE_CONDITIONS_PROPERTY_NAME, "list<" + UnionTypeSIG + ">", false);

		loadEntityType(SuperIface_TypeConditionsSIG, false, SuperIfaceSIG, HasGraphQlTypeConditionsSIG);
		assertProperty(HasGraphQlTypeConditions.TYPE_CONDITIONS_PROPERTY_NAME, "list<" + SuperIfaceSIG + ">", false);
	}

	private void checkRequestTypes() {
		loadEntityType(BaseRequestSIG, true, GraphQlRequestSIG);
		loadEntityType(BaseQueryRequestSIG, true, BaseRequestSIG, GraphQlQueryRequestSIG);
		loadEntityType(BaseMutationRequestSIG, true, BaseRequestSIG, GraphQlMutationRequestSIG);

		loadEntityType(QueryRequestSIG, false, BaseQueryRequestSIG);
		assertEvalsToAndSelect(QueryResultTypeSIG);

		loadEntityType(QueryByIdRequestSIG, false, BaseQueryRequestSIG);
		assertProperty("id_", TYPE_STRING, true);
		assertEvalsToAndSelect(QueryResultTypeSIG);

		loadEntityType(QueryByInputRequestSIG, false, BaseQueryRequestSIG);
		assertProperty("in", InputTypeSIG, true);
		assertEvalsToAndSelect(QueryResultTypeSIG);

		loadEntityType(QueryByIdWithDefaultParamRequestSIG, false, BaseQueryRequestSIG);
		assertProperty("id_", TYPE_STRING, true);
		assertInitProperty("number", TYPE_INTEGER, 100);
		assertEvalsToAndSelect(QueryResultTypeSIG);

		loadEntityType(QueryByReservedWordsSIG, false, BaseQueryRequestSIG);
		assertProperty("select_", TYPE_STRING, false);
		assertProperty("domainId_", TYPE_STRING, false);
		assertEvalsToAndSelect(QueryResultTypeSIG);

		loadEntityType(QueryThatReturnsListSIG, false, BaseQueryRequestSIG);
		assertSelect(QueryResultTypeSIG);
		assertEvalsTo(List_Of_QueryResultTypeSIG);

		loadEntityType(MutateRequestSIG, false, BaseMutationRequestSIG);
		assertEvalsToAndSelect(MutationResultTypeSIG);
	}

}
