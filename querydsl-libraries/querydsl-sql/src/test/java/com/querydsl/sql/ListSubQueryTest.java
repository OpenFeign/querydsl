package com.querydsl.sql;

import static com.querydsl.sql.SQLExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class ListSubQueryTest {

  @Test
  public void hashCode1() {
    var survey = QSurvey.survey;
    var survey2 = new QSurvey("survey2");
    SubQueryExpression<Tuple> query1 = select(survey.all()).from(survey);
    SubQueryExpression<Tuple> query2 = select(survey2.all()).from(survey2);

    Set<SubQueryExpression<Tuple>> queries = new HashSet<>();
    queries.add(query1);
    queries.add(query2);
    assertThat(queries).hasSize(2);
  }

  @Test
  public void hashCode2() {
    var survey = new QSurvey("entity");
    var employee = new QEmployee("entity");
    SubQueryExpression<Integer> query1 = select(survey.id).from(survey);
    SubQueryExpression<Integer> query2 = select(employee.id).from(employee);

    Set<SubQueryExpression<Integer>> queries = new HashSet<>();
    queries.add(query1);
    queries.add(query2);
    assertThat(queries).hasSize(1);
  }
}
