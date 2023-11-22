package hiconic.ext.graphql.api.model.result;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlSingleValue extends GraphQlValue {
	EntityType<GraphQlSingleValue> T = EntityTypes.T(GraphQlSingleValue.class);
	
	String value = "value";
	
	Object getValue();
	void setValue(Object value);
}
