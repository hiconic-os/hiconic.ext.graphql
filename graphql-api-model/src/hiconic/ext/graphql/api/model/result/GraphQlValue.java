package hiconic.ext.graphql.api.model.result;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface GraphQlValue extends GenericEntity {
	EntityType<GraphQlValue> T = EntityTypes.T(GraphQlValue.class);
}
