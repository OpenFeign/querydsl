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

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.r2dbc.dml.R2DBCDeleteClause;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.r2dbc.domain.QEmployeeNoPK;
import com.querydsl.r2dbc.domain.QSurvey;
import com.querydsl.sql.DatePart;
import com.querydsl.sql.Keywords;
import io.r2dbc.spi.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import org.easymock.EasyMock;
import org.junit.Test;

public class SQLSerializerTest {

  private static final QEmployee employee = QEmployee.employee;

  private static final QSurvey survey = QSurvey.survey;

  @Test
  public void count() {
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.handle(employee.id.count().add(employee.id.countDistinct()));
    assertThat(serializer).hasToString("count(EMPLOYEE.ID) + count(distinct EMPLOYEE.ID)");
  }

  @Test
  public void countDistinct() {
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    R2DBCQuery<?> query = new R2DBCQuery<Void>();
    query.from(QEmployeeNoPK.employee);
    query.distinct();
    serializer.serializeForQuery(query.getMetadata(), true);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            select count(*)
            from (select distinct EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME,\
             EMPLOYEE.SALARY, EMPLOYEE.DATEFIELD, EMPLOYEE.TIMEFIELD, EMPLOYEE.SUPERIOR_ID
            from EMPLOYEE EMPLOYEE) internal\
            """);
  }

  @Test
  public void countDistinct_postgreSQL() {
    var postgresql = new Configuration(new PostgreSQLTemplates());
    var serializer = new SQLSerializer(postgresql);
    R2DBCQuery<?> query = new R2DBCQuery<Void>();
    query.from(QEmployeeNoPK.employee);
    query.distinct();
    serializer.serializeForQuery(query.getMetadata(), true);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            select count(\
            distinct (EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.SALARY, \
            EMPLOYEE.DATEFIELD, EMPLOYEE.TIMEFIELD, EMPLOYEE.SUPERIOR_ID))
            from EMPLOYEE EMPLOYEE\
            """);
  }

  @Test
  public void dynamicQuery() {
    Path<Object> userPath = Expressions.path(Object.class, "user");
    NumberPath<Long> idPath = Expressions.numberPath(Long.class, userPath, "id");
    var usernamePath = Expressions.stringPath(userPath, "username");
    Expression<?> sq =
        R2DBCExpressions.select(idPath, usernamePath).from(userPath).where(idPath.eq(1L));

    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.handle(sq);
    // USER is a reserved word in ANSI SQL 2008
    assertThat(serializer.toString())
        .isEqualTo(
            """
            (select "user".id, "user".username
            from "user"
            where "user".id = ?)\
            """);
  }

  @Test
  public void dynamicQuery2() {
    var userPath = new PathBuilder<Object>(Object.class, "user");
    NumberPath<Long> idPath = userPath.getNumber("id", Long.class);
    var usernamePath = userPath.getString("username");
    Expression<?> sq =
        R2DBCExpressions.select(idPath, usernamePath).from(userPath).where(idPath.eq(1L));

    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.handle(sq);
    // USER is a reserved word in ANSI SQL 2008
    assertThat(serializer.toString())
        .isEqualTo(
            """
            (select "user".id, "user".username
            from "user"
            where "user".id = ?)\
            """);
  }

  @Test
  public void in() {
    var path = Expressions.stringPath("str");
    Expression<?> expr = ExpressionUtils.in(path, Arrays.asList("1", "2", "3"));

    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.handle(expr);
    assertThat(serializer.getConstantPaths()).isEqualTo(Arrays.asList(path, path, path));
    assertThat(serializer.getConstants()).hasSize(3);
  }

  @Test
  public void fullJoinWithoutCodeGeneration() {
    R2DBCQuery<?> r2DBCQuery = queryForMYSQLTemplate();

    var customerPath = new PathBuilder<Object>(Object.class, "customer");
    var deptPath = new PathBuilder<Object>(Object.class, "department");
    Path<Object> deptAliasPath = new PathBuilder<>(Object.class, "d");
    r2DBCQuery = r2DBCQuery.from(customerPath.as("c"));
    NumberPath<Long> idPath = Expressions.numberPath(Long.class, deptAliasPath, "id");

    r2DBCQuery = r2DBCQuery.fullJoin(deptPath, deptAliasPath).select(idPath);

    assertThat(r2DBCQuery.toString())
        .isEqualTo("select d.id from customer as c full join department as d");
  }

  @Test
  public void innerJoinWithoutCodeGeneration() {
    R2DBCQuery<?> r2DBCQuery = queryForMYSQLTemplate();

    var customerPath = new PathBuilder<Object>(Object.class, "customer");
    var deptPath = new PathBuilder<Object>(Object.class, "department");
    Path<Object> deptAliasPath = new PathBuilder<>(Object.class, "d");
    r2DBCQuery = r2DBCQuery.from(customerPath.as("c"));
    NumberPath<Long> idPath = Expressions.numberPath(Long.class, deptAliasPath, "id");

    r2DBCQuery = r2DBCQuery.innerJoin(deptPath, deptAliasPath).select(idPath);

    assertThat(r2DBCQuery.toString())
        .isEqualTo("select d.id from customer as c inner join department as d");
  }

  @Test
  public void joinWithoutCodeGeneration() {
    R2DBCQuery<?> r2DBCQuery = queryForMYSQLTemplate();

    var customerPath = new PathBuilder<Object>(Object.class, "customer");
    var deptPath = new PathBuilder<Object>(Object.class, "department");
    Path<Object> deptAliasPath = new PathBuilder<>(Object.class, "d");
    r2DBCQuery = r2DBCQuery.from(customerPath.as("c"));
    NumberPath<Long> idPath = Expressions.numberPath(Long.class, deptAliasPath, "id");

    r2DBCQuery = r2DBCQuery.join(deptPath, deptAliasPath).select(idPath);

    assertThat(r2DBCQuery.toString())
        .isEqualTo("select d.id from customer as c join department as d");
  }

  @Test
  public void leftJoinWithoutCodeGeneration() {
    R2DBCQuery<?> r2DBCQuery = queryForMYSQLTemplate();

    var customerPath = new PathBuilder<Object>(Object.class, "customer");
    var deptPath = new PathBuilder<Object>(Object.class, "department");
    Path<Object> deptAliasPath = new PathBuilder<>(Object.class, "d");
    r2DBCQuery = r2DBCQuery.from(customerPath.as("c"));
    NumberPath<Long> idPath = Expressions.numberPath(Long.class, deptAliasPath, "id");

    r2DBCQuery = r2DBCQuery.leftJoin(deptPath, deptAliasPath).select(idPath);

    assertThat(r2DBCQuery.toString())
        .isEqualTo("select d.id from customer as c left join department as d");
  }

  @Test
  public void or_in() {
    var path = Expressions.stringPath("str");
    Expression<?> expr =
        ExpressionUtils.anyOf(
            ExpressionUtils.in(path, Arrays.asList("1", "2", "3")),
            ExpressionUtils.in(path, Arrays.asList("4", "5", "6")));

    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.handle(expr);
    assertThat(serializer.getConstantPaths())
        .isEqualTo(Arrays.asList(path, path, path, path, path, path));
    assertThat(serializer.getConstants()).hasSize(6);
  }

  @Test
  public void some() {
    // select some((e.FIRSTNAME is not null)) from EMPLOYEE
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.handle(R2DBCExpressions.any(employee.firstname.isNotNull()));
    assertThat(serializer).hasToString("some(EMPLOYEE.FIRSTNAME is not null)");
  }

  @Test
  public void startsWith() {
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    var s1 = new QSurvey("s1");
    serializer.handle(s1.name.startsWith("X"));
    assertThat(serializer).hasToString("s1.NAME like ? escape '\\'");
    assertThat(serializer.getConstants()).isEqualTo(Arrays.asList("X%"));
  }

  @Test
  public void from_function() {
    R2DBCQuery<?> query = query();
    query.from(Expressions.template(Survey.class, "functionCall()")).join(survey);
    query.where(survey.name.isNotNull());
    assertThat(query.toString())
        .isEqualTo("from functionCall()\njoin SURVEY SURVEY\nwhere SURVEY.NAME is not null");
  }

  @Test
  public void join_to_function_with_alias() {
    R2DBCQuery<?> query = query();
    query
        .from(survey)
        .join(
            R2DBCExpressions.relationalFunctionCall(Survey.class, "functionCall"),
            Expressions.path(Survey.class, "fc"));
    query.where(survey.name.isNotNull());
    assertThat(query.toString())
        .isEqualTo("from SURVEY SURVEY\njoin functionCall() as fc\nwhere SURVEY.NAME is not null");
  }

  @Test
  public void keyword_after_dot() {
    R2DBCQuery<?> query = new R2DBCQuery<Void>(MySQLTemplates.DEFAULT);
    var surveyBuilder = new PathBuilder<Survey>(Survey.class, "survey");
    query.from(surveyBuilder).where(surveyBuilder.get("not").isNotNull());
    assertThat(query.toString()).doesNotContain("`");
  }

  @Test
  public void like() {
    Expression<?> expr = Expressions.stringTemplate("'%a%'").contains("%a%");
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.handle(expr);
    assertThat(serializer).hasToString("'%a%' like ? escape '\\'");
  }

  @Test
  public void complex_subQuery() {
    // create sub queries
    List<SubQueryExpression<Tuple>> sq = new ArrayList<>();
    var strs = new String[] {"a", "b", "c"};
    for (String str : strs) {
      Expression<Boolean> alias =
          Expressions.cases().when(survey.name.eq(str)).then(true).otherwise(false);
      sq.add(R2DBCExpressions.select(survey.name, alias).from(survey).distinct());
    }

    // master query
    var subAlias = new PathBuilder<Tuple>(Tuple.class, "sub");
    SubQueryExpression<?> master =
        R2DBCExpressions.selectOne()
            .from(R2DBCExpressions.union(sq).as(subAlias))
            .groupBy(subAlias.get("prop1"));

    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.serialize(master.getMetadata(), false);
    System.err.println(serializer);
  }

  @Test
  public void boolean_() {
    var s = new QSurvey("s");
    var bb1 = new BooleanBuilder();
    bb1.and(s.name.eq(s.name));

    var bb2 = new BooleanBuilder();
    bb2.or(s.name.eq(s.name));
    bb2.or(s.name.eq(s.name));

    var str = new SQLSerializer(Configuration.DEFAULT).handle(bb1.and(bb2)).toString();
    assertThat(str).isEqualTo("s.NAME = s.NAME and (s.NAME = s.NAME or s.NAME = s.NAME)");
  }

  @Test
  public void list_in_query() {
    Expression<?> expr =
        Expressions.list(survey.id, survey.name)
            .in(R2DBCExpressions.select(survey.id, survey.name).from(survey));

    var str = new SQLSerializer(Configuration.DEFAULT).handle(expr).toString();
    assertThat(str)
        .isEqualTo(
            "(SURVEY.ID, SURVEY.NAME) in (select SURVEY.ID, SURVEY.NAME\nfrom SURVEY SURVEY)");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void withRecursive() {
    /*with sub (id, firstname, superior_id) as (
        select id, firstname, superior_id from employee where firstname like 'Mike'
        union all
        select employee.id, employee.firstname, employee.superior_id from sub, employee
        where employee.superior_id = sub.id)
    select * from sub;*/

    var e = QEmployee.employee;
    var sub = new PathBuilder<Tuple>(Tuple.class, "sub");
    R2DBCQuery<?> query = new R2DBCQuery<Void>(SQLTemplates.DEFAULT);
    query
        .withRecursive(
            sub,
            R2DBCExpressions.unionAll(
                R2DBCExpressions.select(e.id, e.firstname, e.superiorId)
                    .from(e)
                    .where(e.firstname.eq("Mike")),
                R2DBCExpressions.select(e.id, e.firstname, e.superiorId)
                    .from(e, sub)
                    .where(e.superiorId.eq(sub.get(e.id)))))
        .from(sub);

    var md = query.getMetadata();
    md.setProjection(Wildcard.all);
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.serialize(md, false);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            with recursive sub as ((select EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.SUPERIOR_ID
            from EMPLOYEE EMPLOYEE
            where EMPLOYEE.FIRSTNAME = ?)
            union all
            (select EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.SUPERIOR_ID
            from EMPLOYEE EMPLOYEE, sub
            where EMPLOYEE.SUPERIOR_ID = sub.ID))
            select *
            from sub\
            """);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void withRecursive2() {
    /*with sub (id, firstname, superior_id) as (
        select id, firstname, superior_id from employee where firstname like 'Mike'
        union all
        select employee.id, employee.firstname, employee.superior_id from sub, employee
        where employee.superior_id = sub.id)
    select * from sub;*/

    var e = QEmployee.employee;
    var sub = new PathBuilder<Tuple>(Tuple.class, "sub");
    R2DBCQuery<?> query = new R2DBCQuery<Void>(SQLTemplates.DEFAULT);
    query
        .withRecursive(sub, sub.get(e.id), sub.get(e.firstname), sub.get(e.superiorId))
        .as(
            R2DBCExpressions.unionAll(
                R2DBCExpressions.select(e.id, e.firstname, e.superiorId)
                    .from(e)
                    .where(e.firstname.eq("Mike")),
                R2DBCExpressions.select(e.id, e.firstname, e.superiorId)
                    .from(e, sub)
                    .where(e.superiorId.eq(sub.get(e.id)))))
        .from(sub);

    var md = query.getMetadata();
    md.setProjection(Wildcard.all);
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.serialize(md, false);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            with recursive sub (ID, FIRSTNAME, SUPERIOR_ID) as ((select EMPLOYEE.ID,\
             EMPLOYEE.FIRSTNAME, EMPLOYEE.SUPERIOR_ID
            from EMPLOYEE EMPLOYEE
            where EMPLOYEE.FIRSTNAME = ?)
            union all
            (select EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.SUPERIOR_ID
            from EMPLOYEE EMPLOYEE, sub
            where EMPLOYEE.SUPERIOR_ID = sub.ID))
            select *
            from sub\
            """);
  }

  @Test
  public void useLiterals() {
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.setUseLiterals(true);

    var offset = TimeZone.getDefault().getRawOffset();
    Expression<?> expr =
        R2DBCExpressions.datediff(DatePart.year, employee.datefield, new java.sql.Date(-offset));
    serializer.handle(expr);
    assertThat(serializer.toString())
        .isEqualTo("datediff('year',EMPLOYEE.DATEFIELD,(date '1970-01-01'))");
  }

  @Test
  public void select_normalization() {
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.visit(
        R2DBCExpressions.select(Expressions.stringPath("id"), Expressions.stringPath("ID")), null);
    assertThat(serializer).hasToString("(select id, ID as col__ID1\n" + "from dual)");
  }

  @Test
  public void noSchemaInWhere() {
    var defaultWithPrintSchema =
        new Configuration(
            new SQLTemplates(Keywords.DEFAULT, "\"", '\\', false, false, SQLTemplates.ANONYMOUS));
    defaultWithPrintSchema.getTemplates().setPrintSchema(true);

    var e = QEmployee.employee;
    var delete =
        new R2DBCDeleteClause(
            (Connection) EasyMock.createNiceMock(Connection.class), defaultWithPrintSchema, e);
    delete.where(e.id.gt(100));

    assertThat(delete.toString())
        .isEqualTo("delete from PUBLIC.EMPLOYEE\n" + "where EMPLOYEE.ID > ?");
  }

  private R2DBCQuery<?> query() {
    return new R2DBCQuery<Void>();
  }

  private R2DBCQuery<?> queryForMYSQLTemplate() {
    return new R2DBCQuery<Void>(
        MySQLTemplates.builder().printSchema().newLineToSingleSpace().build());
  }
}
