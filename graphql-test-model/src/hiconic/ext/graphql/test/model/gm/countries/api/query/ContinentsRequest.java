package hiconic.ext.graphql.test.model.gm.countries.api.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.test.model.gm.countries.api.input.ContinentFilterInput;
import hiconic.ext.graphql.test.model.gm.countries.data.Continent;

public interface ContinentsRequest extends GraphQlTestQueryRequest {

	EntityType<ContinentsRequest> T = EntityTypes.T(ContinentsRequest.class);

	ContinentFilterInput getFilter();
	void setFilter(ContinentFilterInput filter);

	Continent getSelect();
	void setSelect(Continent select);
}
