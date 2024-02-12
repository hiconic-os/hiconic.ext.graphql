package hiconic.ext.graphql;

import java.util.function.BiFunction;

import com.braintribe.codec.marshaller.api.PropertyTypeInferenceOverride;
import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.gm.model.reason.Reasons;
import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.pr.criteria.EntityCriterion;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityVisitor;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.TraversingContext;
import com.braintribe.model.processing.service.api.ProceedContext;
import com.braintribe.model.processing.service.api.ReasonedServiceAroundProcessor;
import com.braintribe.model.processing.service.api.ServiceRequestContext;

import hiconic.ext.graphql.api.model.GraphQlRequest;
import hiconic.ext.graphql.api.model.result.GraphQlListValue;
import hiconic.ext.graphql.api.model.result.GraphQlResult;
import hiconic.ext.graphql.api.model.result.GraphQlSingleValue;
import hiconic.ext.graphql.reason.model.GraphQlServerError;

public class GraphQlAroundProcessor implements ReasonedServiceAroundProcessor<GraphQlRequest, Object> {

	private static Property dataProperty = GraphQlResult.T.getProperty(GraphQlResult.data);
	private static Property entityResultProperty = GraphQlSingleValue.T.getProperty(GraphQlSingleValue.value);
	private static Property listResultProperty = GraphQlListValue.T.getProperty(GraphQlListValue.value);

	@Override
	public Maybe<? extends Object> processReasoned(ServiceRequestContext context, GraphQlRequest request, ProceedContext proceedContext) {

		Property selectProperty = request.entityType().findProperty("select");
		// handle error

		GenericEntity select = selectProperty.get(request); // error handling, type checking

		EntityType<?> entityType = select.entityType();

		GenericModelType evaluatesTo = entityType.getEvaluatesTo();

		// error handling: select type - evaluatesTo

		boolean single = !entityType.isCollection();

		final BiFunction<EntityType<?>, Property, GenericModelType> inference = single ? //
				(t, p) -> singleInference(p, entityType) : //
				(t, p) -> multiInference(p, entityType);

		ServiceRequestContext enrichedContext = context.derive() //
				.set(PropertyTypeInferenceOverride.class, inference) //
				// todo: add id-translation. PropertyDeserializationTranslation
				.build();

		GraphQlResult graphQlResult = proceedContext.proceed(enrichedContext, request);

		if (!graphQlResult.getErrors().isEmpty()) {
			return Reasons.build(GraphQlServerError.T).enrich(r -> r.setErrors(graphQlResult.getErrors())).toMaybe();
		}
		Object data = graphQlResult.getData();

		final Object resultObject;
		if (single) {
			GraphQlSingleValue singularResult = (GraphQlSingleValue) data;
			resultObject = singularResult.getValue();
		} else {
			GraphQlListValue listResult = (GraphQlListValue) data;
			resultObject = listResult.getValue();
		}

		if (resultObject instanceof GenericEntity ge) { // TODO: does this work for collections ?

			// special properties handling
			ge.type().traverse(ge, null, new EntityVisitor() {
				@Override
				protected void visitEntity(GenericEntity entity, EntityCriterion criterion, TraversingContext traversingContext) {
					EntityType<GenericEntity> type = entity.entityType();
					Property idProperty = type.findProperty("id");
					Property idUnderscoreProperty = type.findProperty("id_");
					if (idProperty != null && idUnderscoreProperty != null) {
						Object idValue = idProperty.get(entity);
						if (idValue != null) {
							idUnderscoreProperty.set(entity, idValue);
							idProperty.set(entity, null);
						}
					}
				}
			});
		}

		return Maybe.complete(resultObject);
	}

	private GenericModelType singleInference(Property p, EntityType<?> type) {
		if (p == dataProperty)
			return GraphQlSingleValue.T;
		else if (p == entityResultProperty)
			return type;

		return null;
	}

	private GenericModelType multiInference(Property p, EntityType<?> type) {
		if (p == dataProperty)
			return GraphQlListValue.T;
		else if (p == listResultProperty)
			return GMF.getTypeReflection().getListType(type);

		return null;
	}
}
