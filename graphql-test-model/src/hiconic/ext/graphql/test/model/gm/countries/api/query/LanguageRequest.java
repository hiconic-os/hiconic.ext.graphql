package hiconic.ext.graphql.test.model.gm.countries.api.query;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.test.model.gm.countries.api.typeconditions.HasNameAndCode_TypeConditions;
import hiconic.ext.graphql.test.model.gm.countries.data.HasNameAndCode;
import hiconic.ext.graphql.test.model.gm.countries.data.Language;

public interface LanguageRequest extends GraphQlTestQueryRequest {

	EntityType<LanguageRequest> T = EntityTypes.T(LanguageRequest.class);

	String getCode();
	void setCode(String code);

	/**
	 * To generate a valid query use {@link Language} (or {@link HasNameAndCode_TypeConditions}) with {@link Language} TC.
	 * <p>
	 * But for testing the generated string, use anything else.
	 */
	HasNameAndCode getSelect();
	void setSelect(HasNameAndCode data1);

}
