package hiconic.ext.graphql.api.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Marker for input types
 */
@Abstract
public interface GraphQlInputType extends GenericEntity {
	EntityType<GraphQlInputType> T = EntityTypes.T(GraphQlInputType.class);
}
