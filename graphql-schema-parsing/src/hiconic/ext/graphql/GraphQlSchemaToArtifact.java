package hiconic.ext.graphql;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.service.api.ReasonedServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Neutral;

import hiconic.ext.graphql.schema_api.model.ConvertGraphQlSchemaToArtifact;
import hiconic.ext.graphql.schema_api.model.ConvertGraphQlSchemaToGm;
import tribefire.extension.artifact.management.api.model.request.UploadArtifacts;

public class GraphQlSchemaToArtifact implements ReasonedServiceProcessor<ConvertGraphQlSchemaToArtifact, Neutral> {

	@Override
	public Maybe<? extends Neutral> processReasoned(ServiceRequestContext context,
			ConvertGraphQlSchemaToArtifact request) {

		Resource schema = request.getSchema();
		String path = request.getArtifactName();

		ConvertGraphQlSchemaToGm convertToGm = ConvertGraphQlSchemaToGm.T.create();
		convertToGm.setSchema(schema);
		Evaluator<ServiceRequest> evaluator = null;
		Maybe<?> models = convertToGm.eval(evaluator).getReasoned();

		UploadArtifacts uploadArtifact = UploadArtifacts.T.create();
		uploadArtifact.setPath(path);
		// ...
		Maybe<Neutral> upload = uploadArtifact.eval(evaluator).getReasoned();

		return null;
	}

}
