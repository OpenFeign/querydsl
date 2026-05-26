package fluentq.r2dbc;

import fluentq.r2dbc.domain.QSurvey;
import org.junit.Test;

public class R2DBCQueryTest {

  @Test(expected = IllegalStateException.class)
  public void noConnection() {
    var survey = QSurvey.survey;
    R2DBCExpressions.select(survey.id).from(survey).fetch().collectList().block();
  }
}
