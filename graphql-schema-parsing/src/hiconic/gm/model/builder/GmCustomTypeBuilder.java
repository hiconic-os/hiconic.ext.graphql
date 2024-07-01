// ============================================================================
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
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
