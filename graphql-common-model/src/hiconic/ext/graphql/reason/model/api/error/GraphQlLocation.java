package hiconic.ext.graphql.reason.model.api.error;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlLocation extends GenericEntity {
	EntityType<GraphQlLocation> T = EntityTypes.T(GraphQlLocation.class);

	Integer getLine();
	void setLine(Integer line);

	Integer getColumn();
	void setColumn(Integer column);
}
