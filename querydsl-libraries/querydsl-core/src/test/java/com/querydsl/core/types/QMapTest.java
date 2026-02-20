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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class QMapTest {

  StringPath str1 = Expressions.stringPath("str1");
  StringPath str2 = Expressions.stringPath("str2");
  StringPath str3 = Expressions.stringPath("str3");
  StringPath str4 = Expressions.stringPath("str4");
  Expression<?>[] exprs1 = new Expression[] {str1, str2};
  Expression<?>[] exprs2 = new Expression[] {str3, str4};

  Concatenation concat = new Concatenation(str1, str2);

  @Test
  void twoExpressions_getArgs() {
    assertThat(new QMap(str1, str2).getArgs()).containsExactlyElementsOf(Arrays.asList(str1, str2));
  }

  @Test
  void oneArray_getArgs() {
    assertThat(new QMap(exprs1).getArgs()).containsExactlyElementsOf(Arrays.asList(str1, str2));
  }

  @Test
  void twoExpressionArrays_getArgs() {
    assertThat(new QMap(exprs1, exprs2).getArgs())
        .containsExactlyElementsOf(Arrays.asList(str1, str2, str3, str4));
  }

  @Test
  void nestedProjection_getArgs() {
    assertThat(FactoryExpressionUtils.wrap(new QMap(concat)).getArgs())
        .containsExactlyElementsOf(Arrays.asList(str1, str2));
  }

  @Test
  void nestedProjection_getArgs2() {
    assertThat(FactoryExpressionUtils.wrap(new QMap(concat, str3)).getArgs())
        .containsExactlyElementsOf(Arrays.asList(str1, str2, str3));
  }

  @Test
  void nestedProjection_newInstance() {
    var expr = new QMap(concat);
    var result = FactoryExpressionUtils.wrap(expr).newInstance("12", "34");
    assertThat(result)
        .anySatisfy(
            (key, value) -> {
              assertThat(key).isEqualTo(concat);
              assertThat(value).isEqualTo("1234");
            });
  }

  @Test
  void nestedProjection_newInstance2() {
    var expr = new QMap(str1, str2, concat);
    var result = FactoryExpressionUtils.wrap(expr).newInstance("1", "2", "12", "34");
    assertThat(result)
        .anySatisfy(
            (key, value) -> {
              assertThat(key).isEqualTo(concat);
              assertThat(value).isEqualTo("1234");
            });
  }

  @Test
  void tuple_equals() {
    var expr = new QMap(str1, str2);
    assertThat(expr.newInstance("str1", "str2")).isEqualTo(expr.newInstance("str1", "str2"));
  }

  @Test
  void tuple_hashCode() {
    var expr = new QMap(str1, str2);
    assertThat(expr.newInstance("str1", "str2"))
        .hasSameHashCodeAs(expr.newInstance("str1", "str2"));
  }

  @Test
  void null_value() {
    var expr = new QMap(str1, str2);
    assertThat(expr.newInstance("str1", null)).isNotNull();
  }
}
