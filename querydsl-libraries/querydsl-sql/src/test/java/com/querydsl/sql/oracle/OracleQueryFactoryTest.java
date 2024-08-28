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

import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.domain.QSurvey;
import java.sql.Connection;
import java.util.function.Supplier;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class OracleQueryFactoryTest {

  private OracleQueryFactory queryFactory;

  @Before
  public void setUp() {
    Supplier<Connection> provider = () -> EasyMock.<Connection>createNiceMock(Connection.class);
    queryFactory = new OracleQueryFactory(SQLTemplates.DEFAULT, provider);
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
  public void update() {
    assertThat(queryFactory.update(QSurvey.survey)).isNotNull();
  }

  @Test
  public void merge() {
    assertThat(queryFactory.merge(QSurvey.survey)).isNotNull();
  }
}
