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

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class SQLiteTemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  protected SQLTemplates createTemplates() {
    return new SQLiteTemplates();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void union() {
    var one = Expressions.ONE;
    var two = Expressions.TWO;
    var three = Expressions.THREE;
    Path<Integer> col1 = Expressions.path(Integer.class, "col1");
    Union union = query.union(select(one.as(col1)), select(two), select(three));

    assertThat(union.toString())
        .isEqualTo(
            """
			select 1 as col1
			union
			select 2
			union
			select 3""");
  }

  @Test
  public void precedence() {
    // ||
    // *    /    %
    var p1 = getPrecedence(Ops.MULT, Ops.DIV, Ops.MOD);
    // +    -
    var p2 = getPrecedence(Ops.ADD, Ops.SUB);
    // <<   >>   &    |
    // <    <=   >    >=
    var p3 = getPrecedence(Ops.LT, Ops.GT, Ops.LOE, Ops.GOE);
    // =    ==   !=   <>   IS   IS NOT   IN   LIKE   GLOB   MATCH   REGEXP
    var p4 =
        getPrecedence(
            Ops.EQ,
            Ops.EQ_IGNORE_CASE,
            Ops.IS_NULL,
            Ops.IS_NOT_NULL,
            Ops.IN,
            Ops.LIKE,
            Ops.LIKE_ESCAPE,
            Ops.MATCHES);
    // AND
    var p5 = getPrecedence(Ops.AND);
    //  OR
    var p6 = getPrecedence(Ops.OR);

    assertThat(p1 < p2).isTrue();
    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
  }
}
