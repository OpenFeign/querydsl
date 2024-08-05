package com.querydsl.sql;

import com.querydsl.sql.domain.QSurvey;
import org.junit.Test;

public class SQLQueryTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    var survey = QSurvey.survey;
    SQLExpressions.select(survey.id).from(survey).fetch();
  }
}
