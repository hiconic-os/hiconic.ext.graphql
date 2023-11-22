package hiconic.ext.graphql.test.model.gm.countries.queries.types;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlFieldArguments;

/* this is not a full implementation of the original data type */

public interface StringQueryOperatorInput extends GraphQlFieldArguments {

	EntityType<StringQueryOperatorInput> T = EntityTypes.T(StringQueryOperatorInput.class);

	String getEq();
	void setEq(String eq);

	List<String> getIn();
	void setIn(List<String> in);

}
