package hiconic.gm.model.builder;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmCustomType;
import com.braintribe.utils.lcd.NullSafe;

/**
 * @author peter.gazdik
 */
public abstract class GmCustomTypeBuilder<T extends GmCustomType> extends GmModelElementBuilder<T> {

	protected final GmModelBuilder modelBuilder;
	protected final String typeSignature;

	public GmCustomTypeBuilder(GmModelBuilder modelBuilder, String typeSignature, EntityType<T> gmTypeType) {
		super(gmTypeType);

		this.modelBuilder = NullSafe.nonNull(modelBuilder, "modelBuilder");
		this.typeSignature = NullSafe.nonNull(typeSignature, "typeSignature");

		it.setTypeSignature(typeSignature);
		it.setGlobalId("type:" + typeSignature);
		it.setDeclaringModel(modelBuilder.newModel);
	}

	public abstract void finishAndVerify();

}
