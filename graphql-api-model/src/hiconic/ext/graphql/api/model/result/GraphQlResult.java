package hiconic.ext.graphql.api.model.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

// TODO> remove "prototype"

public interface GraphQlResult extends GenericEntity {
	EntityType<GraphQlResult> T = EntityTypes.T(GraphQlResult.class);

	String data = "data";

	GraphQlValue getData();
	void setData(GraphQlValue data);

//	default <E extends GenericEntity> List<E> results() {
//		GraphQlValue data = getData();
//
//		if (data instanceof GraphQlSingleValue) {
//			return new ArrayList<>(Arrays.asList((E) ((GraphQlSingleValue) data).getValue()));
//		} else {
//			return (List<E>) (List<?>) ((GraphQlListValue) data).getValue();
//		}
//	}
//
//	default <E extends GenericEntity> E result() {
//		GraphQlValue data = getData();
//
//		if (data instanceof GraphQlSingleValue) {
//			return (E) ((GraphQlSingleValue) data).getValue();
//		} else {
//			List<E> results = ((List<E>) (List<?>) ((GraphQlListValue) data).getValue());
//			return results.isEmpty() ? null : results.get(0);
//		}
//	}
}
