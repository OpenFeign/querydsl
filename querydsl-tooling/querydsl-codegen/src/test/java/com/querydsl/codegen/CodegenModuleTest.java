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
package com.querydsl.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import org.junit.Test;

public class CodegenModuleTest {

  private final CodegenModule module = new CodegenModule();

  @Test
  public void defaultPrefix() {
    assertThat(module.get(String.class, CodegenModule.PREFIX)).isEqualTo("Q");
  }

  @Test
  public void typeMappings() {
    assertThat(module.get(TypeMappings.class)).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void get_with_unknown_key() {
    module.get(String.class, "XXX");
  }

  @Test
  public void defaultGeneratedClass() {
    Class<? extends Annotation> o =
        module.get(Class.class, CodegenModule.GENERATED_ANNOTATION_CLASS);
    assertThat(GeneratedAnnotationResolver.resolveDefault()).isEqualTo(o);
  }

  @Test
  public void javadocSuffixForBeanSerializerOverloadedConstructorInjection() {
    var o = module.get(String.class, CodegenModule.JAVADOC_SUFFIX);
    assertThat(BeanSerializer.DEFAULT_JAVADOC_SUFFIX).isEqualTo(o);
  }
}
