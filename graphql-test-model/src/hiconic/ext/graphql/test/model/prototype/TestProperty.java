// ============================================================================
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
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
