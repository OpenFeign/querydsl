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
import static com.querydsl.core.Target.FIREBIRD;
import static com.querydsl.core.Target.H2;
import static com.querydsl.core.Target.HSQLDB;
import static com.querydsl.core.Target.MYSQL;
import static com.querydsl.core.Target.ORACLE;
import static com.querydsl.core.Target.POSTGRESQL;
import static com.querydsl.core.Target.SQLITE;
import static com.querydsl.core.Target.SQLSERVER;
import static com.querydsl.r2dbc.Constants.survey;
import static com.querydsl.r2dbc.Constants.survey2;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.QueryException;
import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.r2dbc.dml.R2DBCInsertClause;
import com.querydsl.r2dbc.domain.QDateTest;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.r2dbc.domain.QSurvey;
import com.querydsl.r2dbc.domain.QUuids;
import com.querydsl.r2dbc.domain.QXmlTest;
import com.querydsl.sql.ColumnMetadata;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class InsertBase extends AbstractBaseTest {

  private void reset() {
    delete(survey).execute().block();
    insert(survey).values(1, "Hello World", "Hello").execute().block();

    delete(QDateTest.qDateTest).execute().block();
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
  @ExcludeIn({CUBRID, SQLITE})
  // https://bitbucket.org/xerial/sqlite-jdbc/issue/133/prepstmtsetdate-int-date-calendar-seems
  public void insert_dates() {
    var dateTest = QDateTest.qDateTest;
    var localDate = LocalDate.of(1978, 1, 2);

    Path<LocalDate> localDateProperty = ExpressionUtils.path(LocalDate.class, "DATE_TEST");
    Path<LocalDateTime> dateTimeProperty = ExpressionUtils.path(LocalDateTime.class, "DATE_TEST");
    var insert = insert(dateTest);
    insert.set(localDateProperty, localDate);
    insert.execute().block();

    var result =
        query()
            .from(dateTest)
            .select(
                dateTest.dateTest.year(),
                dateTest.dateTest.month(),
                dateTest.dateTest.dayOfMonth(),
                dateTimeProperty)
            .fetchFirst()
            .block();
    assertThat(result.get(0, Integer.class)).isEqualTo(Integer.valueOf(1978));
    assertThat(result.get(1, Integer.class)).isEqualTo(Integer.valueOf(1));
    assertThat(result.get(2, Integer.class)).isEqualTo(Integer.valueOf(2));

    LocalDateTime dateTime = result.get(dateTimeProperty);
    assertThat(dateTime).isEqualTo(localDate.atStartOfDay());
  }

  @Test
  public void complex1() {
    // related to #584795
    var survey = new QSurvey("survey");
    var emp1 = new QEmployee("emp1");
    var emp2 = new QEmployee("emp2");
    var insert = insert(survey);
    insert.columns(survey.id, survey.name);
    insert.select(
        R2DBCExpressions.select(survey.id, emp2.firstname)
            .from(survey)
            .innerJoin(emp1)
            .on(survey.id.eq(emp1.id))
            .innerJoin(emp2)
            .on(emp1.superiorId.eq(emp2.superiorId), emp1.firstname.eq(emp2.firstname)));

    assertThat((long) insert.execute().block()).isEqualTo(0);
  }

  @Test
  public void insert_alternative_syntax() {
    // with columns
    assertThat((long) insert(survey).set(survey.id, 3).set(survey.name, "Hello").execute().block())
        .isEqualTo(1);
  }

  @Test
  public void insert_null_with_columns() {
    assertThat(
            (long) insert(survey).columns(survey.id, survey.name).values(3, null).execute().block())
        .isEqualTo(1);
  }

  @Test
  @ExcludeIn({DB2, DERBY})
  public void insert_null_without_columns() {
    assertThat((long) insert(survey).values(4, null, null).execute().block()).isEqualTo(1);
  }

  @Test
  @ExcludeIn({FIREBIRD, HSQLDB, DB2, DERBY, ORACLE})
  public void insert_without_values() {
    assertThat((long) insert(survey).execute().block()).isEqualTo(1);
  }

  @Test
  public void insert_with_columns() {
    assertThat(
            (long)
                insert(survey).columns(survey.id, survey.name).values(3, "Hello").execute().block())
        .isEqualTo(1);
  }

  @Test
  @ExcludeIn({CUBRID, SQLSERVER})
  public void insert_with_keys() {
    var key = insert(survey).set(survey.name, "Hello World").executeWithKey(survey.id).block();
    assertThat(key != null).isTrue();
  }

  @Test
  @ExcludeIn({CUBRID, SQLSERVER})
  public void insert_with_keys_listener() {
    var clause = insert(survey).set(survey.name, "Hello World");
    var key = clause.executeWithKey(survey.id).block();
    assertThat(key != null).isTrue();
  }

  @Test
  @ExcludeIn({CUBRID, SQLSERVER})
  public void insert_with_keys_Projected() {
    assertThat(insert(survey).set(survey.name, "Hello you").executeWithKey(survey.id)).isNotNull();
  }

  @Test
  @ExcludeIn({CUBRID, SQLSERVER})
  public void insert_with_keys_Projected2() {
    Path<Object> idPath = ExpressionUtils.path(Object.class, "id");
    Object id = insert(survey).set(survey.name, "Hello you").executeWithKey(idPath);
    assertThat(id).isNotNull();
  }

  @Test(expected = QueryException.class)
  @IncludeIn({DERBY, HSQLDB})
  public void insert_with_keys_OverriddenColumn() {
    var originalColumnName = ColumnMetadata.getName(survey.id);
    try {
      configuration.registerColumnOverride(
          survey.getSchemaName(), survey.getTableName(), originalColumnName, "wrongColumnName");

      var insert = new R2DBCInsertClause(connection, configuration, survey);
      Object id = insert.set(survey.name, "Hello you").executeWithKey(survey.id);
      assertThat(id).isNotNull();
    } finally {
      configuration.registerColumnOverride(
          survey.getSchemaName(), survey.getTableName(), originalColumnName, originalColumnName);
    }
  }

  // http://sourceforge.net/tracker/index.php?func=detail&aid=3513432&group_id=280608&atid=2377440

  @Test
  public void insert_with_set() {
    assertThat(
            (long)
                insert(survey).set(survey.id, 5).set(survey.name, (String) null).execute().block())
        .isEqualTo(1);
  }

  @Test
  @IncludeIn(MYSQL)
  @SkipForQuoted
  public void insert_with_special_options() {
    var clause = insert(survey).columns(survey.id, survey.name).values(3, "Hello");

    clause.addFlag(Position.START_OVERRIDE, "insert ignore into ");

    assertThat(clause).hasToString("insert ignore into SURVEY (ID, NAME) values (?, ?)");
    assertThat((long) clause.execute().block()).isEqualTo(1);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_with_subQuery() {
    var count = query().from(survey).fetchCount().block();
    assertThat(
            insert(survey)
                .columns(survey.id, survey.name)
                .select(query().from(survey2).select(survey2.id.add(20), survey2.name))
                .execute()
                .block())
        .isEqualTo(count);
  }

  @Test
  @ExcludeIn({DB2, HSQLDB, CUBRID, DERBY, FIREBIRD})
  public void insert_with_subQuery2() {
    //        insert into modules(name)
    //        select 'MyModule'
    //        where not exists
    //        (select 1 from modules where modules.name = 'MyModule')

    R2DBCQuery<String> select =
        query()
            .where(query().from(survey2).where(survey2.name.eq("MyModule")).notExists())
            .select(Expressions.constant("MyModule"));
    assertThat(
            (long) insert(survey).set(survey.name, select.fetchFirst().block()).execute().block())
        .isEqualTo(1);

    assertThat((long) query().from(survey).where(survey.name.eq("MyModule")).fetchCount().block())
        .isEqualTo(1L);
  }

  @Test
  @ExcludeIn({HSQLDB, CUBRID, DERBY})
  public void insert_with_subQuery3() {
    //        insert into modules(name)
    //        select 'MyModule'
    //        where not exists
    //        (select 1 from modules where modules.name = 'MyModule')

    assertThat(
            (long)
                insert(survey)
                    .columns(survey.name)
                    .select(
                        query()
                            .where(
                                query()
                                    .from(survey2)
                                    .where(survey2.name.eq("MyModule2"))
                                    .notExists())
                            .select(Expressions.constant("MyModule2")))
                    .execute()
                    .block())
        .isEqualTo(1);

    assertThat((long) query().from(survey).where(survey.name.eq("MyModule2")).fetchCount().block())
        .isEqualTo(1L);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_with_subQuery_Params() {
    var param = new Param<Integer>(Integer.class, "param");
    R2DBCQuery<?> sq = query().from(survey2);
    sq.set(param, 20);

    long count = query().from(survey).fetchCount().block();
    assertThat(
            (long)
                insert(survey)
                    .columns(survey.id, survey.name)
                    .select(sq.select(survey2.id.add(param), survey2.name))
                    .execute()
                    .block())
        .isEqualTo(count);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_with_subQuery_Via_Constructor() {
    long count = query().from(survey).fetchCount().block();
    var insert = insert(survey, query().from(survey2));
    insert.set(survey.id, survey2.id.add(20));
    insert.set(survey.name, survey2.name);
    assertThat((long) insert.execute().block()).isEqualTo(count);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_with_subQuery_Without_Columns() {
    long count = query().from(survey).fetchCount().block();
    assertThat(
            (long)
                insert(survey)
                    .select(
                        query()
                            .from(survey2)
                            .select(survey2.id.add(10), survey2.name, survey2.name2))
                    .execute()
                    .block())
        .isEqualTo(count);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_without_columns() {
    assertThat((long) insert(survey).values(4, "Hello", "World").execute().block()).isEqualTo(1);
  }

  @Test
  public void like() {
    insert(survey).values(11, "Hello World", "a\\b").execute().block();
    assertThat(
            (long) query().from(survey).where(survey.name2.contains("a\\b")).fetchCount().block())
        .isEqualTo(1L);
  }

  @Test
  @IncludeIn({H2, POSTGRESQL})
  @SkipForQuoted
  public void uuids() {
    delete(QUuids.uuids).execute().block();
    var uuids = QUuids.uuids;
    var uuid = UUID.randomUUID();
    insert(uuids).set(uuids.field, uuid).execute().block();
    assertThat(query().from(uuids).select(uuids.field).fetchFirst().block()).isEqualTo(uuid);
  }

  @Test
  @ExcludeIn({ORACLE, SQLSERVER, POSTGRESQL})
  // TODO when r2dbc-mssql fixes this, remove SQLSERVER
  // TODO when r2dbc-postgre fixes this, remove POSTGRESQL
  public void xml() {
    delete(QXmlTest.xmlTest).execute().block();
    var xmlTest = QXmlTest.xmlTest;
    var contents = "<html><head>a</head><body>b</body></html>";
    insert(xmlTest).set(xmlTest.col, contents).execute().block();
    assertThat(query().from(xmlTest).select(xmlTest.col).fetchFirst().block()).isEqualTo(contents);
  }
}
