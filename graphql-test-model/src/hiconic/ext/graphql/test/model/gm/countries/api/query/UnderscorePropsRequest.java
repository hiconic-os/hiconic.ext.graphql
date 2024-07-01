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
package hiconic.ext.graphql.test.model.gm.countries.api.query;

import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.service.api.ServiceRequest;

import hiconic.ext.graphql.test.model.gm.countries.data.DataTypeWithUnderscores;

public interface UnderscorePropsRequest extends GraphQlTestQueryRequest {

	EntityType<UnderscorePropsRequest> T = EntityTypes.T(UnderscorePropsRequest.class);

	/* This should be just "id" in GraphQL */
	String getId_();
	void setId_(String id_);

	/* This should be just "name_" in GraphQL */
	String getName__();
	void setName__(String name__);

	DataTypeWithUnderscores getSelect();
	void setSelect(DataTypeWithUnderscores select);

	@Override
	EvalContext<DataTypeWithUnderscores> eval(Evaluator<ServiceRequest> evaluator);
}
