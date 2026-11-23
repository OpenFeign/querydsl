/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fluentq.vector.apt;

import fluentq.codegen.AbstractModule;
import fluentq.codegen.CodegenModule;
import fluentq.codegen.Extension;
import fluentq.codegen.TypeMappings;
import fluentq.codegen.utils.model.ClassType;
import fluentq.codegen.utils.model.Types;
import fluentq.vector.VectorPath;
import java.util.HashSet;
import java.util.Set;

/**
 * {@code FloatArrayVectorPathExtension} maps {@code float[]} properties to {@link VectorPath} in
 * code generation
 */
public final class FloatArrayVectorPathExtension implements Extension {

  @Override
  public void addSupport(AbstractModule module) {
    module
        .get(TypeMappings.class)
        .register(Types.FLOAT_P.asArrayType(), new ClassType(VectorPath.class));

    @SuppressWarnings("unchecked")
    Set<String> imports = new HashSet<>(module.get(Set.class, CodegenModule.IMPORTS));
    imports.add(VectorPath.class.getPackageName());
    module.bind(CodegenModule.IMPORTS, imports);
  }
}
