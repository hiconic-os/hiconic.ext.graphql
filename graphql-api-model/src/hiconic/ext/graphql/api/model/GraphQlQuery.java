
package hiconic.ext.graphql.api.model;

import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlQuery extends GenericEntity {

	EntityType<GraphQlQuery> T = EntityTypes.T(GraphQlQuery.class);

	String getQuery();
	void setQuery(String query);

	Map<String, String> getVariables();
	void setVariables(Map<String, String> variables);

	String getOperationName();
	void setOperationName(String operationName);
}
