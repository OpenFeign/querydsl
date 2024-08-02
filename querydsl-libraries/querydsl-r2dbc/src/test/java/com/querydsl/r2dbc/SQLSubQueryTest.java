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

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.r2dbc.domain.QSurvey;
import java.util.List;
import org.junit.Test;

public class SQLSubQueryTest {

  private static final QEmployee employee = QEmployee.employee;

  @Test(expected = IllegalArgumentException.class)
  public void unknownOperator() {
    Operator op =
        new Operator() {
          @Override
          public String name() {
            return "unknownfn";
          }

          @Override
          public String toString() {
            return name();
          }

          @Override
          public Class<?> getType() {
            return Object.class;
          }
        };
    R2DBCQuery<?> query = new R2DBCQuery<Void>();
    query.from(employee).where(Expressions.booleanOperation(op, employee.id)).toString();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void list() {
    SubQueryExpression<?> subQuery =
        R2DBCExpressions.select(employee.id, Expressions.constant("XXX"), employee.firstname)
            .from(employee);
    List<? extends Expression<?>> exprs =
        ((FactoryExpression) subQuery.getMetadata().getProjection()).getArgs();
    assertThat(exprs.getFirst()).isEqualTo(employee.id);
    assertThat(exprs.get(1)).isEqualTo(ConstantImpl.create("XXX"));
    assertThat(exprs.get(2)).isEqualTo(employee.firstname);
  }

  @Test
  public void list_entity() {
    var employee2 = new QEmployee("employee2");
    Expression<?> expr =
        R2DBCExpressions.select(employee, employee2.id)
            .from(employee)
            .innerJoin(employee.superiorIdKey, employee2);

    var serializer = new SQLSerializer(new Configuration(SQLTemplates.DEFAULT));
    serializer.handle(expr);

    assertThat(serializer.toString())
        .isEqualTo(
            """
            (select EMPLOYEE.ID, EMPLOYEE.FIRSTNAME, EMPLOYEE.LASTNAME, EMPLOYEE.SALARY,\
             EMPLOYEE.DATEFIELD, EMPLOYEE.TIMEFIELD, EMPLOYEE.SUPERIOR_ID, employee2.ID as\
             col__ID7
            from EMPLOYEE EMPLOYEE
            inner join EMPLOYEE employee2
            on EMPLOYEE.SUPERIOR_ID = employee2.ID)\
            """);
  }

  @Test
  public void in() {
    SubQueryExpression<Integer> ints = R2DBCExpressions.select(employee.id).from(employee);
    QEmployee.employee.id.in(ints);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void in_union() {
    SubQueryExpression<Integer> ints1 = R2DBCExpressions.select(employee.id).from(employee);
    SubQueryExpression<Integer> ints2 = R2DBCExpressions.select(employee.id).from(employee);
    QEmployee.employee.id.in(R2DBCExpressions.union(ints1, ints2));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void in_union2() {
    SubQueryExpression<Integer> ints1 = R2DBCExpressions.select(employee.id).from(employee);
    SubQueryExpression<Integer> ints2 = R2DBCExpressions.select(employee.id).from(employee);
    QEmployee.employee.id.in(R2DBCExpressions.union(ints1, ints2));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void unique() {
    SubQueryExpression<?> subQuery =
        R2DBCExpressions.select(employee.id, Expressions.constant("XXX"), employee.firstname)
            .from(employee);
    List<? extends Expression<?>> exprs =
        ((FactoryExpression) subQuery.getMetadata().getProjection()).getArgs();
    assertThat(exprs.getFirst()).isEqualTo(employee.id);
    assertThat(exprs.get(1)).isEqualTo(ConstantImpl.create("XXX"));
    assertThat(exprs.get(2)).isEqualTo(employee.firstname);
  }

  @Test
  public void complex() {
    // related to #584795
    var survey = new QSurvey("survey");
    var emp1 = new QEmployee("emp1");
    var emp2 = new QEmployee("emp2");
    SubQueryExpression<?> subQuery =
        R2DBCExpressions.select(survey.id, emp2.firstname)
            .from(survey)
            .innerJoin(emp1)
            .on(survey.id.eq(emp1.id))
            .innerJoin(emp2)
            .on(emp1.superiorId.eq(emp2.superiorId), emp1.firstname.eq(emp2.firstname));

    assertThat(subQuery.getMetadata().getJoins()).hasSize(3);
  }

  @Test
  public void validate() {
    NumberPath<Long> operatorTotalPermits =
        Expressions.numberPath(Long.class, "operator_total_permits");
    var survey = new QSurvey("survey");

    // select survey.name, count(*) as operator_total_permits
    // from survey
    // where survey.name >= "A"
    // group by survey.name
    // order by operator_total_permits asc
    // limit 10

    Expression<?> e =
        R2DBCExpressions.select(survey.name, Wildcard.count.as(operatorTotalPermits))
            .from(survey)
            .where(survey.name.goe("A"))
            .groupBy(survey.name)
            .orderBy(operatorTotalPermits.asc())
            .limit(10)
            .as("top");

    R2DBCExpressions.select(Wildcard.all).from(e);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void union1() {
    var survey = QSurvey.survey;
    SubQueryExpression<Integer> q1 = R2DBCExpressions.select(survey.id).from(survey);
    SubQueryExpression<Integer> q2 = R2DBCExpressions.select(survey.id).from(survey);
    R2DBCExpressions.union(q1, q2);
    R2DBCExpressions.union(q1);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void union1_with() {
    var survey1 = new QSurvey("survey1");
    var survey2 = new QSurvey("survey2");
    var survey3 = new QSurvey("survey3");

    var query = new R2DBCQuery<>();
    query.with(survey1, R2DBCExpressions.select(survey1.all()).from(survey1));
    query.union(
        R2DBCExpressions.select(survey2.all()).from(survey2),
        R2DBCExpressions.select(survey3.all()).from(survey3));

    assertThat(query.toString())
        .isEqualTo(
            """
            with survey1 as (select survey1.NAME, survey1.NAME2, survey1.ID
            from SURVEY survey1)
            (select survey2.NAME, survey2.NAME2, survey2.ID
            from SURVEY survey2)
            union
            (select survey3.NAME, survey3.NAME2, survey3.ID
            from SURVEY survey3)\
            """);
  }
}
