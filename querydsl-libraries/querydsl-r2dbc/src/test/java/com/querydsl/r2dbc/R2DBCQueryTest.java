package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.querydsl.r2dbc.domain.QSurvey;
import org.junit.jupiter.api.Test;

public class R2DBCQueryTest {

  @Test
  public void noConnection() {
    assertThatThrownBy(
            () -> {
              var survey = QSurvey.survey;
              R2DBCExpressions.select(survey.id).from(survey).fetch().collectList().block();
            })
        .isInstanceOf(IllegalStateException.class);
  }
}
