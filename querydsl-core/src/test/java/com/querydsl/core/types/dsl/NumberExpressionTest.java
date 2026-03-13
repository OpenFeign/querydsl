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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;

public class NumberExpressionTest {

  private NumberPath<Integer> intPath = new NumberPath<Integer>(Integer.class, "int");

  @Test
  public void between_start_given() {
    assertEquals(intPath.goe(1L), intPath.between(1L, null));
  }

  @Test
  public void between_end_given() {
    assertEquals(intPath.loe(3L), intPath.between(null, 3L));
  }

  @Test
  public void sumBigDecimal_has_bigDecimal_type() {
    assertEquals(BigDecimal.class, intPath.sumBigDecimal().getType());
  }

  @Test
  public void sumBigInteger_has_bigInteger_type() {
    assertEquals(BigInteger.class, intPath.sumBigInteger().getType());
  }

  @Test
  public void sumDouble_has_double_type() {
    assertEquals(Double.class, intPath.sumDouble().getType());
  }

  @Test
  public void sumLong_has_long_type() {
    assertEquals(Long.class, intPath.sumLong().getType());
  }

}
