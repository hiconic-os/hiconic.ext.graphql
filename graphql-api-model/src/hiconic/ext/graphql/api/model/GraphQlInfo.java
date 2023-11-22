package hiconic.ext.graphql.api.model;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.value.ValueDescriptor;

public interface GraphQlInfo extends ValueDescriptor {
	EntityType<GraphQlInfo> T = EntityTypes.T(GraphQlInfo.class);

	Object getValue();
	void setValue(Object value);
	
	boolean getSelect();
	void setSelect(boolean select);

	Object getCompareTo();
	void setCompareTo(Object compareTo);
}
