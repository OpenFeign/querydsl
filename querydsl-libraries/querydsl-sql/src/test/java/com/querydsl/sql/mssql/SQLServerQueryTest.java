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
package com.querydsl.sql.mssql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.SQLServerTemplates;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import org.junit.Test;

public class SQLServerQueryTest {

  private static final QSurvey survey = QSurvey.survey;

  @Test
  public void tableHints_single() {
    SQLServerQuery<?> query = new SQLServerQuery<Void>(null, new SQLServerTemplates());
    query.from(survey).tableHints(SQLServerTableHints.NOWAIT).where(survey.name.isNull());
    assertThat(query.toString())
        .isEqualTo("from SURVEY SURVEY with (NOWAIT)\nwhere SURVEY.NAME is null");
  }

  @Test
  public void tableHints_multiple() {
    SQLServerQuery<?> query = new SQLServerQuery<Void>(null, new SQLServerTemplates());
    query
        .from(survey)
        .tableHints(SQLServerTableHints.NOWAIT, SQLServerTableHints.NOLOCK)
        .where(survey.name.isNull());
    assertThat(query.toString())
        .isEqualTo("from SURVEY SURVEY with (NOWAIT, NOLOCK)\nwhere SURVEY.NAME is null");
  }

  @Test
  public void tableHints_multiple2() {
    var survey2 = new QSurvey("survey2");
    SQLServerQuery<?> query = new SQLServerQuery<Void>(null, new SQLServerTemplates());
    query
        .from(survey)
        .tableHints(SQLServerTableHints.NOWAIT)
        .from(survey2)
        .tableHints(SQLServerTableHints.NOLOCK)
        .where(survey.name.isNull());
    assertThat(query.toString())
        .isEqualTo(
            """
            from SURVEY SURVEY with (NOWAIT), SURVEY survey2 with (NOLOCK)
            where SURVEY.NAME is null\
            """);
  }

  @Test
  public void join_tableHints_single() {
    var employee1 = QEmployee.employee;
    var employee2 = new QEmployee("employee2");
    SQLServerQuery<?> query = new SQLServerQuery<Void>(null, new SQLServerTemplates());
    query
        .from(employee1)
        .tableHints(SQLServerTableHints.NOLOCK)
        .join(employee2)
        .tableHints(SQLServerTableHints.NOLOCK)
        .on(employee1.superiorId.eq(employee2.id));
    assertThat(query.toString())
        .isEqualTo(
            """
            from EMPLOYEE EMPLOYEE with (NOLOCK)
            join EMPLOYEE employee2 with (NOLOCK)
            on EMPLOYEE.SUPERIOR_ID = employee2.ID\
            """);
  }

  @Test
  public void join_tableHints_multiple() {
    var employee1 = QEmployee.employee;
    var employee2 = new QEmployee("employee2");
    SQLServerQuery<?> query = new SQLServerQuery<Void>(null, new SQLServerTemplates());
    query
        .from(employee1)
        .tableHints(SQLServerTableHints.NOLOCK, SQLServerTableHints.READUNCOMMITTED)
        .join(employee2)
        .tableHints(SQLServerTableHints.NOLOCK, SQLServerTableHints.READUNCOMMITTED)
        .on(employee1.superiorId.eq(employee2.id));
    assertThat(query.toString())
        .isEqualTo(
            """
            from EMPLOYEE EMPLOYEE with (NOLOCK, READUNCOMMITTED)
            join EMPLOYEE employee2 with (NOLOCK, READUNCOMMITTED)
            on EMPLOYEE.SUPERIOR_ID = employee2.ID\
            """);
  }
}
