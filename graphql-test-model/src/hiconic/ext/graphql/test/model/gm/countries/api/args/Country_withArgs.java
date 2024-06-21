package hiconic.ext.graphql.test.model.gm.countries.api.args;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;
import hiconic.ext.graphql.test.model.gm.countries.data.Country;

/* this is not a full implementation of the original data type */

public interface Country_withArgs extends Country, HasGraphQlFieldArguments {

	EntityType<Country_withArgs> T = EntityTypes.T(Country_withArgs.class);

	Country_name_args getName_args_();
	void setName_args_(Country_name_args name_args_);
}
