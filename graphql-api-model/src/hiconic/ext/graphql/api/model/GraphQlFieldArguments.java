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
// ============================================================================
// Braintribe IT-Technologies GmbH - www.braintribe.com
// Copyright Braintribe IT-Technologies GmbH, Austria, 2002-2015 - All Rights Reserved
// It is strictly forbidden to copy, modify, distribute or use this code without written permission
// To this file the Braintribe License Agreement applies.
// ============================================================================

package hiconic.ext.graphql.api.model;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Marker for entities that represent field arguments.
 * <p>
 * Example:
 * 
 * <pre>
 * type Movie {
 *   id: ID!
 *   reviews(
 *     page: Int = 0
 *     limit: Int = 50
 *   ): ReviewList!
 * }
 * 
 * type Query {
 *   movie(movieId: ID): Movie!
 * }
 * </pre>
 * 
 * Movie has a field called "reviews" which cannot be simply retrieved, but takes two arguments - page, limit.
 * <p>
 * 
 * Movie would be part of data model:
 * <pre>
 * entity Movie {
 * 	Object id_
 * 	ReviewList reviews
 * }
 * </pre>
 * 
 * API model would have three entities:
 * <pre>
 * entity Movie_reviews_args extends GraphQlFieldArguments {
 * 	Integer page = 50
 *	Integer limit = 0	
 * }
 *
 * entity Movie_withArgs extends Movie, HasGraphQlFieldArguments {
 * 	Movie_reviews_args reviews_args_
 * }
 * 
 * entity MovieRequest extends GraphQlRequest {
 * 	Object movieId
 * 	Movie select
 * }
 * </pre>
 *  
 * @author peter.gazdik
 */
@Abstract
public interface GraphQlFieldArguments extends GenericEntity {

	EntityType<GraphQlFieldArguments> T = EntityTypes.T(GraphQlFieldArguments.class);

}
