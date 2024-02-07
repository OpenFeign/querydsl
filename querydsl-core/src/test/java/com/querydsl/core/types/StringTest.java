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
import org.junit.Test;

public class StringTest {

  private static class DummyTemplates extends Templates {}

  @SuppressWarnings("unchecked")
  @Test
  public void patternAvailability() throws IllegalArgumentException, IllegalAccessException {
    Templates ops = new DummyTemplates();
    Set<Field> missing = new HashSet<Field>();
    for (Field field : Ops.class.getFields()) {
      if (field.getType().equals(Operator.class)) {
        Operator op = (Operator) field.get(null);
        if (ops.getTemplate(op) == null) {
          missing.add(field);
        }
      }
    }
    for (Class<?> cl : Ops.class.getClasses()) {
      for (Field field : cl.getFields()) {
        if (field.getType().equals(Operator.class)) {
          Operator op = (Operator) field.get(null);
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
  public void toString_() {
    SomeType alias = alias(SomeType.class, "alias");

    // Path toString
    assertThat($(alias.getName()).toString()).isEqualTo("alias.name");
    assertThat($(alias.getRef().getName()).toString()).isEqualTo("alias.ref.name");
    assertThat($(alias.getRefs().get(0)).toString()).isEqualTo("alias.refs.get(0)");

    // Operation toString
    assertThat($(alias.getName()).lower().toString()).isEqualTo("lower(alias.name)");

    // ConstructorExpression
    ConstructorExpression<SomeType> someType =
        new ConstructorExpression<SomeType>(
            SomeType.class, new Class<?>[] {SomeType.class}, $(alias));
    assertThat(someType.toString()).isEqualTo("new SomeType(alias)");

    // ArrayConstructorExpression
    ArrayConstructorExpression<SomeType> someTypeArray =
        new ArrayConstructorExpression<SomeType>(SomeType[].class, $(alias));
    assertThat(someTypeArray.toString()).isEqualTo("new SomeType[](alias)");
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
