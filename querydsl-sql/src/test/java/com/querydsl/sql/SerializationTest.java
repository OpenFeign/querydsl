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

import static com.querydsl.sql.SQLExpressions.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.dml.SQLUpdateClause;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import java.sql.Connection;
import org.easymock.EasyMock;
import org.junit.Test;

public class SerializationTest {

  private static final QSurvey survey = QSurvey.survey;

  private final Connection connection = EasyMock.createMock(Connection.class);

  @Test
  public void innerJoin() {
    SQLQuery<?> query = new SQLQuery<Void>(connection, SQLTemplates.DEFAULT);
    query.from(new QSurvey("s1")).innerJoin(new QSurvey("s2"));
    assertThat(query.toString()).isEqualTo("from SURVEY s1\ninner join SURVEY s2");
  }

  @Test
  public void leftJoin() {
    SQLQuery<?> query = new SQLQuery<Void>(connection, SQLTemplates.DEFAULT);
    query.from(new QSurvey("s1")).leftJoin(new QSurvey("s2"));
    assertThat(query.toString()).isEqualTo("from SURVEY s1\nleft join SURVEY s2");
  }

  @Test
  public void rightJoin() {
    SQLQuery<?> query = new SQLQuery<Void>(connection, SQLTemplates.DEFAULT);
    query.from(new QSurvey("s1")).rightJoin(new QSurvey("s2"));
    assertThat(query.toString()).isEqualTo("from SURVEY s1\nright join SURVEY s2");
  }

  @Test
  public void fullJoin() {
    SQLQuery<?> query = new SQLQuery<Void>(connection, SQLTemplates.DEFAULT);
    query.from(new QSurvey("s1")).fullJoin(new QSurvey("s2"));
    assertThat(query.toString()).isEqualTo("from SURVEY s1\nfull join SURVEY s2");
  }

  @Test
  public void update() {
    SQLUpdateClause updateClause = new SQLUpdateClause(connection, SQLTemplates.DEFAULT, survey);
    updateClause.set(survey.id, 1);
    updateClause.set(survey.name, (String) null);
    assertThat(updateClause.toString()).isEqualTo("update SURVEY\nset ID = ?, NAME = ?");
  }

  @Test
  public void update_where() {
    SQLUpdateClause updateClause = new SQLUpdateClause(connection, SQLTemplates.DEFAULT, survey);
    updateClause.set(survey.id, 1);
    updateClause.set(survey.name, (String) null);
    updateClause.where(survey.name.eq("XXX"));
    assertThat(updateClause.toString())
        .isEqualTo("update SURVEY\nset ID = ?, NAME = ?\nwhere SURVEY.NAME = ?");
  }

  @Test
  public void insert() {
    SQLInsertClause insertClause = new SQLInsertClause(connection, SQLTemplates.DEFAULT, survey);
    insertClause.set(survey.id, 1);
    insertClause.set(survey.name, (String) null);
    assertThat(insertClause.toString()).isEqualTo("insert into SURVEY (ID, NAME)\nvalues (?, ?)");
  }

  @Test
  public void delete_with_subQuery_exists() {
    QSurvey survey1 = new QSurvey("s1");
    QEmployee employee = new QEmployee("e");
    SQLDeleteClause delete = new SQLDeleteClause(connection, SQLTemplates.DEFAULT, survey1);
    delete.where(
        survey1.name.eq("XXX"),
        selectOne().from(employee).where(survey1.id.eq(employee.id)).exists());
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
    SubQueryExpression<?> sq = select(SQLExpressions.nextval("myseq")).from(QSurvey.survey);
    SQLSerializer serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.serialize(sq.getMetadata(), false);
    assertThat(serializer.toString()).isEqualTo("select nextval('myseq')\nfrom SURVEY SURVEY");
  }

  @Test
  public void functionCall() {
    RelationalFunctionCall<String> func =
        SQLExpressions.relationalFunctionCall(String.class, "TableValuedFunction", "parameter");
    PathBuilder<String> funcAlias = new PathBuilder<String>(String.class, "tokFunc");
    SubQueryExpression<?> expr =
        select(survey.name)
            .from(survey)
            .join(func, funcAlias)
            .on(survey.name.like(funcAlias.getString("prop")).not());

    SQLSerializer serializer = new SQLSerializer(new Configuration(new SQLServerTemplates()));
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
    RelationalFunctionCall<String> func =
        SQLExpressions.relationalFunctionCall(String.class, "TableValuedFunction", "parameter");
    PathBuilder<String> funcAlias = new PathBuilder<String>(String.class, "tokFunc");
    SQLQuery<?> q = new SQLQuery<Void>(SQLServerTemplates.DEFAULT);
    q.from(survey).join(func, funcAlias).on(survey.name.like(funcAlias.getString("prop")).not());

    assertThat(q.toString())
        .isEqualTo(
            """
            from SURVEY SURVEY
            join TableValuedFunction(?) as tokFunc
            on not (SURVEY.NAME like tokFunc.prop escape '\\')\
            """);
  }

  @Test
  public void functionCall3() {
    RelationalFunctionCall<String> func =
        SQLExpressions.relationalFunctionCall(String.class, "TableValuedFunction", "parameter");
    PathBuilder<String> funcAlias = new PathBuilder<String>(String.class, "tokFunc");
    SQLQuery<?> q = new SQLQuery<Void>(HSQLDBTemplates.DEFAULT);
    q.from(survey).join(func, funcAlias).on(survey.name.like(funcAlias.getString("prop")).not());

    assertThat(q.toString())
        .isEqualTo(
            """
            from SURVEY SURVEY
            join table(TableValuedFunction(?)) as tokFunc
            on not (SURVEY.NAME like tokFunc.prop escape '\\')\
            """);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void union1() {
    Expression<?> q = union(select(survey.all()).from(survey), select(survey.all()).from(survey));

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
        union(select(survey.all()).from(survey), select(survey.all()).from(survey))
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
        new SQLQuery<Void>()
            .union(survey, select(survey.all()).from(survey), select(survey.all()).from(survey));

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
    QSurvey survey2 = new QSurvey("survey2");
    SQLQuery<?> q = new SQLQuery<Void>();
    q.with(survey, survey.id, survey.name).as(select(survey2.id, survey2.name).from(survey2));

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
    QSurvey s = new QSurvey("s");
    SQLQuery<?> q = new SQLQuery<Void>();
    q.with(s, s.id, s.name)
        .as(select(survey.id, survey.name).from(survey))
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
    PathBuilder<Survey> survey = new PathBuilder<Survey>(Survey.class, "SURVEY");
    QSurvey survey2 = new QSurvey("survey2");
    SQLQuery<?> q = new SQLQuery<Void>();
    q.with(survey, survey.get(survey2.id), survey.get(survey2.name))
        .as(select(survey2.id, survey2.name).from(survey2));

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
    QSurvey survey2 = new QSurvey("survey2");
    SQLQuery<?> q = new SQLQuery<Void>();
    q.with(survey, survey.id, survey.name).as(select(survey2.id, survey2.name).from(survey2));

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
    QSurvey survey2 = new QSurvey("survey2");
    SQLQuery<?> q = new SQLQuery<Void>();
    q.with(survey, new Path<?>[] {survey.id}).as(select(survey2.id).from(survey2));

    assertThat(q.toString())
        .isEqualTo(
            """
            with SURVEY (ID) as (select survey2.ID
            from SURVEY survey2)

            from dual\
            """);
  }
}
