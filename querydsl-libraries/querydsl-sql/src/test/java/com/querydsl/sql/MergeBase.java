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
import static com.querydsl.core.Target.POSTGRESQL;
import static com.querydsl.core.Target.SQLITE;
import static com.querydsl.core.Target.SQLSERVER;
import static com.querydsl.core.Target.TERADATA;
import static com.querydsl.sql.Constants.survey;
import static com.querydsl.sql.Constants.survey2;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.domain.QSurvey;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class MergeBase extends AbstractBaseTest {

  private void reset() throws SQLException {
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
  @ExcludeIn({H2, CUBRID, SQLSERVER, SQLITE})
  public void merge_with_keys() throws SQLException {
    var rs =
        merge(survey)
            .keys(survey.id)
            .set(survey.id, 7)
            .set(survey.name, "Hello World")
            .executeWithKeys();
    assertThat(rs.next()).isTrue();
    assertThat(rs.getObject(1) != null).isTrue();
    rs.close();
  }

  @Test
  @ExcludeIn({H2, CUBRID, SQLSERVER, SQLITE})
  public void merge_with_keys_listener() throws SQLException {
    final var result = new AtomicBoolean();
    SQLListener listener =
        new SQLBaseListener() {
          @Override
          public void end(SQLListenerContext context) {
            result.set(true);
          }
        };
    var clause = merge(survey).keys(survey.id).set(survey.id, 7).set(survey.name, "Hello World");
    clause.addListener(listener);
    var rs = clause.executeWithKeys();
    assertThat(rs.next()).isTrue();
    assertThat(rs.getObject(1) != null).isTrue();
    rs.close();
    assertThat(result.get()).isTrue();
  }

  @Test
  @IncludeIn(H2)
  public void merge_with_keys_and_subQuery() {
    assertThat(insert(survey).set(survey.id, 6).set(survey.name, "H").execute()).isEqualTo(1);

    // keys + subquery
    var survey2 = new QSurvey("survey2");
    assertThat(
            merge(survey)
                .keys(survey.id)
                .select(
                    query().from(survey2).select(survey2.id.add(1), survey2.name, survey2.name2))
                .execute())
        .isEqualTo(2);
  }

  @Test
  @IncludeIn(H2)
  public void merge_with_keys_and_values() {
    // NOTE : doesn't work with composite merge implementation
    // keys + values
    assertThat(merge(survey).keys(survey.id).values(5, "Hello World", "Hello").execute())
        .isEqualTo(1);
  }

  @Test
  public void merge_with_keys_columns_and_values() {
    // keys + columns + values
    assertThat(
            merge(survey)
                .keys(survey.id)
                .set(survey.id, 5)
                .set(survey.name, "Hello World")
                .execute())
        .isEqualTo(1);
  }

  @Test
  public void merge_with_keys_columns_and_values_using_null() {
    // keys + columns + values
    assertThat(
            merge(survey)
                .keys(survey.id)
                .set(survey.id, 5)
                .set(survey.name, (String) null)
                .execute())
        .isEqualTo(1);
  }

  @Test
  @ExcludeIn({CUBRID, DB2, DERBY, POSTGRESQL, SQLSERVER, TERADATA, SQLITE})
  public void merge_with_keys_Null_Id() throws SQLException {
    var rs =
        merge(survey)
            .keys(survey.id)
            .setNull(survey.id)
            .set(survey.name, "Hello World")
            .executeWithKeys();
    assertThat(rs.next()).isTrue();
    assertThat(rs.getObject(1) != null).isTrue();
    rs.close();
  }

  @Test
  @ExcludeIn({H2, CUBRID, SQLSERVER, SQLITE})
  public void merge_with_keys_Projected() throws SQLException {
    assertThat(
            merge(survey)
                .keys(survey.id)
                .set(survey.id, 8)
                .set(survey.name, "Hello you")
                .executeWithKey(survey.id))
        .isNotNull();
  }

  @Test
  @ExcludeIn({H2, CUBRID, SQLSERVER, SQLITE})
  public void merge_with_keys_Projected2() throws SQLException {
    Path<Object> idPath = ExpressionUtils.path(Object.class, "id");
    Object id =
        merge(survey)
            .keys(survey.id)
            .set(survey.id, 9)
            .set(survey.name, "Hello you")
            .executeWithKey(idPath);
    assertThat(id).isNotNull();
  }

  @Test
  @IncludeIn(H2)
  public void mergeBatch() {
    var merge = merge(survey).keys(survey.id).set(survey.id, 5).set(survey.name, "5").addBatch();
    assertThat(merge.getBatchCount()).isEqualTo(1);
    assertThat(merge.isEmpty()).isFalse();

    merge.keys(survey.id).set(survey.id, 6).set(survey.name, "6").addBatch();

    assertThat(merge.getBatchCount()).isEqualTo(2);
    assertThat(merge.execute()).isEqualTo(2);

    assertThat(query().from(survey).where(survey.name.eq("5")).fetchCount()).isEqualTo(1L);
    assertThat(query().from(survey).where(survey.name.eq("6")).fetchCount()).isEqualTo(1L);
  }

  @Test
  @IncludeIn(H2)
  public void mergeBatch_templates() {
    var merge =
        merge(survey)
            .keys(survey.id)
            .set(survey.id, 5)
            .set(survey.name, Expressions.stringTemplate("'5'"))
            .addBatch();

    merge
        .keys(survey.id)
        .set(survey.id, 6)
        .set(survey.name, Expressions.stringTemplate("'6'"))
        .addBatch();

    assertThat(merge.execute()).isEqualTo(2);

    assertThat(query().from(survey).where(survey.name.eq("5")).fetchCount()).isEqualTo(1L);
    assertThat(query().from(survey).where(survey.name.eq("6")).fetchCount()).isEqualTo(1L);
  }

  @Test
  @IncludeIn(H2)
  public void mergeBatch_with_subquery() {
    var merge =
        merge(survey)
            .keys(survey.id)
            .columns(survey.id, survey.name)
            .select(query().from(survey2).select(survey2.id.add(20), survey2.name))
            .addBatch();

    merge(survey)
        .keys(survey.id)
        .columns(survey.id, survey.name)
        .select(query().from(survey2).select(survey2.id.add(40), survey2.name))
        .addBatch();

    assertThat(merge.execute()).isEqualTo(1);
  }

  @Test
  @IncludeIn(H2)
  public void merge_with_templateExpression_in_batch() {
    var merge =
        merge(survey)
            .keys(survey.id)
            .set(survey.id, 5)
            .set(survey.name, Expressions.stringTemplate("'5'"))
            .addBatch();

    assertThat(merge.execute()).isEqualTo(1);
  }

  @Test
  public void merge_listener() {
    final var calls = new AtomicInteger(0);
    SQLListener listener =
        new SQLBaseListener() {
          @Override
          public void end(SQLListenerContext context) {
            if (context.getData(AbstractSQLQuery.PARENT_CONTEXT) == null) {
              calls.incrementAndGet();
            }
          }
        };

    var clause = merge(survey).keys(survey.id).set(survey.id, 5).set(survey.name, "Hello World");
    clause.addListener(listener);
    assertThat(clause.execute()).isEqualTo(1);
    assertThat(calls.intValue()).isEqualTo(1);
  }
}
