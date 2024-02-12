package hiconic.ext.graphql.reason.model.api.error;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlErrorExtensionResponseBody extends GenericEntity {
	EntityType<GraphQlErrorExtensionResponseBody> T = EntityTypes.T(GraphQlErrorExtensionResponseBody.class);

	String getType();
	void setType(String type);

	String getTitle();
	void setTitle(String title);

	Integer getStatus();
	void setStatus(Integer status);

	String getDetail();
	void setDetail(String detail);

	String getInstance();
	void setInstance(String instance);

	GraphQlErrorExtensionResponseBodyProperties getProperties();
	void setProperties(GraphQlErrorExtensionResponseBodyProperties properties);
}
