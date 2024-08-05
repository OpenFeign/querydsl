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

import static com.querydsl.core.Target.CUBRID;
import static com.querydsl.core.Target.DB2;
import static com.querydsl.core.Target.DERBY;
import static com.querydsl.core.Target.H2;
import static com.querydsl.core.Target.MYSQL;
import static com.querydsl.core.Target.ORACLE;
import static com.querydsl.core.Target.SQLSERVER;
import static com.querydsl.core.Target.TERADATA;
import static com.querydsl.sql.Constants.survey;
import static com.querydsl.sql.SQLExpressions.selectOne;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class UpdateBase extends AbstractBaseTest {

  protected void reset() throws SQLException {
    delete(survey).execute();
    insert(survey).values(1, "Hello World", "Hello").execute();
  }

  @Before
  public void setUp() throws SQLException {
    reset();
  }

  @After
  public void tearDown() throws SQLException {
    reset();
  }

  @Test
  public void update() throws SQLException {
    // original state
    var count = query().from(survey).fetchCount();
    assertThat(query().from(survey).where(survey.name.eq("S")).fetchCount()).isEqualTo(0);

    // update call with 0 update count
    assertThat(update(survey).where(survey.name.eq("XXX")).set(survey.name, "S").execute())
        .isEqualTo(0);
    assertThat(query().from(survey).where(survey.name.eq("S")).fetchCount()).isEqualTo(0);

    // update call with full update count
    assertThat(update(survey).set(survey.name, "S").execute()).isEqualTo(count);
    assertThat(query().from(survey).where(survey.name.eq("S")).fetchCount()).isEqualTo(count);
  }

  @Test
  @IncludeIn({CUBRID, H2, MYSQL, ORACLE, SQLSERVER})
  public void update_limit() {
    assertThat(insert(survey).values(2, "A", "B").execute()).isEqualTo(1);
    assertThat(insert(survey).values(3, "B", "C").execute()).isEqualTo(1);

    assertThat(update(survey).set(survey.name, "S").limit(2).execute()).isEqualTo(2);
  }

  @Test
  public void update2() throws SQLException {
    List<Path<?>> paths = Collections.<Path<?>>singletonList(survey.name);
    List<?> values = Collections.singletonList("S");

    // original state
    var count = query().from(survey).fetchCount();
    assertThat(query().from(survey).where(survey.name.eq("S")).fetchCount()).isEqualTo(0);

    // update call with 0 update count
    assertThat(update(survey).where(survey.name.eq("XXX")).set(paths, values).execute())
        .isEqualTo(0);
    assertThat(query().from(survey).where(survey.name.eq("S")).fetchCount()).isEqualTo(0);

    // update call with full update count
    assertThat(update(survey).set(paths, values).execute()).isEqualTo(count);
    assertThat(query().from(survey).where(survey.name.eq("S")).fetchCount()).isEqualTo(count);
  }

  @Test
  public void update3() {
    assertThat(update(survey).set(survey.name, survey.name.append("X")).execute()).isEqualTo(1);
  }

  @Test
  public void update4() {
    assertThat(insert(survey).values(2, "A", "B").execute()).isEqualTo(1);
    assertThat(update(survey).set(survey.name, "AA").where(survey.name.eq("A")).execute())
        .isEqualTo(1);
  }

  @Test
  public void update5() {
    assertThat(insert(survey).values(3, "B", "C").execute()).isEqualTo(1);
    assertThat(update(survey).set(survey.name, "BB").where(survey.name.eq("B")).execute())
        .isEqualTo(1);
  }

  @Test
  public void setNull() {
    List<Path<?>> paths = Collections.<Path<?>>singletonList(survey.name);
    List<?> values = Collections.singletonList(null);
    var count = query().from(survey).fetchCount();
    assertThat(update(survey).set(paths, values).execute()).isEqualTo(count);
  }

  @Test
  public void setNull2() {
    var count = query().from(survey).fetchCount();
    assertThat(update(survey).set(survey.name, (String) null).execute()).isEqualTo(count);
  }

  @Test
  @SkipForQuoted
  @ExcludeIn({DB2, DERBY})
  public void setNullEmptyRootPath() {
    var name = Expressions.stringPath("name");
    var count = query().from(survey).fetchCount();
    assertThat(execute(update(survey).setNull(name))).isEqualTo(count);
  }

  @Test
  public void batch() throws SQLException {
    assertThat(insert(survey).values(2, "A", "B").execute()).isEqualTo(1);
    assertThat(insert(survey).values(3, "B", "C").execute()).isEqualTo(1);

    var update = update(survey);
    update.set(survey.name, "AA").where(survey.name.eq("A")).addBatch();
    assertThat(update.getBatchCount()).isEqualTo(1);
    update.set(survey.name, "BB").where(survey.name.eq("B")).addBatch();
    assertThat(update.getBatchCount()).isEqualTo(2);
    assertThat(update.execute()).isEqualTo(2);
  }

  @Test
  public void batch_templates() throws SQLException {
    assertThat(insert(survey).values(2, "A", "B").execute()).isEqualTo(1);
    assertThat(insert(survey).values(3, "B", "C").execute()).isEqualTo(1);

    var update = update(survey);
    update
        .set(survey.name, "AA")
        .where(survey.name.eq(Expressions.stringTemplate("'A'")))
        .addBatch();
    update
        .set(survey.name, "BB")
        .where(survey.name.eq(Expressions.stringTemplate("'B'")))
        .addBatch();
    assertThat(update.execute()).isEqualTo(2);
  }

  @Test
  public void update_with_subQuery_exists() {
    var survey1 = new QSurvey("s1");
    var employee = new QEmployee("e");
    var update = update(survey1);
    update.set(survey1.name, "AA");
    update.where(selectOne().from(employee).where(survey1.id.eq(employee.id)).exists());
    assertThat(update.execute()).isEqualTo(1);
  }

  @Test
  public void update_with_subQuery_exists_Params() {
    var survey1 = new QSurvey("s1");
    var employee = new QEmployee("e");

    var param = new Param<Integer>(Integer.class, "param");
    SQLQuery<?> sq = query().from(employee).where(employee.id.eq(param));
    sq.set(param, -12478923);

    var update = update(survey1);
    update.set(survey1.name, "AA");
    update.where(sq.exists());
    assertThat(update.execute()).isEqualTo(0);
  }

  @Test
  public void update_with_subQuery_exists2() {
    var survey1 = new QSurvey("s1");
    var employee = new QEmployee("e");
    var update = update(survey1);
    update.set(survey1.name, "AA");
    update.where(selectOne().from(employee).where(survey1.name.eq(employee.lastname)).exists());
    assertThat(update.execute()).isEqualTo(0);
  }

  @Test
  public void update_with_subQuery_notExists() {
    var survey1 = new QSurvey("s1");
    var employee = new QEmployee("e");
    var update = update(survey1);
    update.set(survey1.name, "AA");
    update.where(query().from(employee).where(survey1.id.eq(employee.id)).notExists());
    assertThat(update.execute()).isEqualTo(0);
  }

  @Test
  @ExcludeIn(TERADATA)
  public void update_with_templateExpression_in_batch() {
    assertThat(
            update(survey)
                .set(survey.id, 3)
                .set(survey.name, Expressions.stringTemplate("'Hello'"))
                .addBatch()
                .execute())
        .isEqualTo(1);
  }
}
