package hiconic.ext.graphql;

import static com.braintribe.testing.junit.assertions.assertj.core.api.Assertions.assertThat;
import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.test.model.gm.countries.api.args.Continent_name_args;
import hiconic.ext.graphql.test.model.gm.countries.api.args.Continent_withArgs;
import hiconic.ext.graphql.test.model.gm.countries.api.args.Country_name_args;
import hiconic.ext.graphql.test.model.gm.countries.api.args.Country_withArgs;
import hiconic.ext.graphql.test.model.gm.countries.api.input.ContinentFilterInput;
import hiconic.ext.graphql.test.model.gm.countries.api.input.StringQueryOperatorInput;
import hiconic.ext.graphql.test.model.gm.countries.api.query.ContinentsRequest;
import hiconic.ext.graphql.test.model.gm.countries.api.query.LanguageRequest;
import hiconic.ext.graphql.test.model.gm.countries.api.query.UnderscorePropsRequest;
import hiconic.ext.graphql.test.model.gm.countries.api.typeconditions.HasNameAndCode_TypeConditions;
import hiconic.ext.graphql.test.model.gm.countries.data.Continent;
import hiconic.ext.graphql.test.model.gm.countries.data.Country;
import hiconic.ext.graphql.test.model.gm.countries.data.DataTypeWithUnderscores;
import hiconic.ext.graphql.test.model.gm.countries.data.Language;

/**
 * Tests for {@link GraphQlRequestMarshaller}.
 * 
 * Test entities were taken based on an online example.
 * 
 * @see "https://lucasconstantino.github.io/graphiql-online/"
 * @see "https://countries.trevorblades.com/"
 */
public class GraphQlRequestMarshallerTest {

	private final GraphQlRequestMarshaller marshaller = new GraphQlRequestMarshaller();
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	@Rule
	public TestName testName = new TestName();

	@Test
	public void testSimpleQuery() {
		Language select = Language.T.create();
		select.setNative(""); // activate output

		LanguageRequest query = LanguageRequest.T.create();
		query.setSelect(select);
		query.setCode("la"); // Latina
		query.setSessionId(""); // should be ignored

		// @formatter:off
		assertMarshallsQuery(query, 
				"value: language(code: \"la\") {", 
					"native", 
				"}");
		// @formatter:on
	}

	@Test
	public void testQueryWithArguments() {
		Country c1 = Country.T.create();
		c1.setName("");

		Continent continent = Continent.T.create();
		continent.setCountries(asList(c1)); // activate output

		StringQueryOperatorInput operation = StringQueryOperatorInput.T.create();
		operation.setEq("SA"); // South America

		ContinentFilterInput filter = ContinentFilterInput.T.create();
		filter.setCode(operation);

		ContinentsRequest query = ContinentsRequest.T.create();
		query.setSelect(continent);
		query.setFilter(filter);

		// @formatter:off
		assertMarshallsQuery(query, 
				"value: continents(filter: {code: {eq: \"SA\"}}) {", 
					"countries {", 
						"name", 
					"}", 
				"}");
		// @formatter:on
	}

	@Test
	public void testQueryWithArguments_List() {
		Country c1 = Country.T.create();
		c1.setName("");

		Continent continent = Continent.T.create();
		continent.setCountries(asList(c1)); // activate output

		StringQueryOperatorInput operation = StringQueryOperatorInput.T.create();
		operation.setIn(asList("SA", "NA")); // South America

		ContinentFilterInput filter = ContinentFilterInput.T.create();
		filter.setCode(operation);

		ContinentsRequest query = ContinentsRequest.T.create();
		query.setFilter(filter);
		query.setSelect(continent);

		// @formatter:off
		assertMarshallsQuery(query, 
				"value: continents(filter: {code: {in: [\"SA\", \"NA\"]}}) {", 
					"countries {", "name", "}", 
				"}");
		// @formatter:on
	}

	@Test
	public void testQueryWithFieldArgs() {
		Country_name_args country_name_args = Country_name_args.T.create();
		country_name_args.setLang("de"); // Germany

		Country_withArgs c1 = Country_withArgs.T.create();
		c1.setName_args_(country_name_args);
		c1.setName("");

		Continent_name_args continent_name_args = Continent_name_args.T.create();
		continent_name_args.setLang("de"); // Germany

		Continent_withArgs continent = Continent_withArgs.T.create();
		continent.setName_args_(continent_name_args);
		continent.setName("");
		continent.setCountries(asList(c1)); // activate output

		ContinentsRequest query = ContinentsRequest.T.create();
		query.setSelect(continent);

		// This query actually works on https://countries.trevorblades.com/
		// The "lang" argument controls the language in which the name is returned
		// @formatter:off
		assertMarshallsQuery(query, 
				"value: continents {", 
				"countries {", 
					"name(lang: \"de\")", 
				"}", 
				"name(lang: \"de\")", "}");
		// @formatter:on
	}

	@Test
	public void testQueryUnderscoreProps() {
		DataTypeWithUnderscores d = DataTypeWithUnderscores.T.create();
		d.setId_("");
		d.setAge__(0);

		UnderscorePropsRequest query = UnderscorePropsRequest.T.create();
		query.setId_("25");
		query.setName__("John");
		query.setSelect(d);

		// @formatter:off
		assertMarshallsQuery(query, 
				"value: underscoreProps(id: \"25\", name_: \"John\") {", 
					"age_", 
					"id", 
				"}");
		// @formatter:on
	}

	// With type condition
	// query{value:language(code:"la"){...on Language{native} ...on Language{name}}}

	@Test
	public void testSimpleQuery_withTypeCondition() {
		Language languageTc1 = Language.T.create();
		languageTc1.setNative("");

		Language languageTc2 = Language.T.create();
		languageTc2.setRtl(true);

		HasNameAndCode_TypeConditions select = HasNameAndCode_TypeConditions.T.create();
		select.setName("");
		select.getTypeConditions_().add(languageTc1);
		select.getTypeConditions_().add(languageTc2);

		LanguageRequest query = LanguageRequest.T.create();
		query.setSelect(select);
		query.setCode("la"); // Latina

		// @formatter:off
		assertMarshallsQuery(query, //
				"value: language(code: \"la\") {",
				    "name",
				    "... on Language {", 
				        "native", 
				    "}", 
				    "... on Language {", 
				        "rtl", 
				    "}", 
				"}");
		// @formatter:on
	}

	private void assertMarshallsQuery(GraphQlRequest query, String... expectedLines2) {
		String queryQl = marshall(query);

		final String prefix = "{\"query\": \"";
		final String suffix = "\"}";
		assertThat(queryQl).startsWith(prefix).endsWith(suffix);

		String pureQuery = queryQl.substring(prefix.length(), queryQl.length() - suffix.length());

		System.out.println("--" + testName.getMethodName() + "--");
		System.out.println(pureQuery.replace("\\\"", "\"").replace("\\n", "\n"));

		String[] actualLines = pureQuery.replace("\\\"", "\"").split("\\\\n");

		String[] expectedLines = new String[expectedLines2.length + 2];
		expectedLines[0] = "query {";
		expectedLines[expectedLines.length - 1] = "}";
		System.arraycopy(expectedLines2, 0, expectedLines, 1, expectedLines2.length);

		Assertions.assertThat(actualLines).hasSize(expectedLines.length);

		for (int i = 0; i < actualLines.length; i++) {
			String expectedLine = expectedLines[i];
			String actualLine = actualLines[i];

			Assertions.assertThat(actualLine.trim()).as("Error on line: " + (i + 1)).isEqualTo(expectedLine.trim());
		}
	}

	private String marshall(GraphQlRequest query) {
		marshaller.marshall(out, query);
		return out.toString(StandardCharsets.UTF_8);
	}

}
