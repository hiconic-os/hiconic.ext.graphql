package hiconic.ext.graphql.test.model.gm.countries.queries.types;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;

/* this is not a full implementation of the original data type */

public interface Country_withArgs extends Country, HasGraphQlFieldArguments {

	EntityType<Country_withArgs> T = EntityTypes.T(Country_withArgs.class);

	Countries_name_args getName_args();
	void setName_args(Countries_name_args string);
}
