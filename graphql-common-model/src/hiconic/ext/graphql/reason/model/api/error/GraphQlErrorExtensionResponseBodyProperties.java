package hiconic.ext.graphql.reason.model.api.error;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlErrorExtensionResponseBodyProperties extends GenericEntity {
	EntityType<GraphQlErrorExtensionResponseBodyProperties> T = EntityTypes.T(GraphQlErrorExtensionResponseBodyProperties.class);

	List<GraphQlErrorExtensionResponseBodyError> getErrors();
	void setErrors(List<GraphQlErrorExtensionResponseBodyError> errors);

}
