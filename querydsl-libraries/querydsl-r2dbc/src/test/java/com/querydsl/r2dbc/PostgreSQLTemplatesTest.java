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
package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class PostgreSQLTemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  protected SQLTemplates createTemplates() {
    return new PostgreSQLTemplates();
  }

  @Override
  @Test
  public void noFrom() {
    query.getMetadata().setProjection(Expressions.ONE);
    assertThat(query).hasToString("select 1");
  }

  @Override
  @SuppressWarnings("unchecked")
  @Test
  public void union() {
    var one = Expressions.ONE;
    var two = Expressions.TWO;
    var three = Expressions.THREE;
    Path<Integer> col1 = Expressions.path(Integer.class, "col1");
    Union union =
        query.union(
            R2DBCExpressions.select(one.as(col1)),
            R2DBCExpressions.select(two),
            R2DBCExpressions.select(three));
    assertThat(union.toString())
        .isEqualTo("""
			(select 1 as col1)
			union
			(select 2)
			union
			(select 3)""");
  }

  @Test
  public void precedence() {
    // .    left    table/column name separator
    //        ::    left    PostgreSQL-style typecast
    // [ ]    left    array element selection
    // + -    right    unary plus, unary minus
    var p0 = getPrecedence(Ops.NEGATE);
    //        ^    left    exponentiation
    //        * / %    left    multiplication, division, modulo
    var p1 = getPrecedence(Ops.MULT, Ops.DIV, Ops.MOD);
    // + -    left    addition, subtraction
    var p2 = getPrecedence(Ops.ADD, Ops.SUB);
    // IS         IS TRUE, IS FALSE, IS NULL, etc
    var p3 = getPrecedence(Ops.IS_NULL, Ops.IS_NOT_NULL);
    // ISNULL         test for null
    // NOTNULL         test for not null
    // (any other)    left    all other native and user-defined operators
    // IN         set membership
    var p4 = getPrecedence(Ops.IN);
    // BETWEEN         range containment
    var p5 = getPrecedence(Ops.BETWEEN);
    // OVERLAPS         time interval overlap
    // LIKE ILIKE SIMILAR         string pattern matching
    var p6 = getPrecedence(Ops.LIKE, Ops.LIKE_ESCAPE);
    // < >         less than, greater than
    var p7 = getPrecedence(Ops.LT, Ops.GT);
    //        =    right    equality, assignment
    var p8 = getPrecedence(Ops.EQ);
    // NOT    right    logical negation
    var p9 = getPrecedence(Ops.NOT);
    // AND    left    logical conjunction
    var p10 = getPrecedence(Ops.AND);
    // OR    left    logical disjunction
    var p11 = getPrecedence(Ops.OR);

    assertThat(p0 < p1).isTrue();
    assertThat(p1 < p2).isTrue();
    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
    assertThat(p6 < p7).isTrue();
    assertThat(p7 < p8).isTrue();
    assertThat(p8 < p9).isTrue();
    assertThat(p9 < p10).isTrue();
    assertThat(p10 < p11).isTrue();
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
