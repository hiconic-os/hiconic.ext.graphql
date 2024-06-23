package hiconic.ext.graphql.test.model.gm.countries.data;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/* this is not a full implementation of the original data type */

public interface Continent extends HasNameAndCode {

	EntityType<Continent> T = EntityTypes.T(Continent.class);

	List<Country> getCountries();
	void setCountries(List<Country> countries);

}
