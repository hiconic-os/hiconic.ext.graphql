package hiconic.ext.graphql.schema.gm;

import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_INTEGER;
import static com.braintribe.model.generic.reflection.SimpleTypes.TYPE_STRING;

import org.junit.Test;

import hiconic.ext.graphql.api.model.GraphQlFieldArguments;
import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;

/**
 * Tests for {@link GraphQlSchemaToGmModel}
 * 
 * @author peter.gazdik
 */
public class GraphQlToGmModel_FieldArgsTest extends AbstractGraphQlSchemaToGmModelTest {

	private static final String GraphQlFieldArgumentsSIG = GraphQlFieldArguments.T.getTypeSignature();
	private static final String HasGraphQlFieldArgumentsSIG = HasGraphQlFieldArguments.T.getTypeSignature();

	// Data
	private static final String TypeWithFaSIG = dataSig("TypeWithFa");
	private static final String TypeWithFaSIG_withArgsSIG = fieldArgsSig("TypeWithFa_withArgs");
	private static final String TypeWithFa_name_argsSIG = fieldArgsSig("TypeWithFa_name_args");

	private static final String ISuperWithFaSIG = dataSig("ISuperWithFa");
	private static final String ISuperWithFa_withArgsSIG = fieldArgsSig("ISuperWithFa_withArgs");
	private static final String ISuperWithFa_superName_argsSIG = fieldArgsSig("ISuperWithFa_superName_args");

	private static final String IWithFaSIG = dataSig("IWithFa");
	private static final String IWithFa_withArgsSIG = fieldArgsSig("IWithFa_withArgs");

	private static final String TypeWithInheritedFaSIG = dataSig("TypeWithInheritedFa");
	private static final String TypeWithInheritedFa_withArgsSIG = fieldArgsSig("TypeWithInheritedFa_withArgs");

	// Requests

	private static final String WithFaByIdRequestSIG = queryReqSig("WithFaByIdRequest");
	private static final String WithInheritedFaByNameRequestSIG = queryReqSig("WithInheritedFaByNameRequest");

	@Test
	public void testSchemaWithFieldArgs() throws Exception {
		parseAndConvert("field-args.graphql");

		assertWithFaByIdRequest();
		assertWithInheritedFaByNameRequest();

	}

	private void assertWithFaByIdRequest() {
		loadEntityType(WithFaByIdRequestSIG, false, BaseQueryRequestSIG);

		loadEntityType(TypeWithFaSIG, false);
		assertProperty("name", TYPE_STRING, false);

		loadEntityType(TypeWithFaSIG_withArgsSIG, false, TypeWithFaSIG, HasGraphQlFieldArgumentsSIG);
		assertProperty("name_args_", TypeWithFa_name_argsSIG, false);

		loadEntityType(TypeWithFa_name_argsSIG, false, GraphQlFieldArgumentsSIG);
		assertProperty("number", TYPE_INTEGER, false);
	}

	private void assertWithInheritedFaByNameRequest() {
		loadEntityType(WithInheritedFaByNameRequestSIG, false, BaseQueryRequestSIG);
		assertEvalsToGqlResult();

		loadEntityType(ISuperWithFaSIG, true);
		assertProperty("superName", TYPE_STRING, false);

		loadEntityType(ISuperWithFa_withArgsSIG, true, ISuperWithFaSIG, HasGraphQlFieldArgumentsSIG);
		assertProperty("superName_args_", ISuperWithFa_superName_argsSIG, false);

		loadEntityType(ISuperWithFa_superName_argsSIG, false, GraphQlFieldArgumentsSIG);
		assertProperty("superNumber", TYPE_INTEGER, false);

		loadEntityType(IWithFaSIG, true, ISuperWithFaSIG);
		loadEntityType(IWithFa_withArgsSIG, true, IWithFaSIG, ISuperWithFa_withArgsSIG);

		loadEntityType(TypeWithInheritedFaSIG, false, IWithFaSIG);
		loadEntityType(TypeWithInheritedFa_withArgsSIG, false, TypeWithInheritedFaSIG, IWithFa_withArgsSIG);
	}

}
