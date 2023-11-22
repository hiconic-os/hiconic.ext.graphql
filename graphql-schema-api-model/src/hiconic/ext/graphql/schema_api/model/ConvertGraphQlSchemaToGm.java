package hiconic.ext.graphql.schema_api.model;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;

public interface ConvertGraphQlSchemaToGm extends ServiceRequest {
	EntityType<ConvertGraphQlSchemaToGm> T = EntityTypes.T(ConvertGraphQlSchemaToGm.class);

	String schema = "schema";

	Resource getSchema();
	void setSchema(Resource schema);

}
