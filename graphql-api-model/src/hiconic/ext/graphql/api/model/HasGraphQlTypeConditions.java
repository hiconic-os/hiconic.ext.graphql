// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
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
package hiconic.ext.graphql.api.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Marker for a base type to support type conditions on polymorphic properties.
 * <p>
 * Example:
 * <p>
 * <b>Schema:</b>
 * 
 * <pre>
 * interface Tool {
 *   toolName: String
 * }
 * 
 * type Hammer implements Tool {
 *   weight: Integer!
 *   (more props...)
 * }
 * 
 * type Thermometer implements Tool {
 *   minTemperature: Integer!
 *   maxTemperature: Integer!
 *   (more props...)
 * }
 *
 * type Worker {
 * 	 name: String
 * 	 tool: Tool
 * }
 * </pre>
 * 
 * <b>GraphQl query with type conditions:</b>
 * 
 * <pre>
 * query {
 * 	value: workerByName(name: "John Smith") {
 *  	name
 *  	tool {
 *  		toolName
 *  		... on Hammer {
 *  			weight
 *  		}
 *  		... on Thermometer {
 *  			minTemperature
 *  			maxTemperature
 *  		}
 *  	}
 *  }
 * }
 * </pre>
 * 
 * <b>API Model</b>
 * <p>
 * In order to support these type conditions, a special entity type is created for each polymorphic property type, in this case <i>Tool</i>.
 * 
 * <pre>
 * entity Tool_TypeConditions extends Tool, HasGraphQlTypeConditions {
 * 	List&lt;Tool&gt; typeConditions_	
 * }
 * </pre>
 * 
 * <b>Equivalent type conditions with entities:</b>
 * 
 * <pre>
 * Hammer hammer = Hammer.T.create();
 * hammer.setWeight(0);
 * 
 * Thermometer thermometer = Thermometer.T.create();
 * thermometer.setMinTemperature(0);
 * thermometer.setMaxTemperature(0);
 *
 * Tool_TypeConditions tool = Tool_TypeConditions.T.create();
 * tool.setToolName("");
 * tool.getTypeConditions_().add(hammer);
 * tool.getTypeConditions_().add(thermometer);
 * </pre>
 * 
 * @see "https://spec.graphql.org/October2021/#sec-Type-Conditions"
 * 
 * @author peter.gazdik
 */
@Abstract
public interface HasGraphQlTypeConditions extends GenericEntity {

	EntityType<HasGraphQlTypeConditions> T = EntityTypes.T(HasGraphQlTypeConditions.class);

	String TYPE_CONDITIONS_PROPERTY_NAME = "typeConditions_";

}
