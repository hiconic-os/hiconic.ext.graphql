package hiconic.gm.model.builder;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.meta.GmModelElement;
import com.braintribe.model.meta.data.HasMetaData;
import com.braintribe.model.meta.data.MetaData;

/**
 * @author peter.gazdik
 */
public abstract class GmModelElementBuilder<E extends GmModelElement & HasMetaData> {

	public final E it;

	public GmModelElementBuilder(EntityType<E> itType) {
		this.it = itType.create();
	}

	protected void addMd(MetaData md) {
		it.getMetaData().add(md);
	}

	protected <M extends MetaData> M newMd(EntityType<M> mdType) {
		M md = mdType.create();
		md.setGlobalId(mdType.getShortName() + ":" + it.getGlobalId());
		return md;
	}
}
