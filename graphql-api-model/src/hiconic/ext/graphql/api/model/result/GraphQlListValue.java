package hiconic.ext.graphql.api.model.result;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlListValue extends GraphQlValue {
	EntityType<GraphQlListValue> T = EntityTypes.T(GraphQlListValue.class);

	String value = "value";

	List<Object> getValue();
	void setValue(List<Object> value);
}
