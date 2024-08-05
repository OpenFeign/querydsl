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

import static com.querydsl.core.Target.CUBRID;
import static com.querydsl.core.Target.DB2;
import static com.querydsl.core.Target.DERBY;
import static com.querydsl.core.Target.H2;
import static com.querydsl.core.Target.MYSQL;
import static com.querydsl.core.Target.ORACLE;
import static com.querydsl.core.Target.SQLSERVER;
import static com.querydsl.r2dbc.Constants.survey;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.r2dbc.domain.QSurvey;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class UpdateBase extends AbstractBaseTest {

  protected void reset() {
    delete(survey).execute().block();
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
  public void update() {
    // original state
    long count = query().from(survey).fetchCount().block();
    assertThat((long) query().from(survey).where(survey.name.eq("S")).fetchCount().block())
        .isEqualTo(0);

    // update call with 0 update count
    assertThat(
            (long)
                update(survey).where(survey.name.eq("XXX")).set(survey.name, "S").execute().block())
        .isEqualTo(0);
    assertThat((long) query().from(survey).where(survey.name.eq("S")).fetchCount().block())
        .isEqualTo(0);

    // update call with full update count
    assertThat((long) update(survey).set(survey.name, "S").execute().block()).isEqualTo(count);
    assertThat((long) query().from(survey).where(survey.name.eq("S")).fetchCount().block())
        .isEqualTo(count);
  }

  @Test
  @IncludeIn({CUBRID, H2, MYSQL, ORACLE, SQLSERVER})
  public void update_limit() {
    assertThat((long) insert(survey).values(2, "A", "B").execute().block()).isEqualTo(1);
    assertThat((long) insert(survey).values(3, "B", "C").execute().block()).isEqualTo(1);

    assertThat((long) update(survey).set(survey.name, "S").limit(2).execute().block()).isEqualTo(2);
  }

  @Test
  public void update2() {
    List<Path<?>> paths = Collections.singletonList(survey.name);
    List<?> values = Collections.singletonList("S");

    // original state
    long count = query().from(survey).fetchCount().block();
    assertThat((long) query().from(survey).where(survey.name.eq("S")).fetchCount().block())
        .isEqualTo(0);

    // update call with 0 update count
    assertThat(
            (long) update(survey).where(survey.name.eq("XXX")).set(paths, values).execute().block())
        .isEqualTo(0);
    assertThat((long) query().from(survey).where(survey.name.eq("S")).fetchCount().block())
        .isEqualTo(0);

    // update call with full update count
    assertThat((long) update(survey).set(paths, values).execute().block()).isEqualTo(count);
    assertThat((long) query().from(survey).where(survey.name.eq("S")).fetchCount().block())
        .isEqualTo(count);
  }

  @Test
  public void update3() {
    assertThat((long) update(survey).set(survey.name, survey.name.append("X")).execute().block())
        .isEqualTo(1);
  }

  @Test
  public void update4() {
    assertThat((long) insert(survey).values(2, "A", "B").execute().block()).isEqualTo(1);
    assertThat(
            (long)
                update(survey).set(survey.name, "AA").where(survey.name.eq("A")).execute().block())
        .isEqualTo(1);
  }

  @Test
  public void update5() {
    assertThat((long) insert(survey).values(3, "B", "C").execute().block()).isEqualTo(1);
    assertThat(
            (long)
                update(survey).set(survey.name, "BB").where(survey.name.eq("B")).execute().block())
        .isEqualTo(1);
  }

  @Test
  public void setNull() {
    List<Path<?>> paths = Collections.singletonList(survey.name);
    List<?> values = Collections.singletonList(null);
    long count = query().from(survey).fetchCount().block();
    assertThat((long) update(survey).set(paths, values).execute().block()).isEqualTo(count);
  }

  @Test
  public void setNull2() {
    long count = query().from(survey).fetchCount().block();
    assertThat((long) update(survey).set(survey.name, (String) null).execute().block())
        .isEqualTo(count);
  }

  @Test
  @SkipForQuoted
  @ExcludeIn({DB2, DERBY})
  public void setNullEmptyRootPath() {
    var name = Expressions.stringPath("name");
    long count = query().from(survey).fetchCount().block();
    assertThat((long) execute(update(survey).setNull(name)).block()).isEqualTo(count);
  }

  @Test
  public void update_with_subQuery_exists() {
    var survey1 = new QSurvey("s1");
    var employee = new QEmployee("e");
    var update = update(survey1);
    update.set(survey1.name, "AA");
    update.where(
        R2DBCExpressions.selectOne().from(employee).where(survey1.id.eq(employee.id)).exists());
    assertThat((long) update.execute().block()).isEqualTo(1);
  }

  @Test
  public void update_with_subQuery_exists_Params() {
    var survey1 = new QSurvey("s1");
    var employee = new QEmployee("e");

    var param = new Param<Integer>(Integer.class, "param");
    R2DBCQuery<?> sq = query().from(employee).where(employee.id.eq(param));
    sq.set(param, -12478923);

    var update = update(survey1);
    update.set(survey1.name, "AA");
    update.where(sq.exists());
    assertThat((long) update.execute().block()).isEqualTo(0);
  }

  @Test
  public void update_with_subQuery_exists2() {
    var survey1 = new QSurvey("s1");
    var employee = new QEmployee("e");
    var update = update(survey1);
    update.set(survey1.name, "AA");
    update.where(
        R2DBCExpressions.selectOne()
            .from(employee)
            .where(survey1.name.eq(employee.lastname))
            .exists());
    assertThat((long) update.execute().block()).isEqualTo(0);
  }

  @Test
  public void update_with_subQuery_notExists() {
    var survey1 = new QSurvey("s1");
    var employee = new QEmployee("e");
    var update = update(survey1);
    update.set(survey1.name, "AA");
    update.where(query().from(employee).where(survey1.id.eq(employee.id)).notExists());
    assertThat((long) update.execute().block()).isEqualTo(0);
  }
}
