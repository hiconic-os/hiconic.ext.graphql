package hiconic.ext.graphql.test.model.gm.countries.data;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/* this is not a full implementation of the original data type */

public interface Country extends HasNameAndCode {

	EntityType<Country> T = EntityTypes.T(Country.class);

	String getCapital();
	void setCapital(String capital);

	List<String> getStates();
	void setStates(List<String> states);

}
