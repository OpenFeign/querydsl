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
package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.junit.Test;

public class CastTest extends AbstractQueryTest {

  private static NumberExpression<Integer> expr = Expressions.numberPath(Integer.class, "int");

  @Test
  public void bytes() {
    assertThat(expr.byteValue().getType()).isEqualTo(Byte.class);
  }

  @Test
  public void doubles() {
    assertThat(expr.doubleValue().getType()).isEqualTo(Double.class);
  }

  @Test
  public void floats() {
    assertThat(expr.floatValue().getType()).isEqualTo(Float.class);
  }

  @Test
  public void integers() {
    assertThat(expr.intValue().getType()).isEqualTo(Integer.class);
  }

  @Test
  public void longs() {
    assertThat(expr.longValue().getType()).isEqualTo(Long.class);
  }

  @Test
  public void shorts() {
    assertThat(expr.shortValue().getType()).isEqualTo(Short.class);
  }

  @Test
  public void stringCast() {
    assertThat(expr.stringValue().getType()).isEqualTo(String.class);
  }
}
