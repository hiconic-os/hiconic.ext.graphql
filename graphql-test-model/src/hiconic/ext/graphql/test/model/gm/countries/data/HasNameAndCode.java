package hiconic.ext.graphql.test.model.gm.countries.data;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

@Abstract
public interface HasNameAndCode extends GenericEntity {

	EntityType<HasNameAndCode> T = EntityTypes.T(HasNameAndCode.class);

	String getName();
	void setName(String name);

	String getCode();
	void setCode(String code);

}
