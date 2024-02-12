package hiconic.ext.graphql.reason.model;

import com.braintribe.gm.model.reason.Reason;
import com.braintribe.model.generic.annotation.Abstract;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * Common {@link Reason} base interface to identify issues related to the
 * graphql extension.
 * 
 * @author Ralf Ulrich
 *
 */
@Abstract
public interface GraphQlReason extends Reason {

	EntityType<GraphQlReason> T = EntityTypes.T(GraphQlReason.class);
}
