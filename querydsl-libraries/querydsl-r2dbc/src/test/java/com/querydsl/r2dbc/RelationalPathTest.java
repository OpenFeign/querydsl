package com.querydsl.r2dbc;

import static com.querydsl.core.testutil.Serialization.serialize;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import com.querydsl.r2dbc.domain.QSurvey;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;

public class RelationalPathTest {

  @Test
  public void path() throws ClassNotFoundException, IOException {
    QSurvey survey = QSurvey.survey;
    QSurvey survey2 = serialize(survey);
    assertThat(Arrays.asList(survey2.all())).isEqualTo(Arrays.asList(survey.all()));
    assertThat(survey2.getMetadata()).isEqualTo(survey.getMetadata());
    assertThat(survey2.getMetadata(survey.id)).isEqualTo(survey.getMetadata(survey.id));
  }

  @Test
  public void in_tuple() throws ClassNotFoundException, IOException {
    // (survey.id, survey.name)
    QSurvey survey = QSurvey.survey;
    QTuple tuple = Projections.tuple(survey.id, survey.name);
    serialize(tuple);
    serialize(tuple.newInstance(1, "a"));
  }
}
