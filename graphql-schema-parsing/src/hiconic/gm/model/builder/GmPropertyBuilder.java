package hiconic.gm.model.builder;

import com.braintribe.model.meta.GmProperty;
import com.braintribe.model.meta.GmType;
import com.braintribe.model.meta.data.constraint.Mandatory;

/**
 * @author peter.gazdik
 */
public class GmPropertyBuilder extends GmModelElementBuilder<GmProperty> {

	public GmPropertyBuilder(GmEntityTypeBuilder typeBuilder, String name, GmType type) {
		super(GmProperty.T);

		it.setName(name);
		it.setGlobalId("property:" + typeBuilder.it.getTypeSignature() + "/" + name);
		it.setType(type);
		it.setDeclaringType(typeBuilder.it);

		typeBuilder.it.getProperties().add(it);
	}

	public void markMandatory() {
		addMd(newMd(Mandatory.T));
	}

}
