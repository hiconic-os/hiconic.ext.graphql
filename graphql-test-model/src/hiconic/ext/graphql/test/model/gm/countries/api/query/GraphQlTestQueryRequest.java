package hiconic.ext.graphql.test.model.gm.countries.api.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlQueryRequest;
import hiconic.ext.graphql.test.model.gm.countries.api.GraphQlTestRequest;

public interface GraphQlTestQueryRequest extends GraphQlTestRequest, GraphQlQueryRequest {

	EntityType<GraphQlTestQueryRequest> T = EntityTypes.T(GraphQlTestQueryRequest.class);

}
