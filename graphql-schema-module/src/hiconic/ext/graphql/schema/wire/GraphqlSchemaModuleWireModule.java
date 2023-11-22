
package hiconic.ext.graphql.schema.wire;

import tribefire.module.wire.contract.StandardTribefireModuleWireModule;
import tribefire.module.wire.contract.TribefireModuleContract;
import hiconic.ext.graphql.schema.wire.space.GraphqlSchemaModuleSpace;

public enum GraphqlSchemaModuleWireModule implements StandardTribefireModuleWireModule {

	INSTANCE;

	@Override
	public Class<? extends TribefireModuleContract> moduleSpaceClass() {
		return GraphqlSchemaModuleSpace.class;
	}

}