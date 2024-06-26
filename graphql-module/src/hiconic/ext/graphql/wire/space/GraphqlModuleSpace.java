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
package hiconic.ext.graphql.wire.space;

import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.model.processing.deployment.api.binding.DenotationBindingBuilder;
import com.braintribe.model.processing.service.api.ServiceAroundProcessor;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import hiconic.ext.graphql.GraphQlRequestMarshaller;
import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.deployment.model.GraphQlAroundProcessor;
import hiconic.ext.graphql.deployment.model.GraphQlMarshaller;
import tribefire.module.wire.contract.TribefireModuleContract;
import tribefire.module.wire.contract.TribefireWebPlatformContract;

/**
 * Registers a {@link GraphQlMarshaller} for MIME type "application/graphql".
 * <p>
 * This marshaller encodes {@link GraphQlRequest} into GraphQL syntax.
 */
@Managed
public class GraphqlModuleSpace implements TribefireModuleContract {

	@Import
	private TribefireWebPlatformContract tfPlatform;

	//
	// Hardwired deployables
	//

	@Override
	public void bindHardwired() {
		tfPlatform.hardwiredDeployables().bindMarshaller( //
				"marshaller.graphql", "GraphQL Marshaller", graphQlMarshaller(), "application/graphql");
	}

	//
	// Deployables
	//

	@Override
	public void bindDeployables(DenotationBindingBuilder bindings) {
		bindings.bind(GraphQlMarshaller.T).component(tfPlatform.binders().marshaller()).expert(graphQlMarshaller());
		bindings.bind(GraphQlAroundProcessor.T).component(tfPlatform.binders().serviceAroundProcessor()).expert(graphQlAroundProcessor());
	}

	private ServiceAroundProcessor<?, ?> graphQlAroundProcessor() {
		hiconic.ext.graphql.GraphQlAroundProcessor bean = new hiconic.ext.graphql.GraphQlAroundProcessor();
		return bean;
	}
	@Managed
	private Marshaller graphQlMarshaller() {
		GraphQlRequestMarshaller bean = new GraphQlRequestMarshaller();
		return bean;
	}
}