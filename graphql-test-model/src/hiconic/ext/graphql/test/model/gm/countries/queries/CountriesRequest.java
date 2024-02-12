package hiconic.ext.graphql.test.model.gm.countries.queries;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlQueryRequest;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.Country;

public interface CountriesRequest extends GraphQlQueryRequest {

	EntityType<CountriesRequest> T = EntityTypes.T(CountriesRequest.class);

	// ignore "filter" here

	List<Country> getSelect();
	void setSelect(List<Country> select);
}
