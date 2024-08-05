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

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class MySQLTemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  protected SQLTemplates createTemplates() {
    return new MySQLTemplates();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test() {
    var templates = MySQLTemplates.builder().printSchema().build();
    var conf = new Configuration(templates);
    System.out.println(new SQLQuery(conf).from(survey1).toString());
  }

  @Test
  public void order_nullsFirst() {
    query.from(survey1).orderBy(survey1.name.asc().nullsFirst());
    assertThat(query.toString())
        .isEqualTo(
            """
            from SURVEY survey1 order by (case when survey1.NAME is null then 0 else 1 end),\
             survey1.NAME asc\
            """);
  }

  @Test
  public void order_nullsLast() {
    query.from(survey1).orderBy(survey1.name.asc().nullsLast());
    assertThat(query.toString())
        .isEqualTo(
            """
            from SURVEY survey1 order by (case when survey1.NAME is null then 1 else 0 end),\
             survey1.NAME asc\
            """);
  }

  @Test
  public void precedence() {
    // INTERVAL
    // BINARY, COLLATE
    // !
    // - (unary minus), ~ (unary bit inversion)
    var p0 = getPrecedence(Ops.NEGATE);
    // ^
    // *, /, DIV, %, MOD
    var p1 = getPrecedence(Ops.MULT, Ops.DIV, Ops.MOD);
    // -, +
    var p2 = getPrecedence(Ops.SUB, Ops.ADD);
    // <<, >>
    // &
    // |
    // = (comparison), <=>, >=, >, <=, <, <>, !=, IS, LIKE, REGEXP, IN
    var p3 =
        getPrecedence(
            Ops.EQ,
            Ops.GOE,
            Ops.GT,
            Ops.LT,
            Ops.NE,
            Ops.IS_NULL,
            Ops.IS_NOT_NULL,
            Ops.MATCHES,
            Ops.IN,
            Ops.LIKE,
            Ops.LIKE_ESCAPE);
    // BETWEEN, CASE, WHEN, THEN, ELSE
    var p4 = getPrecedence(Ops.BETWEEN, Ops.CASE, Ops.CASE_ELSE);
    // NOT
    var p5 = getPrecedence(Ops.NOT);
    // &&, AND
    var p6 = getPrecedence(Ops.AND);
    // XOR
    var p7 = getPrecedence(Ops.XOR, Ops.XNOR);
    // ||, OR
    var p8 = getPrecedence(Ops.OR);
    // = (assignment), :=

    assertThat(p0 < p1).isTrue();
    assertThat(p1 < p2).isTrue();
    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
    assertThat(p6 < p7).isTrue();
    assertThat(p7 < p8).isTrue();
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void truncateWeek() {
    final SQLQuery<Comparable> expression =
        query.select(
            SQLExpressions.datetrunc(
                DatePart.week, Expressions.dateTimeTemplate(Comparable.class, "dateExpression")));
    assertThat(expression.toString())
        .isEqualTo(
            """
            select str_to_date(concat(date_format(dateExpression,'%Y-%u'),'-1'),'%Y-%u-%w') from\
             dual\
            """);
  }
}
