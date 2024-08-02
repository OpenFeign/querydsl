package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.r2dbc.domain.QSurvey;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class ListSubQueryTest {

  @Test
  public void hashCode1() {
    var survey = QSurvey.survey;
    var survey2 = new QSurvey("survey2");
    SubQueryExpression<Tuple> query1 = R2DBCExpressions.select(survey.all()).from(survey);
    SubQueryExpression<Tuple> query2 = R2DBCExpressions.select(survey2.all()).from(survey2);

    Set<SubQueryExpression<Tuple>> queries = new HashSet<>();
    queries.add(query1);
    queries.add(query2);
    assertThat(queries).hasSize(2);
  }

  @Test
  public void hashCode2() {
    var survey = new QSurvey("entity");
    var employee = new QEmployee("entity");
    SubQueryExpression<Integer> query1 = R2DBCExpressions.select(survey.id).from(survey);
    SubQueryExpression<Integer> query2 = R2DBCExpressions.select(employee.id).from(employee);

    Set<SubQueryExpression<Integer>> queries = new HashSet<>();
    queries.add(query1);
    queries.add(query2);
    assertThat(queries).hasSize(1);
  }
}
