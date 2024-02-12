package hiconic.ext.graphql.deployment.model;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.marshallerdeployment.Marshaller;

public interface GraphQlMarshaller extends Marshaller {

	EntityType<GraphQlMarshaller> T = EntityTypes.T(GraphQlMarshaller.class);

}
