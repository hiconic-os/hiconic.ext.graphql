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
 * @see GraphQlFieldArguments
 * 
 * @author peter.gazdik
 */
@Abstract
public interface HasGraphQlFieldArguments extends GenericEntity {

	EntityType<HasGraphQlFieldArguments> T = EntityTypes.T(HasGraphQlFieldArguments.class);

}
