package hiconic.ext.graphql.api.model;

import com.braintribe.model.generic.GMF;
import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;
import com.braintribe.model.generic.value.ValueDescriptor;
import com.braintribe.model.service.api.ServiceRequest;

/**
 * The request to be converted into a GraphQL statement. The properties of the
 * "prototype" and its type hierarchy, including eventual (partial) assigned
 * values, are used for building a relatively basic GraphQL query.<br>
 * 
 * It is advised to use the static {@link #prototype()} method to generate
 * prototypes. This will setup property-access-interceptors to specifically
 * store information as needed for the GraphQL generation. For example,
 * absence-information and other value-descriptor information is needed. Manual
 * creation of prototypes will be complex.<br>
 * 
 * After a prototype is created, you can use the "setter" functions to set data
 * selection criteria, and "getter" methods to select output fields of the
 * query.<br> 
 * 
 * @author Ralf Ulrich
 *
 */
public interface QueryByPrototype extends ServiceRequest {
	EntityType<QueryByPrototype> T = EntityTypes.T(QueryByPrototype.class);

	String prototype = "prototype";
	String limit = "limit";

	GenericEntity getPrototype();
	void setPrototype(GenericEntity prototype);

	Integer getLimit();
	void setLimit(Integer limit);

	/*
	 * Sets the absenceInformation on all properties of type AND attaches a
	 * FieldAccessingPropertyAccessInterceptor on read-access to remove the
	 * absenceInformation specifically on individual properties.
	 */
	static <E extends GenericEntity> E prototype(EntityType<E> type) {
		PropertyAccessInterceptor pai = new PropertyAccessInterceptor() {

			@Override
			public Object setProperty(Property property, GenericEntity entity, Object value, boolean isVd) {

				// property.getType().areEntitiesReachable();
				
				ValueDescriptor vd = (ValueDescriptor) super.getProperty(property, entity, true);
				if (vd instanceof GraphQlInfo aux) {
					aux.setValue(value);
					return vd;
				} else {
					GraphQlInfo aux = GraphQlInfo.T.create();
					aux.setValue(value);
					aux.setSelect(false);
					// aux.setCompareTo(value);
					return super.setProperty(property, entity, aux, true);
				}
			}

			@Override
			public Object getProperty(Property property, GenericEntity entity, boolean isVd) {

				ValueDescriptor vd = (ValueDescriptor) super.getProperty(property, entity, true);
				if (isVd)
					return vd;
				if (vd instanceof GraphQlInfo aux) {
					aux.setSelect(true);
					Object value = aux.getValue();
					return value == null ? property.getDefaultValue() : value;
				} else {
					GraphQlInfo aux = GraphQlInfo.T.create();
					aux.setSelect(true);
					super.setProperty(property, entity, aux, true);
					return property.getDefaultValue();
				}
			}
		};

		// I had to comment this out, as it doesn't compile after removing the gm-core dependency
		//pai.next = FieldAccessingPropertyAccessInterceptor.INSTANCE;

		E instance = type.create(pai);

		for (Property p : type.getProperties()) {
			p.setAbsenceInformation(instance, GMF.absenceInformation());
		}

		throw new UnsupportedOperationException("This method doesn't compile anymore after removing gm-core. Needs to be extracted out of this model.");
		//return instance;
	}

}
