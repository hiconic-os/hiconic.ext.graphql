package hiconic.ext.graphql.test.model.gm.countries.api.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.test.model.gm.countries.data.Language;

public interface LanguageRequest extends GraphQlTestQueryRequest {

	EntityType<LanguageRequest> T = EntityTypes.T(LanguageRequest.class);

	String getCode();
	void setCode(String code);

	Language getSelect();
	void setSelect(Language data1);
}
