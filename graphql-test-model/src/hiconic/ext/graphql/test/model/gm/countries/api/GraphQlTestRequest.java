package hiconic.ext.graphql.test.model.gm.countries.api;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.AuthorizableRequest;

import hiconic.ext.graphql.api.model.GraphQlRequest;

public interface GraphQlTestRequest extends GraphQlRequest, AuthorizableRequest {

	EntityType<GraphQlTestRequest> T = EntityTypes.T(GraphQlTestRequest.class);

}
