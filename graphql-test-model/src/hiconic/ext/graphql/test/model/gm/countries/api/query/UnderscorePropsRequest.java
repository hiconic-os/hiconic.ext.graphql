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
