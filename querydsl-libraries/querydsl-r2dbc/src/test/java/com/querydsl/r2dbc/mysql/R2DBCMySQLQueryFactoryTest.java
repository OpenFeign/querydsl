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
package com.querydsl.r2dbc.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.r2dbc.R2DBCConnectionProvider;
import com.querydsl.r2dbc.R2DBCExpressions;
import com.querydsl.r2dbc.SQLTemplates;
import com.querydsl.r2dbc.domain.QSurvey;
import io.r2dbc.spi.Connection;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;

public class R2DBCMySQLQueryFactoryTest {

  private R2DBCMySQLQueryFactory queryFactory;

  @Before
  public void setUp() {
    R2DBCConnectionProvider provider = () -> Mono.just(EasyMock.createNiceMock(Connection.class));
    queryFactory = new R2DBCMySQLQueryFactory(SQLTemplates.DEFAULT, provider);
  }

  @Test
  public void query() {
    assertThat(queryFactory.query()).isNotNull();
  }

  @Test
  public void from() {
    assertThat(queryFactory.from(QSurvey.survey)).isNotNull();
  }

  @Test
  public void delete() {
    assertThat(queryFactory.delete(QSurvey.survey)).isNotNull();
  }

  @Test
  public void insert() {
    assertThat(queryFactory.insert(QSurvey.survey)).isNotNull();
  }

  @Test
  public void insertIgnore() {
    var clause = queryFactory.insertIgnore(QSurvey.survey);
    assertThat(clause).hasToString("insert ignore into SURVEY\nvalues ()");
  }

  @Test
  public void insertOnDuplicateKeyUpdate() {
    var clause = queryFactory.insertOnDuplicateKeyUpdate(QSurvey.survey, "c = c+1");
    assertThat(clause.toString())
        .isEqualTo("insert into SURVEY\nvalues () on duplicate key update c = c+1");
  }

  @Test
  public void insertOnDuplicateKeyUpdate2() {
    var clause = queryFactory.insertOnDuplicateKeyUpdate(QSurvey.survey, QSurvey.survey.id.eq(2));
    assertThat(clause.toString())
        .isEqualTo("insert into SURVEY\nvalues () on duplicate key update SURVEY.ID = ?");
  }

  @Test
  public void insertOnDuplicateKeyUpdate_multiple() {
    var clause =
        queryFactory.insertOnDuplicateKeyUpdate(
            QSurvey.survey,
            R2DBCExpressions.set(QSurvey.survey.id, 2),
            R2DBCExpressions.set(QSurvey.survey.name, "B"));
    assertThat(clause.toString())
        .isEqualTo(
            """
            insert into SURVEY
            values () on duplicate key update SURVEY.ID = ?, SURVEY.NAME = ?\
            """);
  }

  @Test
  public void insertOnDuplicateKeyUpdate_values() {
    var clause =
        queryFactory.insertOnDuplicateKeyUpdate(
            QSurvey.survey, R2DBCExpressions.set(QSurvey.survey.name, QSurvey.survey.name));
    assertThat(clause.toString())
        .isEqualTo(
            """
            insert into SURVEY
            values () on duplicate key update SURVEY.NAME = values(SURVEY.NAME)\
            """);
  }

  @Test
  public void insertOnDuplicateKeyUpdate_null() {
    var clause =
        queryFactory.insertOnDuplicateKeyUpdate(
            QSurvey.survey, R2DBCExpressions.set(QSurvey.survey.name, (String) null));
    assertThat(clause.toString())
        .isEqualTo("insert into SURVEY\n" + "values () on duplicate key update SURVEY.NAME = null");
  }

  @Test
  public void update() {
    assertThat(queryFactory.update(QSurvey.survey)).isNotNull();
  }
}
