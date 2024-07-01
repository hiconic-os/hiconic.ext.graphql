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
