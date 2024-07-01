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
package hiconic.ext.graphql.schema.main;

import static com.braintribe.utils.lcd.CollectionTools2.asList;

import java.io.File;
import java.io.InputStream;

import com.braintribe.model.processing.deployment.PublishModelProcessor;
import com.braintribe.model.resource.Resource;
import com.braintribe.model.resource.api.ResourceBuilder;
import com.braintribe.model.resource.utils.StreamPipeTransientResourceBuilder;
import com.braintribe.utils.FileTools;
import com.braintribe.utils.ZipTools;
import com.braintribe.utils.stream.pools.CompoundBlockPool;
import com.braintribe.utils.stream.pools.SmartBlockPoolFactory;

import graphql.schema.idl.TypeDefinitionRegistry;
import hiconic.ext.graphql.schema.gm.GmModels;
import hiconic.ext.graphql.schema.gm.GraphQlSchemaToGmModel;
import hiconic.ext.graphql.schema.gm.GraphQlToGmModel_BasicTest;

/**
 * <b>How to use:</b>
 * <p>
 * <ul>
 * <li>Create a directory "work"
 * <li>Place your schema to: "work/in/schema.graphql"
 * <li>Run {@link #main(String[])}
 * <li>Find your result in: "work/out"
 * </ul>
 * 
 * @author peter.gazdik
 */
public class GraphQlToGmMain {

	private static final String FILE_NAME_IN = "work/in/schema.graphql";
	private static final String FILE_NAME_OUT = "work/out";

	private static final String MODEL_GROUP_ID = "swissre.claims";
	private static final String MODEL_ARTIFACT_ID_BASE = "tangram";
	private static final String MODEL_VERSION = "1.0";
	private static final String MODEL_PACKAGE_BASE = "swissre.claims.tangram.model";

	public static void main(String[] args) throws Exception {
		System.out.println("Parsing: " + FILE_NAME_IN);
		TypeDefinitionRegistry tdr = GraphQlToGmModel_BasicTest.parse(new File(FILE_NAME_IN));

		System.out.println("Converting GraphQL schema to model.");
		GmModels models = GraphQlSchemaToGmModel.createModels( //
				MODEL_ARTIFACT_ID_BASE, MODEL_GROUP_ID, MODEL_VERSION, //
				MODEL_PACKAGE_BASE, //
				tdr);

		PublishModelProcessor publisher = new PublishModelProcessor();
		publisher.setUserNameSupplier(() -> "graphql-schema-converter");
		publisher.setResourceBuilder(resourceBuilder());

		Resource zippedModels = publisher.publish(asList(models.apiModel(), models.dataModel()));

		System.out.println("Writing model to: " + FILE_NAME_OUT);
		try (InputStream is = zippedModels.openStream()) {
			ZipTools.unzip(is, new File(FILE_NAME_OUT));
		}

		System.out.println("DONE!");
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

}
