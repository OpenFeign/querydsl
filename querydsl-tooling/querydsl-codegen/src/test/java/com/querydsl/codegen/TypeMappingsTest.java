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

import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class TypeMappingsTest {

  static class Entity {}

  @Test
  public void getPathType_of_innerClass() {
    TypeMappings typeMappings = new JavaTypeMappings();
    var model = new EntityType(new ClassType(TypeMappingsTest.class));
    var type = new EntityType(new ClassType(Entity.class));
    typeMappings.register(type, new QueryTypeFactoryImpl("Q", "", "").create(type));

    var pathType = typeMappings.getPathType(type, model, false);
    assertThat(pathType.getSimpleName()).isEqualTo("QTypeMappingsTest_Entity");
  }

  @Test
  public void isRegistered() {
    TypeMappings typeMappings = new JavaTypeMappings();
    typeMappings.register(new ClassType(Double[].class), new ClassType(Point.class));
    assertThat(typeMappings.isRegistered(new ClassType(Double[].class))).isTrue();
  }

  @Test
  public void testGenericTypeRegistration() {
    var rawListType = new SimpleType(List.class.getName());
    var integerListType =
        new SimpleType(
            rawListType, Collections.<Type>singletonList(new SimpleType(Integer.class.getName())));
    var longListType =
        new SimpleType(
            rawListType, Collections.<Type>singletonList(new SimpleType(Long.class.getName())));

    var integerListTypeExpression = new SimpleType("integerListTypeExpression");
    var longListTypeExpression = new SimpleType("longListTypeExpression");

    TypeMappings typeMappings = new JavaTypeMappings();
    typeMappings.register(integerListType, integerListTypeExpression);
    typeMappings.register(longListType, longListTypeExpression);

    assertThat(typeMappings.getExprType(integerListType, null, false))
        .isEqualTo(integerListTypeExpression);
    assertThat(typeMappings.getExprType(longListType, null, false))
        .isEqualTo(longListTypeExpression);
  }
}
