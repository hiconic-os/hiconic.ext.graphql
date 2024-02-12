package hiconic.ext.graphql.api.model.result;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.reason.model.api.error.GraphQlError;

// TODO> remove "prototype"

public interface GraphQlResult extends GenericEntity {
	EntityType<GraphQlResult> T = EntityTypes.T(GraphQlResult.class);

	String data = "data";
	String errors = "errors";

	GraphQlValue getData();
	void setData(GraphQlValue data);

	List<GraphQlError> getErrors();
	void setErrors(List<GraphQlError> errors);

}
