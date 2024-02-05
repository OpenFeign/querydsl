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

import static com.querydsl.sql.SQLExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.junit.Test;

public class SQLServer2005TemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  @Test
  public void noFrom() {
    query.getMetadata().setProjection(Expressions.ONE);
    assertThat(query.toString()).isEqualTo("select 1");
  }

  @Override
  protected SQLTemplates createTemplates() {
    return new SQLServer2005Templates();
  }

  @SuppressWarnings("unchecked")
  @Test
  @Override
  public void union() {
    NumberExpression<Integer> one = Expressions.ONE;
    NumberExpression<Integer> two = Expressions.TWO;
    NumberExpression<Integer> three = Expressions.THREE;
    Path<Integer> col1 = Expressions.path(Integer.class, "col1");
    Union union = query.union(select(one.as(col1)), select(two), select(three));
    assertThat(union.toString())
        .isEqualTo("(select 1 as col1)\n" + "union\n" + "(select 2)\n" + "union\n" + "(select 3)");
  }

  @Test
  public void limit() {
    query.from(survey1).limit(5);
    query.getMetadata().setProjection(survey1.id);
    assertThat(query.toString()).isEqualTo("select top (?) survey1.ID from SURVEY survey1");
  }

  @Test
  public void modifiers() {
    query.from(survey1).limit(5).offset(3);
    query.orderBy(survey1.id.asc());
    query.getMetadata().setProjection(survey1.id);
    assertThat(query.toString())
        .isEqualTo(
            """
            select * from (\
               select survey1.ID, row_number() over (order by survey1.ID asc) as rn from SURVEY survey1) a \
            where rn > ? and rn <= ? order by rn\
            """);
  }

  @Test
  public void modifiers_noOrder() {
    query.from(survey1).limit(5).offset(3);
    query.getMetadata().setProjection(survey1.id);
    assertThat(query.toString())
        .isEqualTo(
            """
            select * from (\
               select survey1.ID, row_number() over (order by current_timestamp asc) as rn from SURVEY survey1) a \
            where rn > ? and rn <= ? order by rn\
            """);
  }

  @Test
  public void nextVal() {
    Operation<String> nextval =
        ExpressionUtils.operation(String.class, SQLOps.NEXTVAL, ConstantImpl.create("myseq"));
    assertThat(
            new SQLSerializer(new Configuration(new SQLServerTemplates()))
                .handle(nextval)
                .toString())
        .isEqualTo("myseq.nextval");
  }

  @Test
  public void precedence() {
    // 1  ~ (Bitwise NOT)
    // 2  (Multiply), / (Division), % (Modulo)
    int p2 = getPrecedence(Ops.MULT, Ops.DIV, Ops.MOD);
    // 3 + (Positive), - (Negative), + (Add), (+ Concatenate), - (Subtract), & (Bitwise AND), ^
    // (Bitwise Exclusive OR), | (Bitwise OR)
    int p3 = getPrecedence(Ops.NEGATE, Ops.ADD, Ops.SUB, Ops.CONCAT);
    // 4 =, >, <, >=, <=, <>, !=, !>, !< (Comparison operators)
    int p4 = getPrecedence(Ops.EQ, Ops.GT, Ops.LT, Ops.GOE, Ops.LOE, Ops.NE);
    // 5 NOT
    int p5 = getPrecedence(Ops.NOT);
    // 6 AND
    int p6 = getPrecedence(Ops.AND);
    // 7 ALL, ANY, BETWEEN, IN, LIKE, OR, SOME
    int p7 = getPrecedence(Ops.BETWEEN, Ops.IN, Ops.LIKE, Ops.LIKE_ESCAPE, Ops.OR);
    // 8 = (Assignment)

    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
    assertThat(p6 < p7).isTrue();
  }
}
