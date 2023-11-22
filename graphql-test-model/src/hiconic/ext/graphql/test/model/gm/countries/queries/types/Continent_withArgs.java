package hiconic.ext.graphql.test.model.gm.countries.queries.types;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;

public interface Continent_withArgs extends Continent, HasGraphQlFieldArguments {

	EntityType<Continent_withArgs> T = EntityTypes.T(Continent_withArgs.class);

	Countries_name_args getCountries_name_args();
	void setCountries_name_args(Countries_name_args name_args);
}
