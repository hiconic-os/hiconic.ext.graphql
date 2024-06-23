package hiconic.ext.graphql.test.model.gm.countries.api.typeconditions;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.HasGraphQlTypeConditions;
import hiconic.ext.graphql.test.model.gm.countries.data.HasNameAndCode;

public interface HasNameAndCode_TypeConditions extends HasNameAndCode, HasGraphQlTypeConditions {

	EntityType<HasNameAndCode_TypeConditions> T = EntityTypes.T(HasNameAndCode_TypeConditions.class);

	List<HasNameAndCode> getTypeConditions_();
	void setTypeConditions_(List<HasNameAndCode> typeConditions_);

}
