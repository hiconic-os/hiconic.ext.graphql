package hiconic.ext.graphql.test.model.gm.countries.api.args;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;
import hiconic.ext.graphql.test.model.gm.countries.data.Continent;

public interface Continent_withArgs extends Continent, HasGraphQlFieldArguments {

	EntityType<Continent_withArgs> T = EntityTypes.T(Continent_withArgs.class);

	Continent_name_args getName_args_();
	void setName_args_(Continent_name_args name_args_);

}
