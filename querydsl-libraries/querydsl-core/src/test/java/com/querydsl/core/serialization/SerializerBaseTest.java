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
package com.querydsl.core.serialization;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.JavaTemplates;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SerializerBaseTest {

  @Test
  void test() {
    var serializer = new DummySerializer(new JavaTemplates());
    var strPath = Expressions.stringPath("str");
    // path
    serializer.handle(strPath);
    // operation
    serializer.handle(strPath.isNotNull());
    // long path
    serializer.handle(new PathBuilder<>(Object.class, "p").getList("l", Map.class).get(0));
    // constant
    serializer.handle(ConstantImpl.create(""));
    //  custom
    serializer.handle(ExpressionUtils.template(Object.class, "xxx", ConstantImpl.create("")));
  }

  /**
   * Constants are labelled by identity, so two equal but distinct instances are bound as two
   * separate parameters. Boxed values outside the {@link Long} cache are distinct instances today;
   * once the JDK migrates the wrappers to value classes (JEP 401) {@code ==} becomes state based
   * and this collapses to a single label.
   */
  @Test
  void equalButDistinctConstantsGetDistinctLabels() {
    var serializer = new DummySerializer(new JavaTemplates());
    Long first = 1000L;
    Long second = 1000L;
    assertThat(first).isNotSameAs(second).isEqualTo(second);

    serializer.handle((Object) first);
    serializer.handle((Object) second);

    assertThat(serializer.getConstants()).containsExactly(first, second);
    assertThat(serializer.getConstantToLabel()).hasSize(2);
    assertThat(serializer).hasToString("a1a2");
  }

  @Test
  void repeatedConstantInstanceReusesLabel() {
    var serializer = new DummySerializer(new JavaTemplates());
    Long value = 1000L;

    serializer.handle((Object) value);
    serializer.handle((Object) value);

    assertThat(serializer.getConstants()).containsExactly(value, value);
    assertThat(serializer.getConstantToLabel()).hasSize(1);
    assertThat(serializer).hasToString("a1a1");
  }
}
