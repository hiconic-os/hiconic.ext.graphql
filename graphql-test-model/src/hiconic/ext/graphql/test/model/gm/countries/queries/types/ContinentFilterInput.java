package hiconic.ext.graphql.test.model.gm.countries.queries.types;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlFieldArguments;

/* this is not a full implementation of the original data type */

public interface ContinentFilterInput extends GraphQlFieldArguments {

	EntityType<ContinentFilterInput> T = EntityTypes.T(ContinentFilterInput.class);

	StringQueryOperatorInput getCode();
	void setCode(StringQueryOperatorInput code);
}
