package hiconic.ext.graphql.api.model;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface GraphQlQueryRequest extends GraphQlRequest {

	EntityType<GraphQlQueryRequest> T = EntityTypes.T(GraphQlQueryRequest.class);

}
