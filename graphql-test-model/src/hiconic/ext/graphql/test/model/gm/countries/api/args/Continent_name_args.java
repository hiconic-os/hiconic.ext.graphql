package hiconic.ext.graphql.test.model.gm.countries.api.args;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlFieldArguments;

/* this is not a full implementation of the original data type */

public interface Continent_name_args extends GraphQlFieldArguments {

	EntityType<Continent_name_args> T = EntityTypes.T(Continent_name_args.class);

	String getLang();
	void setLang(String lang);
}
