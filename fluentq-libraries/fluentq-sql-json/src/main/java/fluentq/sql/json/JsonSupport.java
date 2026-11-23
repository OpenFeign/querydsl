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
package fluentq.sql.json;

import fluentq.codegen.AbstractModule;
import fluentq.codegen.CodegenModule;
import fluentq.codegen.Extension;
import fluentq.codegen.TypeMappings;
import fluentq.codegen.utils.model.ClassType;
import fluentq.codegen.utils.model.SimpleType;
import fluentq.codegen.utils.model.TypeCategory;
import fluentq.sql.json.types.JSONType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** {@code JsonSupport} provides support for j types in code generation */
public final class JsonSupport implements Extension {

  private static void registerTypes(TypeMappings typeMappings) {
    typeMappings.register(
        new ClassType(TypeCategory.MAP, JsonEntity.class),
        new SimpleType(JSONType.class.getSimpleName()));
  }

  private static void addImports(AbstractModule module, String packageName) {
    @SuppressWarnings("unchecked")
    Set<String> imports = module.get(Set.class, CodegenModule.IMPORTS);
    if (imports.isEmpty()) {
      imports = Collections.singleton(packageName);
    } else {
      var old = imports;
      imports = new HashSet<>(old);
      imports.add(packageName);
    }
    module.bind(CodegenModule.IMPORTS, imports);
  }

  /**
   * Register spatial types to the given codegen module
   *
   * @param module module to be customized for spatial support
   */
  @Override
  public void addSupport(AbstractModule module) {
    registerTypes(module.get(TypeMappings.class));
    addImports(module, "fluentq.sql.json.types");
    addImports(module, "fluentq.sql.json");
  }
}
