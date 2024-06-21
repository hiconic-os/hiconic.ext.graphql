package hiconic.ext.graphql.test.model.gm.countries.data;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/* this is not a full implementation of the original data type */

public interface Continent extends GenericEntity {

	EntityType<Continent> T = EntityTypes.T(Continent.class);

	String getCode();
	void setCode(String code);

	List<Country> getCountries();
	void setCountries(List<Country> countries);

	String getName();
	void setName(String name);
}
