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
package com.querydsl.sql.oracle;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Before;
import org.junit.Test;

public class OracleQueryTest {

  private OracleQuery<?> query;

  private QSurvey survey = new QSurvey("survey");

  @Before
  public void setUp() {
    query = new OracleQuery<Void>(null, OracleTemplates.builder().newLineToSingleSpace().build());
    query.from(survey);
    query.orderBy(survey.name.asc());
  }

  @Test
  public void connectByPrior() {
    query.connectByPrior(survey.name.isNull());
    assertThat(toString(query))
        .isEqualTo(
            "from SURVEY survey connect by prior survey.NAME is null order by survey.NAME asc");
  }

  @Test
  public void connectBy() {
    query.connectByPrior(survey.name.isNull());
    assertThat(toString(query))
        .isEqualTo(
            "from SURVEY survey connect by prior survey.NAME is null order by survey.NAME asc");
  }

  @Test
  public void connectByNocyclePrior() {
    query.connectByNocyclePrior(survey.name.isNull());
    assertThat(toString(query))
        .isEqualTo(
            """
            from SURVEY survey connect by nocycle prior survey.NAME is null order by survey.NAME\
             asc\
            """);
  }

  @Test
  public void startWith() {
    query.startWith(survey.name.isNull());
    assertThat(toString(query))
        .isEqualTo("from SURVEY survey start with survey.NAME is null order by survey.NAME asc");
  }

  @Test
  public void orderSiblingsBy() {
    query.orderSiblingsBy(survey.name);
    assertThat(toString(query))
        .isEqualTo("from SURVEY survey order siblings by survey.NAME order by survey.NAME asc");
  }

  @Test
  public void rowNum() {
    query.where(OracleGrammar.rownum.lt(5));
    assertThat(toString(query))
        .isEqualTo("from SURVEY survey where rownum < ? order by survey.NAME asc");
  }

  private String toString(OracleQuery query) {
    return query.toString().replace('\n', ' ');
  }
}
