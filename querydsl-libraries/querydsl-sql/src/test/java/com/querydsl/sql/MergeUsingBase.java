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

import static com.querydsl.core.Target.*;
import static com.querydsl.sql.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.sql.dml.SQLMergeUsingClause;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MergeUsingBase extends AbstractBaseTest {

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
  @IncludeIn({DB2, SQLSERVER, H2, POSTGRESQL})
  public void merge_with_using() {
    QSurvey usingSubqueryAlias = new QSurvey("USING_SUBSELECT");
    SQLMergeUsingClause merge =
        merge(survey)
            .using(
                query()
                    .from(survey2)
                    .select(survey2.id.add(40).as("ID"), survey2.name)
                    .as(usingSubqueryAlias))
            .on(survey.id.eq(usingSubqueryAlias.id))
            .whenNotMatched()
            .thenInsert(
                Arrays.asList(survey.id, survey.name),
                Arrays.asList(usingSubqueryAlias.id, usingSubqueryAlias.name))
            .whenMatched()
            .and(survey.id.goe(10))
            .thenDelete()
            .whenMatched()
            .thenUpdate(
                Collections.singletonList(survey.name),
                Collections.singletonList(usingSubqueryAlias.name));

    assertThat(merge.execute()).isEqualTo(1);
  }

  @Test
  @IncludeIn({DB2, SQLSERVER, H2, POSTGRESQL})
  public void merge_with_using_insert() {
    QSurvey usingSubqueryAlias = new QSurvey("USING_SUBSELECT");
    SQLMergeUsingClause merge =
        merge(survey)
            .using(
                query()
                    .from(survey2)
                    .select(survey2.id.add(40).as("ID"), survey2.name)
                    .as(usingSubqueryAlias))
            .on(survey.id.eq(usingSubqueryAlias.id))
            .whenNotMatched()
            .thenInsert(
                Arrays.asList(survey.id, survey.name),
                Arrays.asList(usingSubqueryAlias.id, usingSubqueryAlias.name));

    assertThat(merge.execute()).isEqualTo(1);
  }

  @Test
  @IncludeIn({DB2, SQLSERVER, H2, POSTGRESQL})
  public void merge_with_using_delete() {
    QSurvey usingSubqueryAlias = new QSurvey("USING_SUBSELECT");
    SQLMergeUsingClause merge =
        merge(survey)
            .using(query().from(survey2).select(survey2.id, survey2.name).as(usingSubqueryAlias))
            .on(survey.id.eq(usingSubqueryAlias.id))
            .whenMatched()
            .thenDelete();

    assertThat(merge.execute()).isEqualTo(1);
  }

  @Test
  @IncludeIn({DB2, SQLSERVER, H2, POSTGRESQL})
  public void merge_with_using_update() {
    QSurvey usingSubqueryAlias = new QSurvey("USING_SUBSELECT");
    SQLMergeUsingClause merge =
        merge(survey)
            .using(
                query()
                    .from(survey2)
                    .select(survey2.id, survey2.name.append("new").as("NAME"))
                    .as(usingSubqueryAlias))
            .on(survey.id.eq(usingSubqueryAlias.id))
            .whenMatched()
            .thenUpdate(List.of(survey.name), List.of(usingSubqueryAlias.name));

    assertThat(merge.execute()).isEqualTo(1);
  }

  @Test
  @IncludeIn({DB2, SQLSERVER, H2, POSTGRESQL})
  public void merge_with_using_extra_filter() {
    QSurvey usingSubqueryAlias = new QSurvey("USING_SUBSELECT");
    SQLMergeUsingClause merge =
        merge(survey)
            .using(query().from(survey2).select(survey2.id, survey2.name).as(usingSubqueryAlias))
            .on(survey.id.eq(usingSubqueryAlias.id))
            .whenMatched()
            .and(usingSubqueryAlias.id.lt(0))
            .thenDelete();

    assertThat(merge.execute()).isEqualTo(0);
  }

  @Test
  @IncludeIn({DB2, SQLSERVER, H2, POSTGRESQL})
  public void merge_with_using_direct_table_with_alias() {
    SQLMergeUsingClause merge =
        merge(survey).using(employee).on(survey.id.eq(employee.id)).whenMatched().thenDelete();

    assertThat(merge.execute()).isEqualTo(1);
  }

  @Test
  @IncludeIn({DB2, SQLSERVER, H2, POSTGRESQL})
  public void merge_with_using_direct_table_no_alias() {
    SQLMergeUsingClause merge =
        merge(survey)
            .using(QEmployee.employee)
            .on(survey.id.eq(QEmployee.employee.id))
            .whenMatched()
            .thenDelete();

    assertThat(merge.execute()).isEqualTo(1);
  }

  @Test
  @IncludeIn({H2})
  public void merge_with_using_direct_table_with_schema() {
    SQLMergeUsingClause merge =
        merge(survey).using(employee).on(survey.id.eq(employee.id)).whenMatched().thenDelete();

    // If running with schema, need to have schema also in ON statement
    if (merge.toString().contains("PUBLIC.SURVEY")) {
      assertThat(merge.toString()).contains("on PUBLIC.SURVEY.ID = e.ID");
    }
  }
}
