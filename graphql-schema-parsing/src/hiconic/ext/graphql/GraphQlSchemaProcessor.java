package hiconic.ext.graphql;

import com.braintribe.model.processing.service.impl.AbstractDispatchingServiceProcessor;
import com.braintribe.model.processing.service.impl.DispatchConfiguration;

import hiconic.ext.graphql.schema_api.model.ConvertGraphqlSchemaToModels;
import hiconic.ext.graphql.schema_api.model.GraphQlSchemaRequest;

public class GraphQlSchemaProcessor extends AbstractDispatchingServiceProcessor<GraphQlSchemaRequest, Object> {

	@Override
	protected void configureDispatching(DispatchConfiguration<GraphQlSchemaRequest, Object> dispatching) {
		dispatching.registerReasoned(ConvertGraphqlSchemaToModels.T, new ConvertGraphqlSchemaToModelsProcessor());
	}

}
