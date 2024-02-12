package hiconic.ext.graphql.reason.model.api.error;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlErrorExtension extends GenericEntity {
	EntityType<GraphQlErrorExtension> T = EntityTypes.T(GraphQlErrorExtension.class);

	String getMessage();
	void setMessage(String message);

	List<Object> getPath();
	void setPath(List<Object> path);

	List<GraphQlLocation> getLocations();
	void setLocations(List<GraphQlLocation> locations);

	String getCode();
	void setCode(String code);

	List<String> getStacktrace();
	void setStacktrace(List<String> stacktrace);

	GraphQlErrorExtensionResponse getResponse();
	void setResponse(GraphQlErrorExtensionResponse response);

	Integer getStatus();
	void setStatus(Integer status);

	String getStatusText();
	void setStatusText(String statusText);

	GraphQlErrorExtensionResponseBody getBody();
	void setBody(GraphQlErrorExtensionResponseBody body);
}
