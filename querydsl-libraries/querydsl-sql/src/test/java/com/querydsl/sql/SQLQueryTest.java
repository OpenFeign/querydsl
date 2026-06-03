package com.querydsl.sql;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.querydsl.sql.domain.QSurvey;
import org.junit.jupiter.api.Test;

public class SQLQueryTest {

  @Test
  public void noConnection() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          var survey = QSurvey.survey;
          SQLExpressions.select(survey.id).from(survey).fetch();
        });
  }
}
