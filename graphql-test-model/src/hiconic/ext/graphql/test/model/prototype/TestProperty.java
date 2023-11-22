package hiconic.ext.graphql.test.model.prototype;

import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface TestProperty extends GenericEntity {

	EntityType<TestProperty> T = EntityTypes.T(TestProperty.class);

	int getTestInt();
	void setTestInt(int testInt);

	String getTestString();
	void setTestString(String testString);
	
	boolean getTestFlag();
	void setTestFlag(boolean testFlag);

	List<Integer> getTestList();
	void setTestList(List<Integer> testList);
	
	TestProperty getTestPropertyInner();
	void setTestPropertyInner(TestProperty testPropertyInner);
}
