package hiconic.ext.graphql.test.model.gm.countries.api.input;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlInputType;

/* this is not a full implementation of the original data type */

public interface StringQueryOperatorInput extends GraphQlInputType {

	EntityType<StringQueryOperatorInput> T = EntityTypes.T(StringQueryOperatorInput.class);

	String getEq();
	void setEq(String eq);

	List<String> getIn();
	void setIn(List<String> in);

}
