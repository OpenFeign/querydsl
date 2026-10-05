/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
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
package com.querydsl.vector.apt;

import com.querydsl.codegen.AbstractModule;
import com.querydsl.codegen.CodegenModule;
import com.querydsl.codegen.Extension;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.Types;
import com.querydsl.vector.VectorPath;
import java.util.HashSet;
import java.util.Set;

/**
 * {@code VectorSupport} maps {@code float[]} properties to {@link VectorPath} in code generation
 */
public final class VectorSupport implements Extension {

  @Override
  public void addSupport(AbstractModule module) {
    var floatArray = Types.FLOAT_P.asArrayType();
    module
        .get(TypeMappings.class)
        .register(floatArray, new ClassType(VectorPath.class, floatArray));

    @SuppressWarnings("unchecked")
    Set<String> imports = new HashSet<>(module.get(Set.class, CodegenModule.IMPORTS));
    imports.add(VectorPath.class.getPackageName());
    module.bind(CodegenModule.IMPORTS, imports);
  }
}
