package hiconic.ext.graphql.reason.model.api.error;

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlError extends GenericEntity {
	EntityType<GraphQlError> T = EntityTypes.T(GraphQlError.class);

	String getMessage();
	void setMessage(String message);

	List<Object> getPath();
	void setPath(List<Object> path);

	List<GraphQlLocation> getLocations();
	void setLocations(List<GraphQlLocation> locations);

	Map<String, GraphQlErrorExtension> getExtensions();
	void setExtensions(Map<String, GraphQlErrorExtension> extensions);
}
