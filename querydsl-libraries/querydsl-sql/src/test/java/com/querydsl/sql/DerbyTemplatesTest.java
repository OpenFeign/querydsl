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
package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class DerbyTemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  protected SQLTemplates createTemplates() {
    return new DerbyTemplates();
  }

  @Test
  public void nextVal() {
    Operation<String> nextval =
        ExpressionUtils.operation(String.class, SQLOps.NEXTVAL, ConstantImpl.create("myseq"));
    assertThat(
            new SQLSerializer(new Configuration(new DerbyTemplates())).handle(nextval).toString())
        .isEqualTo("next value for myseq");
  }

  @Test
  public void precedence() {
    // unary + and -
    var p1 = getPrecedence(Ops.NEGATE);
    // *, /, || (concatenation)
    var p2 = getPrecedence(Ops.MULT, Ops.DIV);
    // binary + and -
    var p3 = getPrecedence(Ops.ADD, Ops.SUB);
    // comparisons, quantified comparisons, EXISTS, IN, IS NULL, LIKE, BETWEEN, IS
    var p4 =
        getPrecedence(
            Ops.EQ,
            Ops.NE,
            Ops.LT,
            Ops.GT,
            Ops.LOE,
            Ops.GOE,
            Ops.EXISTS,
            Ops.IN,
            Ops.IS_NULL,
            Ops.LIKE,
            Ops.BETWEEN,
            Ops.IS_NOT_NULL);
    // NOT
    var p5 = getPrecedence(Ops.NOT);
    // AND
    var p6 = getPrecedence(Ops.AND);
    // OR
    var p7 = getPrecedence(Ops.OR);

    assertThat(p1 < p2).isTrue();
    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
    assertThat(p6 < p7).isTrue();
  }

  @Test
  @Override
  public void booleanTemplate() {
    assertSerialized(Expressions.booleanPath("b").eq(Expressions.TRUE), "b = true");
    assertSerialized(Expressions.booleanPath("b").eq(Expressions.FALSE), "b = false");
    query.setUseLiterals(true);
    query.where(Expressions.booleanPath("b").eq(true));
    assertThat(query.toString().endsWith("where b = true")).as(query.toString()).isTrue();
  }
}
