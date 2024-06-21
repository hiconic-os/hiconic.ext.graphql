package hiconic.ext.graphql.test.model.gm.countries.api.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.test.model.gm.countries.data.Country;

public interface CountriesRequest extends GraphQlTestQueryRequest {

	EntityType<CountriesRequest> T = EntityTypes.T(CountriesRequest.class);

	// ignore "filter" here

	Country getSelect();
	void setSelect(Country select);
}
