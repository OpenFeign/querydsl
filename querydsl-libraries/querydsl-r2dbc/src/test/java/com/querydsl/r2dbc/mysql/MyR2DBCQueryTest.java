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

import static org.junit.Assert.assertEquals;

import com.querydsl.r2dbc.MySQLTemplates;
import com.querydsl.r2dbc.domain.QSurvey;
import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class MyR2DBCQueryTest {
  private R2DBCMySQLQuery<?> query;

  private QSurvey survey = new QSurvey("survey");

  @Before
  public void setUp() {
    query =
        new R2DBCMySQLQuery<Void>(null, MySQLTemplates.builder().newLineToSingleSpace().build());
    query.from(survey);
    query.orderBy(survey.name.asc());
    query.getMetadata().setProjection(survey.name);
  }

  @Test
  public void syntax() {
    //        SELECT
    //        [ALL | DISTINCT | DISTINCTROW ]
    //          [HIGH_PRIORITY]
    query.highPriority();
    //          [STRAIGHT_JOIN]
    query.straightJoin();
    //          [SQL_SMALL_RESULT] [SQL_BIG_RESULT] [SQL_BUFFER_RESULT]
    query.smallResult();
    query.bigResult();
    query.bufferResult();
    //          [SQL_CACHE | SQL_NO_CACHE] [SQL_CALC_FOUND_ROWS]
    query.cache();
    query.noCache();
    query.calcFoundRows();
    //        select_expr [, select_expr ...]
    //        [FROM table_references
    query.from(new QSurvey("survey2"));
    //        [WHERE where_condition]
    query.where(survey.id.isNotNull());
    //        [GROUP BY {col_name | expr | position}
    query.groupBy(survey.name);
    //          [ASC | DESC], ... [WITH ROLLUP]]
    query.withRollup();
    //        [HAVING where_condition]
    query.having(survey.name.isNull());
    //        [ORDER BY {col_name | expr | position}
    //          [ASC | DESC], ...]
    query.orderBy(survey.name.asc());
    //        [LIMIT {[offset,] row_count | row_count OFFSET offset}]
    query.limit(2);
    query.offset(3);
    //        [PROCEDURE procedure_name(argument_list)]
    // TODO
    //        [INTO OUTFILE 'file_name' export_options
    //          | INTO DUMPFILE 'file_name'
    //          | INTO var_name [, var_name]]
    //        [FOR UPDATE | LOCK IN SHARE MODE]]
    query.forUpdate();
    query.lockInShareMode();
  }

  @Test
  public void forceIndex() {
    query =
        new R2DBCMySQLQuery<Void>(null, MySQLTemplates.builder().newLineToSingleSpace().build());
    query.from(survey);
    query.forceIndex("col1_index");
    query.orderBy(survey.name.asc());
    query.getMetadata().setProjection(survey.name);

    assertEquals(
        "select survey.NAME from SURVEY survey force index (col1_index) "
            + "order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void ignoreIndex() {
    query =
        new R2DBCMySQLQuery<Void>(null, MySQLTemplates.builder().newLineToSingleSpace().build());
    query.from(survey);
    query.ignoreIndex("col1_index");
    query.orderBy(survey.name.asc());
    query.getMetadata().setProjection(survey.name);

    assertEquals(
        "select survey.NAME from SURVEY survey ignore index (col1_index) "
            + "order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void useIndex() {
    query =
        new R2DBCMySQLQuery<Void>(null, MySQLTemplates.builder().newLineToSingleSpace().build());
    query.from(survey);
    query.useIndex("col1_index");
    query.orderBy(survey.name.asc());
    query.getMetadata().setProjection(survey.name);

    assertEquals(
        "select survey.NAME from SURVEY survey use index (col1_index) "
            + "order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void useIndex2() {
    query =
        new R2DBCMySQLQuery<Void>(null, MySQLTemplates.builder().newLineToSingleSpace().build());
    query.from(survey);
    query.useIndex("col1_index", "col2_index");
    query.orderBy(survey.name.asc());
    query.getMetadata().setProjection(survey.name);

    assertEquals(
        "select survey.NAME from SURVEY survey use index (col1_index, col2_index) "
            + "order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void highPriority() {
    query.highPriority();
    assertEquals(
        "select high_priority survey.NAME from SURVEY survey order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void straightJoin() {
    query.straightJoin();
    assertEquals(
        "select straight_join survey.NAME from SURVEY survey order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void smallResult() {
    query.smallResult();
    assertEquals(
        "select sql_small_result survey.NAME from SURVEY survey order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void bigResult() {
    query.bigResult();
    assertEquals(
        "select sql_big_result survey.NAME from SURVEY survey order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void bufferResult() {
    query.bufferResult();
    assertEquals(
        "select sql_buffer_result survey.NAME from SURVEY survey order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void cache() {
    query.cache();
    assertEquals(
        "select sql_cache survey.NAME from SURVEY survey order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void noCache() {
    query.noCache();
    assertEquals(
        "select sql_no_cache survey.NAME from SURVEY survey order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void calcFoundRows() {
    query.calcFoundRows();
    assertEquals(
        "select sql_calc_found_rows survey.NAME from SURVEY survey order by survey.NAME asc",
        toString(query));
  }

  @Test
  public void withRollup() {
    query.groupBy(survey.name);
    query.withRollup();
    assertEquals(
        "select survey.NAME from SURVEY survey group by survey.NAME with rollup  order by"
            + " survey.NAME asc",
        toString(query));
  }

  @Test
  public void forUpdate() {
    query.forUpdate();
    assertEquals(
        "select survey.NAME from SURVEY survey order by survey.NAME asc for update",
        toString(query));
  }

  @Test
  public void forUpdate_with_limit() {
    query.forUpdate();
    query.limit(2);
    assertEquals(
        "select survey.NAME from SURVEY survey order by survey.NAME asc limit ? for update",
        toString(query));
  }

  @Test
  public void intoOutfile() {
    query.intoOutfile(new File("target/out"));
    assertEquals(
        "select survey.NAME from SURVEY survey "
            + "order by survey.NAME asc into outfile 'target"
            + File.separator
            + "out'",
        toString(query));
  }

  @Test
  public void intoDumpfile() {
    query.intoDumpfile(new File("target/out"));
    assertEquals(
        "select survey.NAME from SURVEY survey "
            + "order by survey.NAME asc into dumpfile 'target"
            + File.separator
            + "out'",
        toString(query));
  }

  @Test
  public void intoString() {
    query.into("var1");
    assertEquals(
        "select survey.NAME from SURVEY survey " + "order by survey.NAME asc into var1",
        toString(query));
  }

  @Test
  public void lockInShareMode() {
    query.lockInShareMode();
    assertEquals(
        "select survey.NAME from SURVEY survey " + "order by survey.NAME asc lock in share mode",
        toString(query));
  }

  private String toString(R2DBCMySQLQuery<?> query) {
    return query.toString().replace('\n', ' ');
  }
}
