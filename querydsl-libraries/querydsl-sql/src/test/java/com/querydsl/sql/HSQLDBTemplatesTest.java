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
import org.junit.Test;

public class HSQLDBTemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  protected SQLTemplates createTemplates() {
    return new HSQLDBTemplates();
  }

  @Test
  public void precedence() {
    // Evaluation from left to right. Parentheses group operations.
    // Multiplication and division take precedence over addition and subtraction.
    // AND takes precedence over OR.
    // NOT applies to the immediate term.
    // LIKE applies to the result of any string concatenation to the right.
    // Comparison ops are not combined without logical ops so there is no precedence issue.

    // int p1 = getPrecedence(Ops.NEGATE);
    var p2 = getPrecedence(Ops.MULT, Ops.DIV, Ops.CONCAT);
    var p3 = getPrecedence(Ops.ADD, Ops.SUB);
    var p4 = getPrecedence(Ops.NOT);
    var p5 = getPrecedence(Ops.EQ, Ops.NE, Ops.LT, Ops.GT, Ops.LOE, Ops.GOE);
    var p6 =
        getPrecedence(
            Ops.IS_NULL,
            Ops.IS_NOT_NULL,
            Ops.LIKE,
            Ops.LIKE_ESCAPE,
            Ops.BETWEEN,
            Ops.IN,
            Ops.NOT_IN,
            Ops.EXISTS);
    var p7 = getPrecedence(Ops.AND);
    var p8 = getPrecedence(Ops.OR);

    // assertTrue(p1 < p2);
    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
    assertThat(p6 < p7).isTrue();
    assertThat(p7 < p8).isTrue();
  }
}
