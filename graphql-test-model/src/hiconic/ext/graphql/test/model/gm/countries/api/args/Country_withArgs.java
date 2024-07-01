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
package hiconic.ext.graphql.test.model.gm.countries.api.args;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

import hiconic.ext.graphql.api.model.HasGraphQlFieldArguments;
import hiconic.ext.graphql.test.model.gm.countries.data.Country;

/* this is not a full implementation of the original data type */

public interface Country_withArgs extends Country, HasGraphQlFieldArguments {

	EntityType<Country_withArgs> T = EntityTypes.T(Country_withArgs.class);

	Country_name_args getName_args_();
	void setName_args_(Country_name_args name_args_);
}
