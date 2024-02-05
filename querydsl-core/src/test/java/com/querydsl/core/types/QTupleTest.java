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
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import org.junit.Ignore;
import org.junit.Test;

public class QTupleTest {

  StringPath str1 = Expressions.stringPath("str1");
  StringPath str2 = Expressions.stringPath("str2");
  StringPath str3 = Expressions.stringPath("str3");
  StringPath str4 = Expressions.stringPath("str4");
  Expression<?>[] exprs1 = new Expression[] {str1, str2};
  Expression<?>[] exprs2 = new Expression[] {str3, str4};

  Concatenation concat = new Concatenation(str1, str2);

  @Test
  public void alias() {
    Expression<?> expr = str1.as("s");
    QTuple qTuple = new QTuple(expr);
    Tuple tuple = qTuple.newInstance("arg");
    assertEquals("arg", tuple.get(expr));
    assertEquals("arg", tuple.get(Expressions.stringPath("s")));
  }

  @Test
  public void twoExpressions_getArgs() {
    assertEquals(Arrays.asList(str1, str2), new QTuple(str1, str2).getArgs());
  }

  @Test
  public void oneArray_getArgs() {
    assertEquals(Arrays.asList(str1, str2), new QTuple(exprs1).getArgs());
  }

  @Test
  public void twoExpressionArrays_getArgs() {
    assertEquals(Arrays.asList(str1, str2, str3, str4), new QTuple(exprs1, exprs2).getArgs());
  }

  @Test
  public void nestedProjection_getArgs() {
    assertEquals(
        Arrays.asList(str1, str2), FactoryExpressionUtils.wrap(new QTuple(concat)).getArgs());
  }

  @Test
  public void nestedProjection_getArgs2() {
    assertEquals(
        Arrays.asList(str1, str2, str3),
        FactoryExpressionUtils.wrap(new QTuple(concat, str3)).getArgs());
  }

  @Test
  public void nestedProjection_newInstance() {
    QTuple expr = new QTuple(concat);
    assertEquals("1234", FactoryExpressionUtils.wrap(expr).newInstance("12", "34").get(concat));
  }

  @Test
  public void nestedProjection_newInstance2() {
    QTuple expr = new QTuple(str1, str2, concat);
    assertEquals(
        "1234", FactoryExpressionUtils.wrap(expr).newInstance("1", "2", "12", "34").get(concat));
  }

  @Test
  public void tuple_equals() {
    QTuple expr = new QTuple(str1, str2);
    assertEquals(expr.newInstance("str1", "str2"), expr.newInstance("str1", "str2"));
  }

  @Test
  public void tuple_hashCode() {
    QTuple expr = new QTuple(str1, str2);
    assertEquals(
        expr.newInstance("str1", "str2").hashCode(), expr.newInstance("str1", "str2").hashCode());
  }

  @Test
  @Ignore
  public void duplicates() {
    QTuple expr = new QTuple(str1, str1);
    assertEquals(1, expr.getArgs().size());
    assertEquals(str1, expr.getArgs().get(0));
  }

  @Test
  @Ignore
  public void duplicates2() {
    QTuple expr = new QTuple(Arrays.asList(str1, str1));
    assertEquals(1, expr.getArgs().size());
    assertEquals(str1, expr.getArgs().get(0));
  }

  @Test
  public void newInstance() {
    assertThat(new QTuple(str1, str1).newInstance(null, null)).isNotNull();
    assertThat(new QTuple(str1, str1).skipNulls().newInstance(null, null)).isNull();
  }
}
