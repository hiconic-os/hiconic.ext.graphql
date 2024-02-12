package hiconic.ext.graphql.test.model.gm.countries.queries;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlQueryRequest;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.Continent;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.ContinentFilterInput;

public interface ContinentsRequest extends GraphQlQueryRequest {

	EntityType<ContinentsRequest> T = EntityTypes.T(ContinentsRequest.class);

	ContinentFilterInput getFilter();
	void setFilter(ContinentFilterInput filter);

	List<Continent> getSelect();
	void setSelect(List<Continent> select);
}
