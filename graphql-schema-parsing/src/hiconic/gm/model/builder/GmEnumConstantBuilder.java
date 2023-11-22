package hiconic.gm.model.builder;

import com.braintribe.model.meta.GmEnumConstant;

/**
 * @author peter.gazdik
 */
public class GmEnumConstantBuilder extends GmModelElementBuilder<GmEnumConstant> {

	public GmEnumConstantBuilder(GmEnumTypeBuilder typeBuilder, String name) {
		super(GmEnumConstant.T);
		it.setName(name);
		it.setGlobalId("enum:" + typeBuilder.it.getTypeSignature() + "/" + name);
		it.setDeclaringType(typeBuilder.it);

		typeBuilder.it.getConstants().add(it);
	}

}
