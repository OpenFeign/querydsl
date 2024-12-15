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
package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.JoinFlag;
import com.querydsl.sql.domain.QSurvey;
import java.sql.Connection;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class JoinFlagsTest {

  private Connection connection = EasyMock.createMock(Connection.class);

  private QSurvey s1, s2, s3, s4, s5, s6;

  private SQLQuery query;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    s1 = new QSurvey("s");
    s2 = new QSurvey("s2");
    s3 = new QSurvey("s3");
    s4 = new QSurvey("s4");
    s5 = new QSurvey("s5");
    s6 = new QSurvey("s6");
    query = new SQLQuery(connection, SQLTemplates.DEFAULT);
    query.from(s1);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void joinFlags_beforeCondition() {
    query.innerJoin(s2).on(s1.eq(s2));
    query.addJoinFlag(" a ", JoinFlag.Position.BEFORE_CONDITION);

    assertThat(query.toString())
        .isEqualTo(
            """
            from SURVEY s
            inner join SURVEY s2 a\s
            on s.ID = s2.ID""");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void joinFlags_beforeTarget() {
    query.innerJoin(s3).on(s1.eq(s3));
    query.addJoinFlag(" b ", JoinFlag.Position.BEFORE_TARGET);

    assertThat(query.toString())
        .isEqualTo(
            """
            from SURVEY s
            inner join  b SURVEY s3
            on s.ID = s3.ID""");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void joinFlags_end() {
    query.innerJoin(s4).on(s1.eq(s4));
    query.addJoinFlag(" c ", JoinFlag.Position.END);

    assertThat(query.toString())
        .isEqualTo(
            """
            from SURVEY s
            inner join SURVEY s4
            on s.ID = s4.ID c""");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void joinFlags_override() {
    query.innerJoin(s5).on(s1.eq(s5));
    query.addJoinFlag(" d ", JoinFlag.Position.OVERRIDE);

    assertThat(query).hasToString("from SURVEY s d SURVEY s5\n" + "on s.ID = s5.ID");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void joinFlags_start() {
    query.innerJoin(s6).on(s1.eq(s6));
    query.addJoinFlag(" e ", JoinFlag.Position.START);

    assertThat(query.toString())
        .isEqualTo(
            """
            from SURVEY s e\s
            inner join SURVEY s6
            on s.ID = s6.ID""");
  }
}
