
package hiconic.ext.graphql.wire;

import tribefire.module.wire.contract.StandardTribefireModuleWireModule;
import tribefire.module.wire.contract.TribefireModuleContract;
import hiconic.ext.graphql.wire.space.GraphqlModuleSpace;

public enum GraphqlModuleWireModule implements StandardTribefireModuleWireModule {

	INSTANCE;

	@Override
	public Class<? extends TribefireModuleContract> moduleSpaceClass() {
		return GraphqlModuleSpace.class;
	}

}