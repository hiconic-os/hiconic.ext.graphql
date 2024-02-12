package hiconic.ext.graphql.deployment.model;

import com.braintribe.model.extensiondeployment.ServiceAroundProcessor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlAroundProcessor extends ServiceAroundProcessor {

	EntityType<GraphQlAroundProcessor> T = EntityTypes.T(GraphQlAroundProcessor.class);

}
