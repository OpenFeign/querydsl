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
import static com.querydsl.sql.SQLExpressions.selectOne;
import static com.querydsl.sql.SQLExpressions.union;
import static com.querydsl.sql.SQLExpressions.unionAll;
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
import com.querydsl.sql.dml.SQLDeleteClause;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QEmployeeNoPK;
import com.querydsl.sql.domain.QSurvey;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    SQLQuery<?> query = new SQLQuery<Void>();
    query.from(QEmployeeNoPK.employee);
    query.distinct();
    serializer.serializeForQuery(query.getMetadata(), true);
    assertThat(serializer.toString())
        .isEqualTo(
            """
select count(*)
from (select distinct EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.SALARY, \
EMPLOYEE.DATEFIELD, EMPLOYEE.TIMEFIELD, EMPLOYEE.SUPERIOR_ID
from EMPLOYEE EMPLOYEE) internal\
""");
  }

  @Test
  public void countDistinct_postgreSQL() {
    var postgresql = new Configuration(new PostgreSQLTemplates());
    var serializer = new SQLSerializer(postgresql);
    SQLQuery<?> query = new SQLQuery<Void>();
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
    Expression<?> sq = select(idPath, usernamePath).from(userPath).where(idPath.eq(1L));

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
    Expression<?> sq = select(idPath, usernamePath).from(userPath).where(idPath.eq(1L));

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
    SQLQuery<?> sqlQuery = queryForMYSQLTemplate();

    var customerPath = new PathBuilder<Object>(Object.class, "customer");
    var deptPath = new PathBuilder<Object>(Object.class, "department");
    Path<Object> deptAliasPath = new PathBuilder<>(Object.class, "d");
    sqlQuery = sqlQuery.from(customerPath.as("c"));
    NumberPath<Long> idPath = Expressions.numberPath(Long.class, deptAliasPath, "id");

    sqlQuery = sqlQuery.fullJoin(deptPath, deptAliasPath).select(idPath);

    assertThat(sqlQuery.toString())
        .isEqualTo("select d.id from customer as c full join department as d");
  }

  @Test
  public void innerJoinWithoutCodeGeneration() {
    SQLQuery<?> sqlQuery = queryForMYSQLTemplate();

    var customerPath = new PathBuilder<Object>(Object.class, "customer");
    var deptPath = new PathBuilder<Object>(Object.class, "department");
    Path<Object> deptAliasPath = new PathBuilder<>(Object.class, "d");
    sqlQuery = sqlQuery.from(customerPath.as("c"));
    NumberPath<Long> idPath = Expressions.numberPath(Long.class, deptAliasPath, "id");

    sqlQuery = sqlQuery.innerJoin(deptPath, deptAliasPath).select(idPath);

    assertThat(sqlQuery.toString())
        .isEqualTo("select d.id from customer as c inner join department as d");
  }

  @Test
  public void joinWithoutCodeGeneration() {
    SQLQuery<?> sqlQuery = queryForMYSQLTemplate();

    var customerPath = new PathBuilder<Object>(Object.class, "customer");
    var deptPath = new PathBuilder<Object>(Object.class, "department");
    Path<Object> deptAliasPath = new PathBuilder<>(Object.class, "d");
    sqlQuery = sqlQuery.from(customerPath.as("c"));
    NumberPath<Long> idPath = Expressions.numberPath(Long.class, deptAliasPath, "id");

    sqlQuery = sqlQuery.join(deptPath, deptAliasPath).select(idPath);

    assertThat(sqlQuery.toString())
        .isEqualTo("select d.id from customer as c join department as d");
  }

  @Test
  public void leftJoinWithoutCodeGeneration() {
    SQLQuery<?> sqlQuery = queryForMYSQLTemplate();

    var customerPath = new PathBuilder<Object>(Object.class, "customer");
    var deptPath = new PathBuilder<Object>(Object.class, "department");
    Path<Object> deptAliasPath = new PathBuilder<>(Object.class, "d");
    sqlQuery = sqlQuery.from(customerPath.as("c"));
    NumberPath<Long> idPath = Expressions.numberPath(Long.class, deptAliasPath, "id");

    sqlQuery = sqlQuery.leftJoin(deptPath, deptAliasPath).select(idPath);

    assertThat(sqlQuery.toString())
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
    serializer.handle(SQLExpressions.any(employee.firstname.isNotNull()));
    assertThat(serializer).hasToString("some(EMPLOYEE.FIRSTNAME is not null)");
  }

  @Test
  public void startsWith() {
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    var s1 = new QSurvey("s1");
    serializer.handle(s1.name.startsWith("X"));
    assertThat(serializer).hasToString("s1.NAME like ? escape '\\'");
    assertThat(serializer.getConstants()).isEqualTo(Collections.singletonList("X%"));
  }

  @Test
  public void from_function() {
    SQLQuery<?> query = query();
    query.from(Expressions.template(Survey.class, "functionCall()")).join(survey);
    query.where(survey.name.isNotNull());
    assertThat(query.toString())
        .isEqualTo("from functionCall()\njoin SURVEY SURVEY\nwhere SURVEY.NAME is not null");
  }

  @Test
  public void join_to_function_with_alias() {
    SQLQuery<?> query = query();
    query
        .from(survey)
        .join(
            SQLExpressions.relationalFunctionCall(Survey.class, "functionCall"),
            Expressions.path(Survey.class, "fc"));
    query.where(survey.name.isNotNull());
    assertThat(query.toString())
        .isEqualTo("from SURVEY SURVEY\njoin functionCall() as fc\nwhere SURVEY.NAME is not null");
  }

  @Test
  public void join_to_function_in_derby() {
    SQLQuery<?> query = new SQLQuery<Void>(DerbyTemplates.DEFAULT);
    query
        .from(survey)
        .join(
            SQLExpressions.relationalFunctionCall(Survey.class, "functionCall"),
            Expressions.path(Survey.class, "fc"));
    query.where(survey.name.isNotNull());
    assertThat(query.toString())
        .isEqualTo(
            "from SURVEY SURVEY\njoin table(functionCall()) as fc\nwhere SURVEY.NAME is not null");
  }

  @Test
  public void crossJoin() {
    SQLTemplates templates = new DerbyTemplates();
    templates.setCrossJoin(" cross join ");
    SQLQuery<?> query = new SQLQuery<Void>(templates);
    query.from(survey, employee);
    assertThat(query).hasToString("from SURVEY SURVEY cross join EMPLOYEE EMPLOYEE");
  }

  @Test
  public void keyword_after_dot() {
    SQLQuery<?> query = new SQLQuery<Void>(MySQLTemplates.DEFAULT);
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
  public void override() {
    var conf = new Configuration(new DerbyTemplates());
    conf.registerTableOverride("SURVEY", "surveys");

    SQLQuery<?> query = new SQLQuery<Void>(conf);
    query.from(survey);
    assertThat(query).hasToString("from surveys SURVEY");
  }

  @Test
  public void columnOverrides() {
    var conf = new Configuration(new DerbyTemplates());
    conf.registerColumnOverride("SURVEY", "NAME", "LABEL");

    SQLQuery<?> query = new SQLQuery<Void>(conf);
    query.from(survey).where(survey.name.isNull());
    assertThat(query).hasToString("from SURVEY SURVEY\n" + "where SURVEY.LABEL is null");
  }

  @Test
  public void columnOverrides2() {
    var conf = new Configuration(new DerbyTemplates());
    conf.registerColumnOverride("PUBLIC", "SURVEY", "NAME", "LABEL");

    SQLQuery<?> query = new SQLQuery<Void>(conf);
    query.from(survey).where(survey.name.isNull());
    assertThat(query).hasToString("from SURVEY SURVEY\n" + "where SURVEY.LABEL is null");
  }

  @Test
  public void complex_subQuery() {
    // create sub queries
    List<SubQueryExpression<Tuple>> sq = new ArrayList<>();
    var strs = new String[] {"a", "b", "c"};
    for (String str : strs) {
      Expression<Boolean> alias =
          Expressions.cases().when(survey.name.eq(str)).then(true).otherwise(false);
      sq.add(select(survey.name, alias).from(survey).distinct());
    }

    // master query
    var subAlias = new PathBuilder<Tuple>(Tuple.class, "sub");
    SubQueryExpression<?> master =
        selectOne().from(union(sq).as(subAlias)).groupBy(subAlias.get("prop1"));

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
        Expressions.list(survey.id, survey.name).in(select(survey.id, survey.name).from(survey));

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
    SQLQuery<?> query = new SQLQuery<Void>(SQLTemplates.DEFAULT);
    query
        .withRecursive(
            sub,
            unionAll(
                select(e.id, e.firstname, e.superiorId).from(e).where(e.firstname.eq("Mike")),
                select(e.id, e.firstname, e.superiorId)
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
    SQLQuery<?> query = new SQLQuery<Void>(SQLTemplates.DEFAULT);
    query
        .withRecursive(sub, sub.get(e.id), sub.get(e.firstname), sub.get(e.superiorId))
        .as(
            unionAll(
                select(e.id, e.firstname, e.superiorId).from(e).where(e.firstname.eq("Mike")),
                select(e.id, e.firstname, e.superiorId)
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
with recursive sub (ID, FIRSTNAME, SUPERIOR_ID) as ((select EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.SUPERIOR_ID
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
        SQLExpressions.datediff(DatePart.year, employee.datefield, new java.sql.Date(-offset));
    serializer.handle(expr);
    assertThat(serializer.toString())
        .isEqualTo("datediff('year',EMPLOYEE.DATEFIELD,(date '1970-01-01'))");
  }

  @Test
  public void select_normalization() {
    var serializer = new SQLSerializer(Configuration.DEFAULT);
    serializer.visit(select(Expressions.stringPath("id"), Expressions.stringPath("ID")), null);
    assertThat(serializer).hasToString("(select id, ID as col__ID1\n" + "from dual)");
  }

  @Test
  public void noSchemaInWhere() {
    var defaultWithPrintSchema =
        new Configuration(new SQLTemplates(Keywords.DEFAULT, "\"", '\\', false, false));
    defaultWithPrintSchema.getTemplates().setPrintSchema(true);

    var e = QEmployee.employee;
    var delete =
        new SQLDeleteClause(
            EasyMock.<Connection>createNiceMock(Connection.class), defaultWithPrintSchema, e);
    delete.where(e.id.gt(100));

    assertThat(delete.toString())
        .isEqualTo("delete from PUBLIC.EMPLOYEE\n" + "where EMPLOYEE.ID > ?");
  }

  @Test
  public void schemaInWhere() {
    var derbyWithPrintSchema = new Configuration(DerbyTemplates.builder().printSchema().build());

    var e = QEmployee.employee;
    var delete =
        new SQLDeleteClause(
            EasyMock.<Connection>createNiceMock(Connection.class), derbyWithPrintSchema, e);
    delete.where(e.id.gt(100));

    assertThat(delete.toString())
        .isEqualTo("delete from \"PUBLIC\".EMPLOYEE\n" + "where \"PUBLIC\".EMPLOYEE.ID > ?");
  }

  private SQLQuery<?> query() {
    return new SQLQuery<Void>();
  }

  private SQLQuery<?> queryForMYSQLTemplate() {
    return new SQLQuery<Void>(
        MySQLTemplates.builder().printSchema().newLineToSingleSpace().build());
  }
}
