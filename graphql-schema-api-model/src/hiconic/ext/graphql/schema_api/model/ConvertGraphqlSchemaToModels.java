// ============================================================================
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
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

// NOTW: we use Graphql (not GraphQl) here so the command is called convert-graphql-schema-to-models.

@Description("Creates data and api models from a graphql schema file.")
public interface ConvertGraphqlSchemaToModels extends GraphQlSchemaRequest {

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

	@Description("If true, built artifacts with all relevant parts (classes, sources, javadoc) will be created. Otherwise only sources are created.")
	boolean getAsBuiltArtifacts();
	void setAsBuiltArtifacts(boolean asArtifacts);

	@Override
	EvalContext<Neutral> eval(Evaluator<ServiceRequest> evaluator);

}
