package hiconic.ext.graphql.test.model.gm.countries.queries;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.test.model.gm.countries.queries.types.Language;

public interface language extends GraphQlRequest {

	EntityType<language> T = EntityTypes.T(language.class);

	String getCode();
	void setCode(String code);
	
	Language getSelect();
	void setSelect(Language data1);
}
