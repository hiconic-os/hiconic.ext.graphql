package hiconic.ext.graphql.test.model.gm.countries.queries.types;

import java.util.Date;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface Language extends GenericEntity {

	EntityType<Language> T = EntityTypes.T(Language.class);

	String getCode();
	void setCode(String code);
	
	String getName();
	void setName(String name);
	
	String getNative();
	void setNative(String native_);
	
	Boolean getRtl();
	void setRtl(Boolean rtl);
	
	// ................
	// this is for unit tests only. It is not part of the original graphQL endpoint
	Integer getTestInt();
	void setTestInt(Integer testInt);
	
	Date getTestDate();
	void setTestDate(Date testDate);
}
