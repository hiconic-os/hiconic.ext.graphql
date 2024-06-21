package hiconic.ext.graphql;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.junit.Test;

import com.braintribe.codec.marshaller.api.MarshallException;
import com.braintribe.testing.junit.assertions.assertj.core.api.Assertions;

import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.test.model.gm.countries.api.query.ContinentsRequest;
import hiconic.ext.graphql.test.model.gm.countries.data.Continent;
import hiconic.ext.graphql.test.model.gm.countries.data.Country;

/**
 * Tests for {@link GraphQlRequestMarshaller}.
 * 
 * @see GraphQlRequestMarshallerTest
 */
public class GraphQlRequestMarshaller_ErrorInput_Test {

	private final GraphQlRequestMarshaller marshaller = new GraphQlRequestMarshaller();
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	@Test
	public void testSelectingEmptyCollectionNotAllowed() {
		Continent continent = Continent.T.create();
		continent.setCountries(new ArrayList<>()); // ILLEGAL

		ContinentsRequest query = ContinentsRequest.T.create();
		query.setSelect(continent);

		assertMarshallerException(query, "no selected return fields");
	}

	@Test
	public void testSelectingCollectionWithMultipleElementsNotAllowed() {
		ContinentsRequest query = ContinentsRequest.T.create();

		Continent continent = Continent.T.create();
		continent.setCountries(asList(Country.T.create(), Country.T.create()));

		query.setSelect(continent);

		assertMarshallerException(query, "contains 2 elements");
	}

	private void assertMarshallerException(GraphQlRequest query, String msg) {
		Assertions.assertThatThrownBy(() -> marshaller.marshall(out, query)) //
				.isInstanceOf(MarshallException.class) //
				.hasMessageContaining(msg);
	}

}
