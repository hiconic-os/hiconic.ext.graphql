package hiconic.ext.graphql.schema_api.model;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;

public interface ConvertGraphQlSchemaToArtifact extends ServiceRequest {
	EntityType<ConvertGraphQlSchemaToArtifact> T = EntityTypes.T(ConvertGraphQlSchemaToArtifact.class);

	String schema = "schema";
	String artifactName = "artifactName";

	Resource getSchema();
	void setSchema(Resource schema);

	String getArtifactName();
	void setArtifactName(String name);

}
