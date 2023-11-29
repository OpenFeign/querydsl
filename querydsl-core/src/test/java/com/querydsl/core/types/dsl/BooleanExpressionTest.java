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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BooleanExpressionTest {

  private final BooleanExpression a = new BooleanPath("a");
  private final BooleanExpression b = new BooleanPath("b");
  private final BooleanExpression c = new BooleanPath("c");

  @Test
  public void anyOf() {
    assertEquals(a.or(b).or(c), Expressions.anyOf(a, b, c));
  }

  @Test
  public void allOf() {
    assertEquals(a.and(b).and(c), Expressions.allOf(a, b, c));
  }

  @Test
  public void allOf_with_nulls() {
    assertEquals("a && b", Expressions.allOf(a, b, null).toString());
    assertEquals("a", Expressions.allOf(a, null).toString());
    assertEquals("a", Expressions.allOf(null, a).toString());
  }

  @Test
  public void anyOf_with_nulls() {
    assertEquals("a || b", Expressions.anyOf(a, b, null).toString());
    assertEquals("a", Expressions.anyOf(a, null).toString());
    assertEquals("a", Expressions.anyOf(null, a).toString());
  }

  @Test
  public void andAnyOf() {
    assertEquals(a.and(b.or(c)), a.andAnyOf(b, c));
  }

  @Test
  public void orAllOf() {
    assertEquals(a.or(b.and(c)), a.orAllOf(b, c));
  }

  @Test
  public void not() {
    assertEquals(a, a.not().not());
  }

  @Test
  public void isTrue() {
    assertEquals(a.eq(true), a.isTrue());
  }

  @Test
  public void isFalse() {
    assertEquals(a.eq(false), a.isFalse());
  }
}
