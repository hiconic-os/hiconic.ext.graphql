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
package hiconic.ext.graphql;

import static com.braintribe.console.ConsoleOutputs.println;
import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import com.braintribe.console.ConsoleOutputs;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.gm.model.reason.essential.InvalidArgument;
import com.braintribe.model.io.metamodel.GmSourceWriter;
import com.braintribe.model.meta.GmMetaModel;
import com.braintribe.model.processing.deployment.PublishModelProcessor;
import com.braintribe.model.processing.service.api.ReasonedServiceProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.api.ResourceBuilder;
import com.braintribe.model.resource.utils.StreamPipeTransientResourceBuilder;
import com.braintribe.model.service.api.result.Neutral;
import com.braintribe.utils.FileTools;
import com.braintribe.utils.ZipTools;
import com.braintribe.utils.lcd.StringTools;
import com.braintribe.utils.stream.pools.CompoundBlockPool;
import com.braintribe.utils.stream.pools.SmartBlockPoolFactory;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import hiconic.ext.graphql.schema.gm.GmModels;
import hiconic.ext.graphql.schema.gm.GraphQlSchemaToGmModel;
import hiconic.ext.graphql.schema_api.model.ConvertGraphqlSchemaToModels;

public class ConvertGraphqlSchemaToModelsProcessor implements ReasonedServiceProcessor<ConvertGraphqlSchemaToModels, Neutral> {

	@Override
	public Maybe<? extends Neutral> processReasoned(ServiceRequestContext context, ConvertGraphqlSchemaToModels request) {
		File schemaFile = new File(request.getSchema());
		if (!schemaFile.exists())
			return invalidArugment("Schema file does not exist: " + schemaFile.getAbsolutePath());

		TypeDefinitionRegistry tdr = parse(schemaFile);

		GmModels models = GraphQlSchemaToGmModel.createModels( //
				request.getArtifactIdBase(), request.getGroupId(), request.getVersion(), //
				request.getPackageBase(), //
				tdr);

		File outDir = new File(request.getOutputDir());

		if (request.getAsBuiltArtifacts())
			projectBuiltArtifacts(models, outDir);
		else
			projectSources(models, outDir);

		return Maybe.complete(Neutral.NEUTRAL);
	}

	private void projectBuiltArtifacts(GmModels models, File outDir) {
		PublishModelProcessor publisher = new PublishModelProcessor();
		publisher.setUserNameSupplier(() -> "graphql-schema-converter");
		publisher.setResourceBuilder(resourceBuilder());

		Resource zippedModels = publisher.publish(asList(models.apiModel(), models.dataModel()));

		try (InputStream is = zippedModels.openStream()) {
			ZipTools.unzip(is, outDir);

		} catch (IOException e) {
			println(ConsoleOutputs.red("Error while writing models: " + e.getMessage()));
		}
	}

	private void projectSources(GmModels models, File outDir) {
		writeSources(models.apiModel(), outDir);
		writeSources(models.dataModel(), outDir);
	}

	private void writeSources(GmMetaModel gmModel, File outDir) {
		String modelName = getModelArtifactId(gmModel);

		File srcDir = outDir.toPath().resolve(modelName).resolve("src").toFile();

		GmSourceWriter sourceWriter = new GmSourceWriter();
		sourceWriter.setOutputDirectory(srcDir);
		sourceWriter.setGmMetaModel(gmModel);
		sourceWriter.enableWritingSourcesForExistingClasses();

		sourceWriter.writeMetaModelToDirectory();
	}

	private String getModelArtifactId(GmMetaModel gmModel) {
		return StringTools.getSubstringAfterLast(gmModel.getName(), ":");
	}

	private static ResourceBuilder resourceBuilder() {
		return new StreamPipeTransientResourceBuilder(streamPipeFactory());
	}

	private static CompoundBlockPool streamPipeFactory() {
		SmartBlockPoolFactory poolFactory = SmartBlockPoolFactory.usingAvailableMemory(0.1);
		poolFactory.setStreamPipeFolder(streamPipeFolder());

		return poolFactory.create();
	}

	private static File streamPipeFolder() {
		File tempDir = FileTools.getTempDir();
		File bean = new File(tempDir, "hiconic/basicStreamPipe");

		return bean;
	}

	private Maybe<? extends Neutral> invalidArugment(String text) {
		return Reasons.build(InvalidArgument.T) //
				.text(text) //
				.toMaybe();
	}

	public static TypeDefinitionRegistry parse(File schemaFile) {
		try (Reader reader = new BufferedReader(new FileReader(schemaFile))) {
			SchemaParser schemaParser = new SchemaParser();
			return schemaParser.parse(reader);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
