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
package com.querydsl.sql.mysql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.domain.QSurvey;
import java.sql.Connection;
import java.util.function.Supplier;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class MySQLQueryFactoryTest {

  private MySQLQueryFactory queryFactory;

  @Before
  public void setUp() {
    Supplier<Connection> provider = () -> EasyMock.<Connection>createNiceMock(Connection.class);
    queryFactory = new MySQLQueryFactory(SQLTemplates.DEFAULT, provider);
  }

  @Test
  public void query() {
    assertNotNull(queryFactory.query());
  }

  @Test
  public void from() {
    assertNotNull(queryFactory.from(QSurvey.survey));
  }

  @Test
  public void delete() {
    assertNotNull(queryFactory.delete(QSurvey.survey));
  }

  @Test
  public void insert() {
    assertNotNull(queryFactory.insert(QSurvey.survey));
  }

  @Test
  public void insertIgnore() {
    SQLInsertClause clause = queryFactory.insertIgnore(QSurvey.survey);
    assertEquals("insert ignore into SURVEY\nvalues ()", clause.toString());
  }

  @Test
  public void insertOnDuplicateKeyUpdate() {
    SQLInsertClause clause = queryFactory.insertOnDuplicateKeyUpdate(QSurvey.survey, "c = c+1");
    assertEquals(
        "insert into SURVEY\nvalues () on duplicate key update c = c+1", clause.toString());
  }

  @Test
  public void insertOnDuplicateKeyUpdate2() {
    SQLInsertClause clause =
        queryFactory.insertOnDuplicateKeyUpdate(QSurvey.survey, QSurvey.survey.id.eq(2));
    assertEquals(
        "insert into SURVEY\nvalues () on duplicate key update SURVEY.ID = ?", clause.toString());
  }

  @Test
  public void insertOnDuplicateKeyUpdate_multiple() {
    SQLInsertClause clause =
        queryFactory.insertOnDuplicateKeyUpdate(
            QSurvey.survey,
            SQLExpressions.set(QSurvey.survey.id, 2),
            SQLExpressions.set(QSurvey.survey.name, "B"));
    assertEquals(
        "insert into SURVEY\n" + "values () on duplicate key update SURVEY.ID = ?, SURVEY.NAME = ?",
        clause.toString());
  }

  @Test
  public void insertOnDuplicateKeyUpdate_values() {
    SQLInsertClause clause =
        queryFactory.insertOnDuplicateKeyUpdate(
            QSurvey.survey, SQLExpressions.set(QSurvey.survey.name, QSurvey.survey.name));
    assertEquals(
        "insert into SURVEY\n"
            + "values () on duplicate key update SURVEY.NAME = values(SURVEY.NAME)",
        clause.toString());
  }

  @Test
  public void insertOnDuplicateKeyUpdate_null() {
    SQLInsertClause clause =
        queryFactory.insertOnDuplicateKeyUpdate(
            QSurvey.survey, SQLExpressions.set(QSurvey.survey.name, (String) null));
    assertEquals(
        "insert into SURVEY\n" + "values () on duplicate key update SURVEY.NAME = null",
        clause.toString());
  }

  @Test
  public void replace() {
    assertNotNull(queryFactory.replace(QSurvey.survey));
  }

  @Test
  public void update() {
    assertNotNull(queryFactory.update(QSurvey.survey));
  }

  @Test
  public void merge() {
    assertNotNull(queryFactory.merge(QSurvey.survey));
  }
}
