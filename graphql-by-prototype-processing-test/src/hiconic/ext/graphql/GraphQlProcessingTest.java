package hiconic.ext.graphql;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import hiconic.ext.graphql.test.model.gm.countries.queries.continents;
import hiconic.ext.graphql.test.model.gm.countries.queries.language;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.Continent;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.ContinentFilterInput;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.Countries_name_args;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.Country;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.Country_withArgs;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.Language;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.StringQueryOperatorInput;

public class GraphQlProcessingTest {

	@Test
	public void testMarshalling() {

		final String expectation = "query{value:language(code:\"la\"){native}}";

		language languageQuery = language.T.create();

		Language select = Language.T.create();
		select.setNative(""); // activate output

		languageQuery.setSelect(select);
		languageQuery.setCode("la"); // Latina

		// languageQuery.setTestProperty(prop); // should throw
		// getGraphqlInfo(test, TestPrototype::getTestFlag );
		// getGraphQlInfo(test::getTestFlag);

		// languageQuery.setCode();

		GraphQlRequestMarshaller marshaller = new GraphQlRequestMarshaller();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		marshaller.marshall(out, languageQuery);
		String queryQl = "";
		try {
			queryQl = out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Assertions.assertThat(queryQl.replace(" ", "").replace("\n", "")).isEqualTo(expectation);
		System.out.println(queryQl);
	}

	@Test
	public void testMarshallingArguments() {

		final String expectation = "query{value:continents{countries{name(lang:\"de\")}}}";

		continents query = continents.T.create();

		Continent continent = Continent.T.create();

		List<Country> countries = new ArrayList<>();
		Country_withArgs c1 = Country_withArgs.T.create();
		Countries_name_args name_Args = Countries_name_args.T.create();
		name_Args.setLang("de"); // Germany
		c1.setName_args(name_Args);

		c1.setName("");
		countries.add(c1);
		continent.setCountries(countries); // activate output

		List<Continent> select = new ArrayList<>();
		select.add(continent);

		query.setSelect(select);

		GraphQlRequestMarshaller marshaller = new GraphQlRequestMarshaller();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		marshaller.marshall(out, query);
		String queryQl = "";
		try {
			queryQl = out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Assertions.assertThat(queryQl.replace(" ", "").replace("\n", "")).isEqualTo(expectation);
		System.out.println(queryQl);
	}

	@Test
	public void testMarshallingFilter() {

		final String expectation = "query{value:continents(filter:{code:{eq:\"SA\"}}){countries{name}}}";

		continents query = continents.T.create();

		Continent continent = Continent.T.create();

		List<Country> countries = new ArrayList<>();
		Country c1 = Country.T.create();
		c1.setName("");
		countries.add(c1);
		continent.setCountries(countries); // activate output

		List<Continent> select = new ArrayList<>();
		select.add(continent);
		query.setSelect(select);

		ContinentFilterInput filter = ContinentFilterInput.T.create();
		StringQueryOperatorInput operation = StringQueryOperatorInput.T.create();
		operation.setEq("SA"); // South America
		filter.setCode(operation);
		query.setFilter(filter);

		GraphQlRequestMarshaller marshaller = new GraphQlRequestMarshaller();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		marshaller.marshall(out, query);
		String queryQl = "";
		try {
			queryQl = out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Assertions.assertThat(queryQl.replace(" ", "").replace("\n", "")).isEqualTo(expectation);
		System.out.println(queryQl);
	}

	@Test
	public void testMarshallingFilterList() {

		final String expectation = "query{value:continents(filter:{code:{in:[\"SA\",\"NA\"]}}){countries{name}}}";

		continents query = continents.T.create();

		Continent continent = Continent.T.create();

		List<Country> countries = new ArrayList<>();
		Country c1 = Country.T.create();
		c1.setName("");
		countries.add(c1);
		continent.setCountries(countries); // activate output

		List<Continent> select = new ArrayList<>();
		select.add(continent);
		query.setSelect(select);

		ContinentFilterInput filter = ContinentFilterInput.T.create();
		StringQueryOperatorInput operation = StringQueryOperatorInput.T.create();
		List<String> filterList = new ArrayList<String>();
		filterList.add("SA");
		filterList.add("NA");
		operation.setIn(filterList); // South America
		filter.setCode(operation);
		query.setFilter(filter);

		GraphQlRequestMarshaller marshaller = new GraphQlRequestMarshaller();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		marshaller.marshall(out, query);
		String queryQl = "";
		try {
			queryQl = out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Assertions.assertThat(queryQl.replace(" ", "").replace("\n", "")).isEqualTo(expectation);
		System.out.println(queryQl);
	}
}
