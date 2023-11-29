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

import static com.querydsl.jpa.Constants.*;
import static com.querydsl.jpa.JPAExpressions.selectFrom;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.BooleanBuilder;
import org.junit.Test;

public class BooleanOperationsTest extends AbstractQueryTest {

  @Test
  public void booleanOperations_or() {
    assertToString("cust is null or cat is null", cust.isNull().or(cat.isNull()));
  }

  @Test
  public void booleanOperations_and() {
    assertToString("cust is null and cat is null", cust.isNull().and(cat.isNull()));
  }

  @Test
  public void booleanOperations_not() {
    assertToString("not cust is null", cust.isNull().not());
  }

  @Test
  public void booleanOperations2_and() {
    cat.name.eq(cust.name.firstName).and(cat.bodyWeight.eq(kitten.bodyWeight));
  }

  @Test
  public void booleanOperations2_or() {
    cat.name.eq(cust.name.firstName).or(cat.bodyWeight.eq(kitten.bodyWeight));
  }

  @Test
  public void logicalOperations_or() {
    assertToString("cat = kitten or kitten = cat", cat.eq(kitten).or(kitten.eq(cat)));
  }

  @Test
  public void logicalOperations_and() {
    assertToString("cat = kitten and kitten = cat", cat.eq(kitten).and(kitten.eq(cat)));
  }

  @Test
  public void logicalOperations_and2() {
    assertToString(
        "cat is null and (kitten is null or kitten.bodyWeight > ?1)",
        cat.isNull().and(kitten.isNull().or(kitten.bodyWeight.gt(10))));
  }

  @Test
  public void booleanBuilder1() {
    BooleanBuilder bb1 = new BooleanBuilder();
    bb1.and(cat.eq(cat));

    BooleanBuilder bb2 = new BooleanBuilder();
    bb2.or(cat.eq(cat));
    bb2.or(cat.eq(cat));

    assertToString("cat = cat and (cat = cat or cat = cat)", bb1.and(bb2));
  }

  @Test
  public void booleanBuilder2() {
    BooleanBuilder bb1 = new BooleanBuilder();
    bb1.and(cat.eq(cat));

    BooleanBuilder bb2 = new BooleanBuilder();
    bb2.or(cat.eq(cat));
    bb2.or(cat.eq(cat));

    assertToString("cat = cat and (cat = cat or cat = cat)", bb1.and(bb2.getValue()));
  }

  @Test
  public void booleanBuilder_with_null_in_where() {
    assertThat(selectFrom(cat).where(new BooleanBuilder()).toString())
        .isEqualTo("select cat\nfrom Cat cat");
  }

  @Test
  public void booleanBuilder_with_null_in_having() {
    assertThat(selectFrom(cat).groupBy(cat.name).having(new BooleanBuilder()).toString())
        .isEqualTo("select cat\nfrom Cat cat\ngroup by cat.name");
  }
}
