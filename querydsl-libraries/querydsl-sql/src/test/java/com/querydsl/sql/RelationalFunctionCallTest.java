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

import static com.querydsl.sql.SQLExpressions.selectOne;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Test;

public class RelationalFunctionCallTest {

  private static Expression[] serializeCollection(String... tokens) {
    var rv = new Expression[tokens.length];
    for (var i = 0; i < tokens.length; i++) {
      rv[i] = ConstantImpl.create(tokens[i]);
    }
    return rv;
  }

  private static class TokenizeFunction extends RelationalFunctionCall<String> {
    final PathBuilder<String> alias;
    final StringPath token;

    TokenizeFunction(String alias, String... tokens) {
      super(String.class, "tokenize", serializeCollection(tokens));
      this.alias = new PathBuilder<>(String.class, alias);
      this.token = Expressions.stringPath(this.alias, "token");
    }
  }

  @Test
  public void validation() {
    var survey = QSurvey.survey;
    var func = new TokenizeFunction("func", "a", "b");
    SQLQuery<?> sub = selectOne().from(func.as(func.alias)).where(survey.name.like(func.token));
    System.out.println(sub);
  }

  @Test
  public void noArgs() {
    RelationalFunctionCall<String> functionCall =
        SQLExpressions.relationalFunctionCall(String.class, "getElements");
    assertThat(functionCall.getTemplate()).hasToString("getElements()");
  }

  @Test
  public void twoArgs() {
    var str = Expressions.stringPath("str");
    RelationalFunctionCall<String> functionCall =
        SQLExpressions.relationalFunctionCall(String.class, "getElements", "a", str);
    assertThat(functionCall.getTemplate()).hasToString("getElements({0}, {1})");
    assertThat(functionCall.getArg(0)).isEqualTo("a");
    assertThat(functionCall.getArg(1)).isEqualTo(str);
  }
}
