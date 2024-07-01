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
