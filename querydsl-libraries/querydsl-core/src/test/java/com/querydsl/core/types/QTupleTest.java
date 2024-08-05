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
    var qTuple = new QTuple(expr);
    var tuple = qTuple.newInstance("arg");
    assertThat(tuple.get(expr)).isEqualTo("arg");
    assertThat(tuple.get(Expressions.stringPath("s"))).isEqualTo("arg");
  }

  @Test
  public void twoExpressions_getArgs() {
    assertThat(new QTuple(str1, str2).getArgs()).isEqualTo(Arrays.asList(str1, str2));
  }

  @Test
  public void oneArray_getArgs() {
    assertThat(new QTuple(exprs1).getArgs()).isEqualTo(Arrays.asList(str1, str2));
  }

  @Test
  public void twoExpressionArrays_getArgs() {
    assertThat(new QTuple(exprs1, exprs2).getArgs())
        .isEqualTo(Arrays.asList(str1, str2, str3, str4));
  }

  @Test
  public void nestedProjection_getArgs() {
    assertThat(FactoryExpressionUtils.wrap(new QTuple(concat)).getArgs())
        .isEqualTo(Arrays.asList(str1, str2));
  }

  @Test
  public void nestedProjection_getArgs2() {
    assertThat(FactoryExpressionUtils.wrap(new QTuple(concat, str3)).getArgs())
        .isEqualTo(Arrays.asList(str1, str2, str3));
  }

  @Test
  public void nestedProjection_newInstance() {
    var expr = new QTuple(concat);
    assertThat(FactoryExpressionUtils.wrap(expr).newInstance("12", "34").get(concat))
        .isEqualTo("1234");
  }

  @Test
  public void nestedProjection_newInstance2() {
    var expr = new QTuple(str1, str2, concat);
    assertThat(FactoryExpressionUtils.wrap(expr).newInstance("1", "2", "12", "34").get(concat))
        .isEqualTo("1234");
  }

  @Test
  public void tuple_equals() {
    var expr = new QTuple(str1, str2);
    assertThat(expr.newInstance("str1", "str2")).isEqualTo(expr.newInstance("str1", "str2"));
  }

  @Test
  public void tuple_hashCode() {
    var expr = new QTuple(str1, str2);
    assertThat(expr.newInstance("str1", "str2").hashCode())
        .isEqualTo(expr.newInstance("str1", "str2").hashCode());
  }

  @Test
  @Ignore
  public void duplicates() {
    var expr = new QTuple(str1, str1);
    assertThat(expr.getArgs()).hasSize(1);
    assertThat(expr.getArgs().getFirst()).isEqualTo(str1);
  }

  @Test
  @Ignore
  public void duplicates2() {
    var expr = new QTuple(Arrays.asList(str1, str1));
    assertThat(expr.getArgs()).hasSize(1);
    assertThat(expr.getArgs().getFirst()).isEqualTo(str1);
  }

  @Test
  public void newInstance() {
    assertThat(new QTuple(str1, str1).newInstance(null, null)).isNotNull();
    assertThat(new QTuple(str1, str1).skipNulls().newInstance(null, null)).isNull();
  }
}
