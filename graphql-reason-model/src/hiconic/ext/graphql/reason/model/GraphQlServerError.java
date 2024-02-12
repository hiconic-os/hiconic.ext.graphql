package hiconic.ext.graphql.reason.model;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.reason.model.api.error.GraphQlError;

public interface GraphQlServerError extends GraphQlReason {

	EntityType<GraphQlServerError> T = EntityTypes.T(GraphQlServerError.class);

	List<GraphQlError> getErrors();
	void setErrors(List<GraphQlError> errors);

}
