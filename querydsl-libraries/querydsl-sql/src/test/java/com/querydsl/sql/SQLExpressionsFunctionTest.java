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
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.SimpleTemplate;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Test;

public class SQLExpressionsFunctionTest {

  private static final QSurvey survey = QSurvey.survey;

  private static final QEmployee employee = QEmployee.employee;

  @Test
  public void simpleFunction() {
    SimpleTemplate<String> expr =
        SQLExpressions.function(String.class, "my_function", survey.name);
    assertThat(expr.toString()).isEqualTo("my_function(SURVEY.NAME)");
  }

  @Test
  public void twoPartName() {
    StringTemplate expr =
        SQLExpressions.stringFunction("dbo.my_function", survey.name, survey.name2);
    assertThat(expr.toString()).isEqualTo("dbo.my_function(SURVEY.NAME, SURVEY.NAME2)");
  }

  @Test
  public void threePartName() {
    StringTemplate expr =
        SQLExpressions.stringFunction(
            "external_db.dbo.my_function",
            ConstantImpl.create("PARAM"),
            survey.name,
            ConstantImpl.create(""));
    assertThat(expr.toString())
        .isEqualTo("external_db.dbo.my_function(PARAM, SURVEY.NAME, )");
  }

  @Test
  public void numberFunction() {
    NumberTemplate<Integer> expr =
        SQLExpressions.numberFunction(Integer.class, "dbo.calculate", survey.id);
    assertThat(expr.toString()).isEqualTo("dbo.calculate(SURVEY.ID)");
  }

  @Test
  public void stringFunction() {
    StringTemplate expr = SQLExpressions.stringFunction("my_encrypt", survey.name);
    assertThat(expr.toString()).isEqualTo("my_encrypt(SURVEY.NAME)");
  }

  @Test
  public void functionInSelect() {
    SQLQuery<?> query = new SQLQuery<Void>(SQLServerTemplates.DEFAULT);
    query
        .select(SQLExpressions.stringFunction("dbo.my_function", survey.name))
        .from(survey);
    assertThat(query.toString())
        .isEqualTo(
            """
            select dbo.my_function(SURVEY.NAME)
            from SURVEY SURVEY""");
  }

  @Test
  public void functionInWhere() {
    SQLQuery<?> query = new SQLQuery<Void>(SQLServerTemplates.DEFAULT);
    query
        .select(survey.name)
        .from(survey)
        .where(
            SQLExpressions.stringFunction("dbo.decrypt", survey.name)
                .eq("expected"));
    assertThat(query.toString())
        .isEqualTo(
            """
            select SURVEY.NAME
            from SURVEY SURVEY
            where dbo.decrypt(SURVEY.NAME) = ?""");
  }

  @Test
  public void functionInInsertValues() {
    SQLInsertClause insert =
        new SQLInsertClause(null, new Configuration(SQLServerTemplates.DEFAULT), survey);
    insert.set(survey.name, SQLExpressions.stringFunction("dbo.encrypt", Expressions.constant("value")));

    assertThat(insert.toString())
        .contains("dbo.encrypt(?)");
  }

  @Test
  public void fourPartName() {
    StringTemplate expr =
        SQLExpressions.stringFunction(
            "linked_server.external_db.dbo.my_function", survey.name);
    assertThat(expr.toString())
        .isEqualTo("linked_server.external_db.dbo.my_function(SURVEY.NAME)");
  }

  @Test
  public void multipleArguments() {
    StringTemplate expr =
        SQLExpressions.stringFunction(
            "schema.func",
            ConstantImpl.create("A"),
            survey.name,
            survey.id,
            ConstantImpl.create("B"));
    assertThat(expr.toString())
        .isEqualTo("schema.func(A, SURVEY.NAME, SURVEY.ID, B)");
  }
}
