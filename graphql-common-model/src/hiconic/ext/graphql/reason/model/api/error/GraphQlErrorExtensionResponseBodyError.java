package hiconic.ext.graphql.reason.model.api.error;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlErrorExtensionResponseBodyError extends GenericEntity {
	EntityType<GraphQlErrorExtensionResponseBodyError> T = EntityTypes.T(GraphQlErrorExtensionResponseBodyError.class);

	String getType();
	void setType(String type);

	String getCode();
	void setCode(String code);

}
