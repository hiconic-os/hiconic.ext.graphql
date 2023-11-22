package hiconic.ext.graphql.test.model.prototype;

import java.util.Date;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface TestPrototype extends GenericEntity {

	EntityType<TestPrototype> T = EntityTypes.T(TestPrototype.class);

	int getTestInt();
	void setTestInt(int testInt);

	String getTestString();
	void setTestString(String testString);

	TestProperty getTestProperty();
	void setTestProperty(TestProperty testProperty);
	
	boolean getTestFlag();
	void setTestFlag(boolean testFlag);
	
	Date getTestDate();
	void setTestDate(Date testDate);

	List<TestProperty> getListProps();
	void setListProps(List<TestProperty> listProps);

}
