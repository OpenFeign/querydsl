package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Param;
import com.querydsl.r2dbc.domain.QSurvey;
import java.util.Arrays;
import org.junit.Test;

public class SQLBindingsTest {

  private QSurvey survey = QSurvey.survey;

  private R2DBCQuery<?> query = new R2DBCQuery<Void>();

  @Test
  public void empty() {
    var bindings = query.getSQL();
    assertThat(bindings.getSQL()).isEqualTo("\nfrom dual");
    assertThat(bindings.getNullFriendlyBindings()).isEmpty();
  }

  @Test
  public void singleArg() {
    query.from(survey).where(survey.name.eq("Bob")).select(survey.id);
    var bindings = query.getSQL();
    assertThat(bindings.getSQL())
        .isEqualTo("select SURVEY.ID\nfrom SURVEY SURVEY\nwhere SURVEY.NAME = ?");
    assertThat(bindings.getNullFriendlyBindings()).isEqualTo(Arrays.asList("Bob"));
  }

  @Test
  public void twoArgs() {
    query.from(survey).where(survey.name.eq("Bob"), survey.name2.eq("A")).select(survey.id);
    var bindings = query.getSQL();
    assertThat(bindings.getSQL())
        .isEqualTo(
            "select SURVEY.ID\nfrom SURVEY SURVEY\nwhere SURVEY.NAME = ? and SURVEY.NAME2 = ?");
    assertThat(bindings.getNullFriendlyBindings()).isEqualTo(Arrays.asList("Bob", "A"));
  }

  @Test
  public void params() {
    var name = new Param<String>(String.class, "name");
    query.from(survey).where(survey.name.eq(name), survey.name2.eq("A")).select(survey.id);
    query.set(name, "Bob");
    var bindings = query.getSQL();
    assertThat(bindings.getSQL())
        .isEqualTo(
            "select SURVEY.ID\nfrom SURVEY SURVEY\nwhere SURVEY.NAME = ? and SURVEY.NAME2 = ?");
    assertThat(bindings.getNullFriendlyBindings()).isEqualTo(Arrays.asList("Bob", "A"));
  }
}
