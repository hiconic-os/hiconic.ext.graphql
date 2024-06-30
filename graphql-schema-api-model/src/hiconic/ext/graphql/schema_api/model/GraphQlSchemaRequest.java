package hiconic.ext.graphql.schema_api.model;

import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

@Abstract
public interface GraphQlSchemaRequest extends ServiceRequest {

	EntityType<GraphQlSchemaRequest> T = EntityTypes.T(GraphQlSchemaRequest.class);

}
