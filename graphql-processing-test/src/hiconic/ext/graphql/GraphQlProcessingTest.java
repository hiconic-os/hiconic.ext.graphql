package hiconic.ext.graphql;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;

import com.braintribe.codec.marshaller.api.MarshallException;

import hiconic.ext.graphql.test.model.gm.countries.queries.ContinentsRequest;
import hiconic.ext.graphql.test.model.gm.countries.queries.LanguageRequest;
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

		final String expectation = wrapToJson("query{value:language(code:\\\"la\\\"){native}}");

		LanguageRequest languageQuery = LanguageRequest.T.create();

		Language select = Language.T.create();
		select.setNative(""); // activate output

		languageQuery.setSelect(select);
		languageQuery.setCode("la"); // Latina

		GraphQlRequestMarshaller marshaller = new GraphQlRequestMarshaller();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		marshaller.marshall(out, languageQuery);
		String queryQl = out.toString(StandardCharsets.UTF_8);
		Assertions.assertThat(strip(queryQl)).isEqualTo(expectation);
		System.out.println(queryQl);
	}

	@Test
	@Ignore
	public void testMarshallingArguments() {

		final String expectation = wrapToJson("query{value:continents{countries{name(lang:\\\"de\\\")}}}");

		ContinentsRequest query = ContinentsRequest.T.create();

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
		String queryQl = out.toString(StandardCharsets.UTF_8);
		Assertions.assertThat(strip(queryQl)).isEqualTo(expectation);
		System.out.println(queryQl);
	}

	@Test
	public void testEmptyCollectionThrows() {

		ContinentsRequest query = ContinentsRequest.T.create();

		Continent continent = Continent.T.create();

		List<Country> countries = new ArrayList<>();
		Countries_name_args name_Args = Countries_name_args.T.create();
		name_Args.setLang("de"); // Germany
		continent.setCountries(countries); // ILLEGAL

		List<Continent> select = new ArrayList<>();
		select.add(continent);

		query.setSelect(select);

		GraphQlRequestMarshaller marshaller = new GraphQlRequestMarshaller();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Assertions.assertThatThrownBy(() -> {
			marshaller.marshall(out, query);
		}).isInstanceOf(MarshallException.class).hasMessageContaining("Malformed GraphQlRequest with no selected return fields");
	}

	@Test
	public void testTooFullCollectionThrows() {

		ContinentsRequest query = ContinentsRequest.T.create();

		Continent continent = Continent.T.create();

		List<Country> countries = new ArrayList<>();
		Countries_name_args name_Args = Countries_name_args.T.create();
		name_Args.setLang("de"); // Germany
		countries.add(Country.T.create());
		countries.add(Country.T.create());// ILLEGAL
		continent.setCountries(countries);

		List<Continent> select = new ArrayList<>();
		select.add(continent);

		query.setSelect(select);

		GraphQlRequestMarshaller marshaller = new GraphQlRequestMarshaller();
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Assertions.assertThatThrownBy(() -> {
			marshaller.marshall(out, query);
		}).isInstanceOf(MarshallException.class).hasMessageContaining("contains 2 elements. Must be exactly one.");
	}

	@Test
	public void testMarshallingFilter() {

		final String expectation = wrapToJson("query{value:continents(filter:{code:{eq:\\\"SA\\\"}}){countries{name}}}");

		ContinentsRequest query = ContinentsRequest.T.create();

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
		String queryQl = out.toString(StandardCharsets.UTF_8);
		Assertions.assertThat(strip(queryQl)).isEqualTo(expectation);
		System.out.println(queryQl);
	}

	@Test
	public void testMarshallingFilterList() {

		final String expectation = wrapToJson("query{value:continents(filter:{code:{in:[\\\"SA\\\",\\\"NA\\\"]}}){countries{name}}}");

		ContinentsRequest query = ContinentsRequest.T.create();

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
		String queryQl = out.toString(StandardCharsets.UTF_8);
		Assertions.assertThat(strip(queryQl)).isEqualTo(expectation);
		System.out.println(queryQl);
	}

	private String wrapToJson(String str) {
		return "{\"query\":\"" + str + "\"}";
	}

	private String strip(String str) {
		return str.replace(" ", "").replace("\\n", "").replace("\n", "");
	}
}
