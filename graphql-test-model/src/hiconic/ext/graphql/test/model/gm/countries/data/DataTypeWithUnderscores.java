package hiconic.ext.graphql.test.model.gm.countries.data;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/* this is not a full implementation of the original data type */

public interface DataTypeWithUnderscores extends GenericEntity {

	EntityType<DataTypeWithUnderscores> T = EntityTypes.T(DataTypeWithUnderscores.class);

	/* This should be just "id" in GraphQL */
	String getId_();
	void setId_(String id_);

	/* This should be just "name_" in GraphQL */
	Integer getAge__();
	void setAge__(Integer age__);

}
