package hiconic.ext.graphql.test.model.gm.countries.queries;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.Country;

public interface countries extends GraphQlRequest {

	EntityType<countries> T = EntityTypes.T(countries.class);

	// ignore "filter" here

	List<Country> getSelect();
	void setSelect(List<Country> select);
}
