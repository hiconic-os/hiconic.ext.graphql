package hiconic.ext.graphql;

import java.lang.reflect.InvocationTargetException;

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.enhance.FieldAccessingPropertyAccessInterceptor;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.GenericModelType;
import com.braintribe.model.generic.reflection.Property;
import com.braintribe.model.generic.reflection.PropertyAccessInterceptor;

public class GraphQlQueries {

	/*
	 * Sets the absenceInformation on all properties of type AND attaches a
	 * FieldAccessingPropertyAccessInterceptor on read-access to remove the
	 * absenceInformation specifically on individual properties.
	 */
	static <E extends GenericEntity> E selectPrototype(EntityType<E> type) {

		PropertyAccessInterceptor pai = new PropertyAccessInterceptor() {

			// TODO: throw on setProperty ?
			@Override
			public Object setProperty(Property property, GenericEntity entity, Object value, boolean isVd) {

				// property.getType().areEntitiesReachable();

//				ValueDescriptor vd = (ValueDescriptor) super.getProperty(property, entity, true);
//				if (vd instanceof GraphQlInfo aux) {
//					aux.setValue(value);
//					return vd;
//				} else {
//					GraphQlInfo aux = GraphQlInfo.T.create();
//					aux.setValue(value);
//					aux.setSelect(false);
//					// aux.setCompareTo(value);
//					return super.setProperty(property, entity, aux, true);
//				}
				return super.setProperty(property, entity, value, isVd);
			}

			@Override
			public Object getProperty(Property property, GenericEntity entity, boolean isVd) {

				// set actual property to non-null
				Object value = super.getProperty(property, entity, isVd);
				if (value != null)
					return value;

				property.isNullable(); // throw
				GenericModelType type = property.getType();
				if (type.isEntity())
					return value; // no action. Need specific setting of GE

				Object defaultValue = type.getDefaultValue();
				if (defaultValue == null) {
					try {
						defaultValue = type.getJavaType().getConstructor(null).newInstance(null);
						if (defaultValue == null) {
							// this must not happen. error.
							System.err.println("nulling did not work in getter!");
						}

					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				super.setProperty(property, entity, defaultValue, isVd);
				return defaultValue;
			}
		};

		pai.next = FieldAccessingPropertyAccessInterceptor.INSTANCE;

		// initialize
		E instance = type.create(pai);
		for (Property p : type.getProperties()) {
			p.set(instance, null);
		}

		return instance;
	}
}
