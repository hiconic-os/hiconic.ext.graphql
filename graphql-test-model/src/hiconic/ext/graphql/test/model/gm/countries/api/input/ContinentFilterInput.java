package hiconic.ext.graphql.test.model.gm.countries.api.input;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlInputType;

/* this is not a full implementation of the original data type */

public interface ContinentFilterInput extends GraphQlInputType {

	EntityType<ContinentFilterInput> T = EntityTypes.T(ContinentFilterInput.class);

	StringQueryOperatorInput getCode();
	void setCode(StringQueryOperatorInput code);
}
