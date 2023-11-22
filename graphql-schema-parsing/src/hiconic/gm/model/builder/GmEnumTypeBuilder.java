package hiconic.gm.model.builder;

import com.braintribe.model.meta.GmEnumType;

/**
 * @author peter.gazdik
 */
public class GmEnumTypeBuilder extends GmCustomTypeBuilder<GmEnumType> {

	public GmEnumTypeBuilder(GmModelBuilder modelBuilder, String typeSignature) {
		super(modelBuilder, typeSignature, GmEnumType.T);
	}

	public GmEnumConstantBuilder createConstant(String name) {
		return new GmEnumConstantBuilder(this, name);
	}

	@Override
	public void finishAndVerify() {
		if (it.getConstants().isEmpty())
			throw new IllegalStateException("Enum type '" + typeSignature + "' has no constants.");
	}

}
