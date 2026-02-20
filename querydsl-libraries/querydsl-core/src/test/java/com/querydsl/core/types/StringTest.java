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
package com.querydsl.core.types;

import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StringTest {

  private static class DummyTemplates extends Templates {}

  @SuppressWarnings("unchecked")
  @Test
  void patternAvailability() throws Exception {
    Templates ops = new DummyTemplates();
    Set<Field> missing = new HashSet<>();
    for (Field field : Ops.class.getFields()) {
      if (field.getType().equals(Operator.class)) {
        var op = (Operator) field.get(null);
        if (ops.getTemplate(op) == null) {
          missing.add(field);
        }
      }
    }
    for (Class<?> cl : Ops.class.getClasses()) {
      for (Field field : cl.getFields()) {
        if (field.getType().equals(Operator.class)) {
          var op = (Operator) field.get(null);
          if (ops.getTemplate(op) == null) {
            missing.add(field);
          }
        }
      }
    }

    if (!missing.isEmpty()) {
      for (Field field : missing) {
        System.err.println(field.getName());
      }
      fail("");
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  void toString_() {
    var alias = alias(SomeType.class, "alias");

    // Path toString
    assertThat($(alias.getName())).hasToString("alias.name");
    assertThat($(alias.getRef().getName())).hasToString("alias.ref.name");
    assertThat($(alias.getRefs().get(3))).hasToString("alias.refs.get(3)");
    assertThat($(alias.getRefs().getFirst())).hasToString("alias.refs.getFirst()");

    // Operation toString
    assertThat($(alias.getName()).lower()).hasToString("lower(alias.name)");

    // ConstructorExpression
    var someType =
        new ConstructorExpression<>(SomeType.class, new Class<?>[] {SomeType.class}, $(alias));
    assertThat(someType).hasToString("new SomeType(alias)");

    // ArrayConstructorExpression
    var someTypeArray = new ArrayConstructorExpression<>(SomeType[].class, $(alias));
    assertThat(someTypeArray).hasToString("new SomeType[](alias)");
  }

  public static class SomeType {

    public SomeType() {}

    public SomeType(SomeType st) {}

    public String getName() {
      return "";
    }

    public SomeType getRef() {
      return null;
    }

    public List<SomeType> getRefs() {
      return null;
    }

    public int getAmount() {
      return 0;
    }
  }
}
