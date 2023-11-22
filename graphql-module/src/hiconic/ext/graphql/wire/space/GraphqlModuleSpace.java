
package hiconic.ext.graphql.wire.space;

import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.model.processing.deployment.api.binding.DenotationBindingBuilder;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;

import hiconic.ext.graphql.GraphQlByPrototypeMarshaller;
import tribefire.module.api.InitializerBindingBuilder;
import tribefire.module.api.WireContractBindingBuilder;
import tribefire.module.wire.contract.TribefireModuleContract;
import tribefire.module.wire.contract.TribefireWebPlatformContract;

/**
  * Bind the GraphQL extension into a platform. It registers a marshaller using
 * the MIME type "graphql". The provided marshaller can transpose a
 * QueryByPrototype request into GraphQL syntax.
 * 
 * QueryByPrototyp offers (only) a very basic functionality of GraphQl but it
 * does it with full type and name safety.
 */
@Managed
public class GraphqlModuleSpace implements TribefireModuleContract {

	@Import
	private TribefireWebPlatformContract tfPlatform;

	//
	// WireContracts
	//

	@Override
	public void bindWireContracts(WireContractBindingBuilder bindings) {
		// Bind wire contracts to make them available for other modules.
		// Note that the Contract class cannot be defined in this module, but must be in
		// a gm-api artifact.
	}

	//
	// Hardwired deployables
	//

	@Override
	public void bindHardwired() {
		String name = "graphql-by-prototype";
		String externalId = "";
		tfPlatform.hardwiredDeployables().bindMarshaller(externalId, name, graphQlMarshaller(), "graphql-by-prototype");
	}

	@Managed
	private Marshaller graphQlMarshaller() {
		return new GraphQlByPrototypeMarshaller();
	}

	//
	// Initializers
	//

	@Override
	public void bindInitializers(InitializerBindingBuilder bindings) {
		// Bind DataInitialiers for various CollaborativeAcceses
	}

	//
	// Deployables
	//

	@Override
	public void bindDeployables(DenotationBindingBuilder bindings) {
		// Bind deployment experts for deployable denotation types.
		// Note that the basic component binders (for e.g. serviceProcessor or
		// incrementalAccess) can be found via tfPlatform.deployment().binders().
	}

}