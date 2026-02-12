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
package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BooleanExpressionTest {

  private final BooleanExpression a = new BooleanPath("a");
  private final BooleanExpression b = new BooleanPath("b");
  private final BooleanExpression c = new BooleanPath("c");

  @Test
  void anyOf() {
    assertThat(Expressions.anyOf(a, b, c)).isEqualTo(a.or(b).or(c));
  }

  @Test
  void allOf() {
    assertThat(Expressions.allOf(a, b, c)).isEqualTo(a.and(b).and(c));
  }

  @Test
  void allOf_with_nulls() {
    assertThat(Expressions.allOf(a, b, null)).hasToString("a && b");
    assertThat(Expressions.allOf(a, null)).hasToString("a");
    assertThat(Expressions.allOf(null, a)).hasToString("a");
  }

  @Test
  void anyOf_with_nulls() {
    assertThat(Expressions.anyOf(a, b, null)).hasToString("a || b");
    assertThat(Expressions.anyOf(a, null)).hasToString("a");
    assertThat(Expressions.anyOf(null, a)).hasToString("a");
  }

  @Test
  void andAnyOf() {
    assertThat(a.andAnyOf(b, c)).isEqualTo(a.and(b.or(c)));
  }

  @Test
  void orAllOf() {
    assertThat(a.orAllOf(b, c)).isEqualTo(a.or(b.and(c)));
  }

  @Test
  void not() {
    assertThat(a.not().not()).isEqualTo(a);
  }

  @Test
  void isTrue() {
    assertThat(a.isTrue()).isEqualTo(a.eq(true));
  }

  @Test
  void isFalse() {
    assertThat(a.isFalse()).isEqualTo(a.eq(false));
  }
}
