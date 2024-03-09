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
package com.querydsl.r2dbc;

import static com.querydsl.core.Target.*;
import static com.querydsl.r2dbc.Constants.survey;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.r2dbc.dml.R2DBCDeleteClause;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.r2dbc.domain.QSurvey;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class DeleteBase extends AbstractBaseTest {

  private void reset() {
    delete(survey).where(survey.name.isNotNull()).execute().block();
    insert(survey).values(1, "Hello World", "Hello").execute().block();
  }

  @Before
  public void setUp() {
    reset();
  }

  @After
  public void tearDown() {
    reset();
  }

  @Test
  @ExcludeIn(MYSQL)
  public void delete() {
    Long count = query().from(survey).fetchCount().block();
    assertThat((long) delete(survey).where(survey.name.eq("XXX")).execute().block()).isEqualTo(0);
    assertThat(delete(survey).execute().block()).isEqualTo(count);
  }

  @Test
  @IncludeIn({CUBRID, H2, MYSQL, ORACLE, SQLSERVER})
  public void delete_limit() {
    insert(survey).values(2, "A", "B").execute().block();
    insert(survey).values(3, "B", "C").execute().block();
    insert(survey).values(4, "D", "E").execute().block();

    assertThat((long) delete(survey).limit(2).execute().block()).isEqualTo(2);
  }

  @Test
  public void delete_with_subQuery_exists() {
    QSurvey survey1 = new QSurvey("s1");
    QEmployee employee = new QEmployee("e");
    R2DBCDeleteClause delete = delete(survey1);
    delete.where(
        survey1.name.eq("XXX"), query().from(employee).where(survey1.id.eq(employee.id)).exists());
    assertThat((long) delete.execute().block()).isEqualTo(0);
  }

  @Test
  public void delete_with_subQuery_exists_Params() {
    QSurvey survey1 = new QSurvey("s1");
    QEmployee employee = new QEmployee("e");

    Param<Integer> param = new Param<Integer>(Integer.class, "param");
    R2DBCQuery<?> sq = query().from(employee).where(employee.id.eq(param));
    sq.set(param, -12478923);

    R2DBCDeleteClause delete = delete(survey1);
    delete.where(survey1.name.eq("XXX"), sq.exists());
    assertThat((long) delete.execute().block()).isEqualTo(0);
  }

  @Test
  public void delete_with_subQuery_exists2() {
    QSurvey survey1 = new QSurvey("s1");
    QEmployee employee = new QEmployee("e");
    R2DBCDeleteClause delete = delete(survey1);
    delete.where(
        survey1.name.eq("XXX"),
        query().from(employee).where(survey1.name.eq(employee.lastname)).exists());
    assertThat((long) delete.execute().block()).isEqualTo(0);
  }
}
