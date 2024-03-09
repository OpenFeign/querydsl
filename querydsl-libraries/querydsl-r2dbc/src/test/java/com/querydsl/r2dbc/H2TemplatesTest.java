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

public class H2TemplatesTest extends AbstractSQLTemplatesTest {

  @Override
  protected SQLTemplates createTemplates() {
    return new H2Templates();
  }

  @Test
  public void builder() {
    SQLTemplates templates = H2Templates.builder().quote().newLineToSingleSpace().build();

    assertThat(templates).isNotNull();
  }

  @Test
  public void precedence() {
    // unary
    // *, /, %
    // +, -
    // ||
    // comparison
    // NOT
    // AND
    // OR

    int p1 = getPrecedence(Ops.NEGATE);
    int p2 = getPrecedence(Ops.MULT, Ops.DIV, Ops.MOD);
    int p3 = getPrecedence(Ops.ADD, Ops.SUB);
    int p4 = getPrecedence(Ops.CONCAT);
    int p5 = getPrecedence(Ops.EQ, Ops.NE, Ops.LT, Ops.GT); // ...
    int p6 = getPrecedence(Ops.NOT);
    int p7 = getPrecedence(Ops.AND);
    int p8 = getPrecedence(Ops.OR);

    assertThat(p1 < p2).isTrue();
    assertThat(p2 < p3).isTrue();
    assertThat(p3 < p4).isTrue();
    assertThat(p4 < p5).isTrue();
    assertThat(p5 < p6).isTrue();
    assertThat(p6 < p7).isTrue();
    assertThat(p7 < p8).isTrue();
  }
}
