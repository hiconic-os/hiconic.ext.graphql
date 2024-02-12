package hiconic.ext.graphql.reason.model.api.error;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlErrorExtensionResponse extends GenericEntity {
	EntityType<GraphQlErrorExtensionResponse> T = EntityTypes.T(GraphQlErrorExtensionResponse.class);

	String getUrl();
	void setUrl(String url);

	Integer getStatus();
	void setStatus(Integer status);

	String getStatusText();
	void setStatusText(String statusText);

	GraphQlErrorExtensionResponseBody getBody();
	void setBody(GraphQlErrorExtensionResponseBody body);
}
