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
package hiconic.ext.graphql.reason.model.api.error;

import java.util.List;
import java.util.Map;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface GraphQlError extends GenericEntity {
	EntityType<GraphQlError> T = EntityTypes.T(GraphQlError.class);

	String getMessage();
	void setMessage(String message);

	List<Object> getPath();
	void setPath(List<Object> path);

	List<GraphQlLocation> getLocations();
	void setLocations(List<GraphQlLocation> locations);

	Map<String, GraphQlErrorExtension> getExtensions();
	void setExtensions(Map<String, GraphQlErrorExtension> extensions);
}
