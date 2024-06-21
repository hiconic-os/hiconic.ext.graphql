package hiconic.ext.graphql.test.model.gm.countries.api.args;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlFieldArguments;

/* this is not a full implementation of the original data type */

public interface Country_name_args extends GraphQlFieldArguments {

	EntityType<Country_name_args> T = EntityTypes.T(Country_name_args.class);

	String getLang();
	void setLang(String lang);
}
