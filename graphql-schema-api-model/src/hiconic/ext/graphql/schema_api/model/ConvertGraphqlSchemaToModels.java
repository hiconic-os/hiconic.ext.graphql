package hiconic.ext.graphql.schema_api.model;

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Description;
import com.braintribe.model.generic.annotation.meta.FileName;
import com.braintribe.model.generic.annotation.meta.FolderName;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.Neutral;

@Description("Creates data and api models from a graphql schema file.")
public interface ConvertGraphqlSchemaToModels extends ServiceRequest {

	EntityType<ConvertGraphqlSchemaToModels> T = EntityTypes.T(ConvertGraphqlSchemaToModels.class);

	@Description("Graphql schema file.")
	@Mandatory
	@FileName(mustExist = true)
	String getSchema();
	void setSchema(String schema);

	@Description("Directory where both data and api model artifacts will be placed.")
	@Mandatory
	@FolderName
	String getOutputDir();
	void setOutputDir(String outputDir);

	@Description("GroupId for both created models.")
	@Mandatory
	String getGroupId();
	void setGroupId(String groupId);

	@Description("Base name from which the api and data model names will be derived.")
	@Mandatory
	String getArtifactIdBase();
	void setArtifactIdBase(String artifactIdBase);

	@Description("Package under which all the data and api types will be placed.")
	@Mandatory
	String getPackageBase();
	void setPackageBase(String packageBase);

	@Description("Version for both created models.")
	@Initializer("'1.0'")
	String getVersion();
	void setVersion(String version);

	@Override
	EvalContext<Neutral> eval(Evaluator<ServiceRequest> evaluator);

}
