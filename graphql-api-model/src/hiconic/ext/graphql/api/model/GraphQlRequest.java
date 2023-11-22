package hiconic.ext.graphql.api.model;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.DomainRequest;

/**
 * Every request has a property called "select".
 */
@Abstract
public interface GraphQlRequest extends DomainRequest {

	EntityType<GraphQlRequest> T = EntityTypes.T(GraphQlRequest.class);

}
