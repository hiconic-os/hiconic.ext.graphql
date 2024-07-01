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

import java.util.List;

import com.braintribe.model.meta.GmEntityType;
import com.braintribe.model.meta.GmType;

/**
 * @author peter.gazdik
 */
public class GmEntityTypeBuilder extends GmCustomTypeBuilder<GmEntityType> {

	public GmEntityTypeBuilder(GmModelBuilder modelBuilder, String typeSignature) {
		super(modelBuilder, typeSignature, GmEntityType.T);
	}

	public void addSuperType(GmEntityType superType) {
		// possibly verify no circular deps are being created
		it.getSuperTypes().add(superType);
	}

	public GmPropertyBuilder createProperty(String name, GmType type) {
		return new GmPropertyBuilder(this, name, type);
	}

	public void markAbstract(boolean isAbstract) {
		it.setIsAbstract(isAbstract);
	}

	@Override
	public void finishAndVerify() {
		ensureSuperType();
	}

	private void ensureSuperType() {
		List<GmEntityType> superTypes = it.getSuperTypes();
		if (superTypes.isEmpty())
			superTypes.add(modelBuilder.genericEntityType());
	}

}
