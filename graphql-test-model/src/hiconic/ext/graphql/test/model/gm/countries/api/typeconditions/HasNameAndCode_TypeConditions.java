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
package hiconic.ext.graphql.test.model.gm.countries.api.typeconditions;

import java.util.List;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.HasGraphQlTypeConditions;
import hiconic.ext.graphql.test.model.gm.countries.data.HasNameAndCode;

public interface HasNameAndCode_TypeConditions extends HasNameAndCode, HasGraphQlTypeConditions {

	EntityType<HasNameAndCode_TypeConditions> T = EntityTypes.T(HasNameAndCode_TypeConditions.class);

	List<HasNameAndCode> getTypeConditions_();
	void setTypeConditions_(List<HasNameAndCode> typeConditions_);

}
