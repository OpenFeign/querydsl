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
import org.junit.Test;

public class MySQLTemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  protected SQLTemplates createTemplates() {
    return new MySQLTemplates();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void test() {
    SQLTemplates templates = MySQLTemplates.builder().printSchema().build();
    Configuration conf = new Configuration(templates);
    System.out.println(new R2DBCQuery(conf).from(survey1).toString());
  }

  @Test
  public void order_nullsFirst() {
    query.from(survey1).orderBy(survey1.name.asc().nullsFirst());
    assertThat(query.toString())
        .isEqualTo(
            "from SURVEY survey1 order by (case when survey1.NAME is null then 0 else 1 end),"
                + " survey1.NAME asc");
  }

  @Test
  public void order_nullsLast() {
    query.from(survey1).orderBy(survey1.name.asc().nullsLast());
    assertThat(query.toString())
        .isEqualTo(
            "from SURVEY survey1 order by (case when survey1.NAME is null then 1 else 0 end),"
                + " survey1.NAME asc");
  }

  @Test
  public void precedence() {
    // INTERVAL
    // BINARY, COLLATE
    // !
    // - (unary minus), ~ (unary bit inversion)
    int p0 = getPrecedence(Ops.NEGATE);
    // ^
    // *, /, DIV, %, MOD
    int p1 = getPrecedence(Ops.MULT, Ops.DIV, Ops.MOD);
    // -, +
    int p2 = getPrecedence(Ops.SUB, Ops.ADD);
    // <<, >>
    // &
    // |
    // = (comparison), <=>, >=, >, <=, <, <>, !=, IS, LIKE, REGEXP, IN
    int p3 =
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
    int p4 = getPrecedence(Ops.BETWEEN, Ops.CASE, Ops.CASE_ELSE);
    // NOT
    int p5 = getPrecedence(Ops.NOT);
    // &&, AND
    int p6 = getPrecedence(Ops.AND);
    // XOR
    int p7 = getPrecedence(Ops.XOR, Ops.XNOR);
    // ||, OR
    int p8 = getPrecedence(Ops.OR);
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
}
