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

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.r2dbc.dml.R2DBCDeleteClause;
import com.querydsl.r2dbc.dml.R2DBCInsertClause;
import com.querydsl.r2dbc.dml.R2DBCUpdateClause;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.r2dbc.domain.QSurvey;
import io.r2dbc.spi.Connection;
import org.easymock.EasyMock;
import org.junit.Test;

public class SerializationTest {

  private static final QSurvey survey = QSurvey.survey;

  private final Connection connection = EasyMock.createMock(Connection.class);

  @Test
  public void innerJoin() {
    R2DBCQuery<?> query = new R2DBCQuery<Void>(connection, SQLTemplates.DEFAULT);
    query.from(new QSurvey("s1")).innerJoin(new QSurvey("s2"));
    assertThat(query).hasToString("from SURVEY s1\ninner join SURVEY s2");
  }

  @Test
  public void leftJoin() {
    R2DBCQuery<?> query = new R2DBCQuery<Void>(connection, SQLTemplates.DEFAULT);
    query.from(new QSurvey("s1")).leftJoin(new QSurvey("s2"));
    assertThat(query).hasToString("from SURVEY s1\nleft join SURVEY s2");
  }

  @Test
  public void rightJoin() {
    R2DBCQuery<?> query = new R2DBCQuery<Void>(connection, SQLTemplates.DEFAULT);
    query.from(new QSurvey("s1")).rightJoin(new QSurvey("s2"));
    assertThat(query).hasToString("from SURVEY s1\nright join SURVEY s2");
  }

  @Test
  public void fullJoin() {
    R2DBCQuery<?> query = new R2DBCQuery<Void>(connection, SQLTemplates.DEFAULT);
    query.from(new QSurvey("s1")).fullJoin(new QSurvey("s2"));
    assertThat(query).hasToString("from SURVEY s1\nfull join SURVEY s2");
  }

  @Test
  public void update() {
    var updateClause = new R2DBCUpdateClause(connection, SQLTemplates.DEFAULT, survey);
    updateClause.set(survey.id, 1);
    updateClause.set(survey.name, (String) null);
    assertThat(updateClause).hasToString("update SURVEY\nset ID = ?, NAME = ?");
  }

  @Test
  public void update_where() {
    var updateClause = new R2DBCUpdateClause(connection, SQLTemplates.DEFAULT, survey);
    updateClause.set(survey.id, 1);
    updateClause.set(survey.name, (String) null);
    updateClause.where(survey.name.eq("XXX"));
    assertThat(updateClause.toString())
        .isEqualTo("update SURVEY\nset ID = ?, NAME = ?\nwhere SURVEY.NAME = ?");
  }

  @Test
  public void insert() {
    var insertClause = new R2DBCInsertClause(connection, SQLTemplates.DEFAULT, survey);
    insertClause.set(survey.id, 1);
    insertClause.set(survey.name, (String) null);
    assertThat(insertClause).hasToString("insert into SURVEY (ID, NAME)\nvalues (?, ?)");
  }

  @Test
  public void delete_with_subQuery_exists() {
    var survey1 = new QSurvey("s1");
    var employee = new QEmployee("e");
    var delete = new R2DBCDeleteClause(connection, SQLTemplates.DEFAULT, survey1);
    delete.where(
        survey1.name.eq("XXX"),
        R2DBCExpressions.selectOne().from(employee).where(survey1.id.eq(employee.id)).exists());
    assertThat(delete.toString())
        .isEqualTo(
            """
            delete from SURVEY
            where SURVEY.NAME = ? and exists (select 1
            from EMPLOYEE e
            where SURVEY.ID = e.ID)\
            """);
  }

  @Test
  public void nextval() {
    SubQueryExpression<?> sq =
        R2DBCExpressions.select(R2DBCExpressions.nextval("myseq")).from(QSurvey.survey);
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.serialize(sq.getMetadata(), false);
    assertThat(serializer).hasToString("select nextval('myseq')\nfrom SURVEY SURVEY");
  }

  @Test
  public void functionCall() {
    R2DBCRelationalFunctionCall<String> func =
        R2DBCExpressions.relationalFunctionCall(String.class, "TableValuedFunction", "parameter");
    var funcAlias = new PathBuilder<>(String.class, "tokFunc");
    SubQueryExpression<?> expr =
        R2DBCExpressions.select(survey.name)
            .from(survey)
            .join(func, funcAlias)
            .on(survey.name.like(funcAlias.getString("prop")).not());

    var serializer = new SQLSerializer(new Configuration(new SQLServerTemplates()));
    serializer.serialize(expr.getMetadata(), false);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            select SURVEY.NAME
            from SURVEY SURVEY
            join TableValuedFunction(?) as tokFunc
            on not (SURVEY.NAME like tokFunc.prop escape '\\')\
            """);
  }

  @Test
  public void functionCall2() {
    R2DBCRelationalFunctionCall<String> func =
        R2DBCExpressions.relationalFunctionCall(String.class, "TableValuedFunction", "parameter");
    var funcAlias = new PathBuilder<>(String.class, "tokFunc");
    R2DBCQuery<?> q = new R2DBCQuery<Void>(SQLServerTemplates.DEFAULT);
    q.from(survey).join(func, funcAlias).on(survey.name.like(funcAlias.getString("prop")).not());

    assertThat(q.toString())
        .isEqualTo(
            """
            from SURVEY SURVEY
            join TableValuedFunction(?) as tokFunc
            on not (SURVEY.NAME like tokFunc.prop escape '\\')\
            """);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void union1() {
    Expression<?> q =
        R2DBCExpressions.union(
            R2DBCExpressions.select(survey.all()).from(survey),
            R2DBCExpressions.select(survey.all()).from(survey));

    assertThat(q.toString())
        .isEqualTo(
            """
            (select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID
            from SURVEY SURVEY)
            union
            (select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID
            from SURVEY SURVEY)\
            """);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void union1_groupBy() {
    Expression<?> q =
        R2DBCExpressions.union(
                R2DBCExpressions.select(survey.all()).from(survey),
                R2DBCExpressions.select(survey.all()).from(survey))
            .groupBy(survey.id);

    assertThat(q.toString())
        .isEqualTo(
            """
            (select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID
            from SURVEY SURVEY)
            union
            (select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID
            from SURVEY SURVEY)
            group by SURVEY.ID\
            """);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void union2() {
    Expression<?> q =
        new R2DBCQuery<Void>()
            .union(
                survey,
                R2DBCExpressions.select(survey.all()).from(survey),
                R2DBCExpressions.select(survey.all()).from(survey));

    assertThat(q.toString())
        .isEqualTo(
            """
            from ((select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID
            from SURVEY SURVEY)
            union
            (select SURVEY.NAME, SURVEY.NAME2, SURVEY.ID
            from SURVEY SURVEY)) as SURVEY\
            """);
  }

  @Test
  public void with() {
    var survey2 = new QSurvey("survey2");
    R2DBCQuery<?> q = new R2DBCQuery<Void>();
    q.with(survey, survey.id, survey.name)
        .as(R2DBCExpressions.select(survey2.id, survey2.name).from(survey2));

    assertThat(q.toString())
        .isEqualTo(
            """
            with SURVEY (ID, NAME) as (select survey2.ID, survey2.NAME
            from SURVEY survey2)

            from dual\
            """);
  }

  @Test
  public void with_complex() {
    var s = new QSurvey("s");
    R2DBCQuery<?> q = new R2DBCQuery<Void>();
    q.with(s, s.id, s.name)
        .as(R2DBCExpressions.select(survey.id, survey.name).from(survey))
        .select(s.id, s.name, survey.id, survey.name)
        .from(s, survey);

    assertThat(q.toString())
        .isEqualTo(
            """
            with s (ID, NAME) as (select SURVEY.ID, SURVEY.NAME
            from SURVEY SURVEY)
            select s.ID, s.NAME, SURVEY.ID, SURVEY.NAME
            from s s, SURVEY SURVEY\
            """);
  }

  @Test
  public void with_tuple() {
    var survey = new PathBuilder<>(Survey.class, "SURVEY");
    var survey2 = new QSurvey("survey2");
    R2DBCQuery<?> q = new R2DBCQuery<Void>();
    q.with(survey, survey.get(survey2.id), survey.get(survey2.name))
        .as(R2DBCExpressions.select(survey2.id, survey2.name).from(survey2));

    assertThat(q.toString())
        .isEqualTo(
            """
            with SURVEY (ID, NAME) as (select survey2.ID, survey2.NAME
            from SURVEY survey2)

            from dual\
            """);
  }

  @Test
  public void with_tuple2() {
    var survey2 = new QSurvey("survey2");
    R2DBCQuery<?> q = new R2DBCQuery<Void>();
    q.with(survey, survey.id, survey.name)
        .as(R2DBCExpressions.select(survey2.id, survey2.name).from(survey2));

    assertThat(q.toString())
        .isEqualTo(
            """
            with SURVEY (ID, NAME) as (select survey2.ID, survey2.NAME
            from SURVEY survey2)

            from dual\
            """);
  }

  @Test
  public void with_singleColumn() {
    var survey2 = new QSurvey("survey2");
    R2DBCQuery<?> q = new R2DBCQuery<Void>();
    q.with(survey, new Path<?>[] {survey.id}).as(R2DBCExpressions.select(survey2.id).from(survey2));

    assertThat(q.toString())
        .isEqualTo(
            """
            with SURVEY (ID) as (select survey2.ID
            from SURVEY survey2)

            from dual\
            """);
  }
}
