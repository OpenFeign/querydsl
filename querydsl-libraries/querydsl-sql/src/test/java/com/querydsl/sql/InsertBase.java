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
import static com.querydsl.core.Target.FIREBIRD;
import static com.querydsl.core.Target.H2;
import static com.querydsl.core.Target.HSQLDB;
import static com.querydsl.core.Target.MYSQL;
import static com.querydsl.core.Target.ORACLE;
import static com.querydsl.core.Target.POSTGRESQL;
import static com.querydsl.core.Target.SQLITE;
import static com.querydsl.core.Target.SQLSERVER;
import static com.querydsl.sql.Constants.survey;
import static com.querydsl.sql.Constants.survey2;
import static com.querydsl.sql.SQLExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.QueryException;
import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.Mapper;
import com.querydsl.sql.dml.SQLInsertClause;
import com.querydsl.sql.domain.Employee;
import com.querydsl.sql.domain.QDateTest;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QSurvey;
import com.querydsl.sql.domain.QUuids;
import com.querydsl.sql.domain.QXmlTest;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public abstract class InsertBase extends AbstractBaseTest {

  private void reset() throws SQLException {
    delete(survey).execute();
    insert(survey).values(1, "Hello World", "Hello").execute();

    delete(QDateTest.qDateTest).execute();
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
  @ExcludeIn({
    CUBRID, SQLITE, DERBY, ORACLE
  }) // https://bitbucket.org/xerial/sqlite-jdbc/issue/133/prepstmtsetdate-int-date-calendar-seems
  public void insert_dates() {
    var dateTest = QDateTest.qDateTest;
    var localDate = LocalDate.of(1978, 1, 2);

    Path<LocalDate> localDateProperty = ExpressionUtils.path(LocalDate.class, "DATE_TEST");
    Path<LocalDateTime> dateTimeProperty = ExpressionUtils.path(LocalDateTime.class, "DATE_TEST");
    var insert = insert(dateTest);
    insert.set(localDateProperty, localDate);
    insert.execute();

    var result =
        query()
            .from(dateTest)
            .select(
                dateTest.dateTest.year(),
                dateTest.dateTest.month(),
                dateTest.dateTest.dayOfMonth(),
                dateTimeProperty)
            .fetchFirst();
    assertThat(result.get(0, Integer.class)).isEqualTo(Integer.valueOf(1978));
    assertThat(result.get(1, Integer.class)).isEqualTo(Integer.valueOf(1));
    assertThat(result.get(2, Integer.class)).isEqualTo(Integer.valueOf(2));

    LocalDateTime dateTime = result.get(dateTimeProperty);
    if (target == CUBRID) {
      // XXX Cubrid adds random milliseconds for some reason
      dateTime = dateTime.withNano(0);
    }
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
        select(survey.id, emp2.firstname)
            .from(survey)
            .innerJoin(emp1)
            .on(survey.id.eq(emp1.id))
            .innerJoin(emp2)
            .on(emp1.superiorId.eq(emp2.superiorId), emp1.firstname.eq(emp2.firstname)));

    assertThat(insert.execute()).isEqualTo(0);
  }

  @Test
  public void insert_alternative_syntax() {
    // with columns
    assertThat(insert(survey).set(survey.id, 3).set(survey.name, "Hello").execute()).isEqualTo(1);
  }

  @Test
  public void insert_batch() {
    var insert = insert(survey).set(survey.id, 5).set(survey.name, "55").addBatch();

    assertThat(insert.getBatchCount()).isEqualTo(1);

    insert.set(survey.id, 6).set(survey.name, "66").addBatch();

    assertThat(insert.getBatchCount()).isEqualTo(2);
    assertThat(insert.execute()).isEqualTo(2);

    assertThat(query().from(survey).where(survey.name.eq("55")).fetchCount()).isEqualTo(1L);
    assertThat(query().from(survey).where(survey.name.eq("66")).fetchCount()).isEqualTo(1L);
  }

  @Test
  public void insert_batch_to_bulk() {
    var insert = insert(survey);
    insert.setBatchToBulk(true);

    insert.set(survey.id, 5).set(survey.name, "55").addBatch();

    assertThat(insert.getBatchCount()).isEqualTo(1);

    insert.set(survey.id, 6).set(survey.name, "66").addBatch();

    assertThat(insert.getBatchCount()).isEqualTo(2);
    assertThat(insert.execute()).isEqualTo(2);

    assertThat(query().from(survey).where(survey.name.eq("55")).fetchCount()).isEqualTo(1L);
    assertThat(query().from(survey).where(survey.name.eq("66")).fetchCount()).isEqualTo(1L);
  }

  @Test
  public void insert_batch_Templates() {
    var insert =
        insert(survey)
            .set(survey.id, 5)
            .set(survey.name, Expressions.stringTemplate("'55'"))
            .addBatch();

    insert.set(survey.id, 6).set(survey.name, Expressions.stringTemplate("'66'")).addBatch();

    assertThat(insert.execute()).isEqualTo(2);

    assertThat(query().from(survey).where(survey.name.eq("55")).fetchCount()).isEqualTo(1L);
    assertThat(query().from(survey).where(survey.name.eq("66")).fetchCount()).isEqualTo(1L);
  }

  @Test
  public void insert_batch2() {
    var insert = insert(survey).set(survey.id, 5).set(survey.name, "55").addBatch();

    insert.set(survey.id, 6).setNull(survey.name).addBatch();

    assertThat(insert.execute()).isEqualTo(2);
  }

  @Test
  public void insert_null_with_columns() {
    assertThat(insert(survey).columns(survey.id, survey.name).values(3, null).execute())
        .isEqualTo(1);
  }

  @Test
  @ExcludeIn({DB2, DERBY})
  public void insert_null_without_columns() {
    assertThat(insert(survey).values(4, null, null).execute()).isEqualTo(1);
  }

  @Test
  @ExcludeIn({FIREBIRD, HSQLDB, DB2, DERBY, ORACLE})
  public void insert_without_values() {
    assertThat(insert(survey).execute()).isEqualTo(1);
  }

  @Test
  @ExcludeIn(ORACLE)
  public void insert_nulls_in_batch() {
    //        QFoo f= QFoo.foo;
    //        SQLInsertClause sic = new SQLInsertClause(c, new H2Templates(), f);
    //        sic.columns(f.c1,f.c2).values(null,null).addBatch();
    //        sic.columns(f.c1,f.c2).values(null,1).addBatch();
    //        sic.execute();
    var sic = insert(survey);
    sic.columns(survey.name, survey.name2).values(null, null).addBatch();
    sic.columns(survey.name, survey.name2).values(null, "X").addBatch();
    assertThat(sic.execute()).isEqualTo(2);
  }

  @Test
  @Ignore
  @ExcludeIn({DERBY})
  public void insert_nulls_in_batch2() {
    Mapper<Object> mapper = DefaultMapper.WITH_NULL_BINDINGS;
    //        QFoo f= QFoo.foo;
    //        SQLInsertClause sic = new SQLInsertClause(c, new H2Templates(), f);
    //        Foo f1=new Foo();
    //        sic.populate(f1).addBatch();
    //        f1=new Foo();
    //        f1.setC1(1);
    //        sic.populate(f1).addBatch();
    //        sic.execute();
    var employee = QEmployee.employee;
    var sic = insert(employee);
    var e = new Employee();
    sic.populate(e, mapper).addBatch();
    e = new Employee();
    e.setFirstname("X");
    sic.populate(e, mapper).addBatch();
    assertThat(sic.execute()).isEqualTo(0);
  }

  @Test
  public void insert_with_columns() {
    assertThat(insert(survey).columns(survey.id, survey.name).values(3, "Hello").execute())
        .isEqualTo(1);
  }

  @Test
  @ExcludeIn({CUBRID, SQLSERVER, SQLITE})
  public void insert_with_keys() throws SQLException {
    var rs = insert(survey).set(survey.name, "Hello World").executeWithKeys();
    assertThat(rs.next()).isTrue();
    assertThat(rs.getObject(1) != null).isTrue();
    rs.close();
  }

  @Test
  @ExcludeIn({CUBRID, SQLSERVER, SQLITE})
  public void insert_with_keys_listener() throws SQLException {
    final var result = new AtomicBoolean();
    SQLListener listener =
        new SQLBaseListener() {
          @Override
          public void end(SQLListenerContext context) {
            result.set(true);
          }
        };
    var clause = insert(survey).set(survey.name, "Hello World");
    clause.addListener(listener);
    var rs = clause.executeWithKeys();
    assertThat(result.get()).isFalse();
    assertThat(rs.next()).isTrue();
    assertThat(rs.getObject(1) != null).isTrue();
    rs.close();
    assertThat(result.get()).isTrue();
  }

  @Test
  @ExcludeIn({CUBRID, SQLSERVER, SQLITE})
  public void insert_with_keys_Projected() throws SQLException {
    assertThat(insert(survey).set(survey.name, "Hello you").executeWithKey(survey.id)).isNotNull();
  }

  @Test
  @ExcludeIn({CUBRID, SQLSERVER, SQLITE})
  public void insert_with_keys_Projected2() throws SQLException {
    Path<Object> idPath = ExpressionUtils.path(Object.class, "id");
    Object id = insert(survey).set(survey.name, "Hello you").executeWithKey(idPath);
    assertThat(id).isNotNull();
  }

  @Test(expected = QueryException.class)
  @IncludeIn({DERBY, HSQLDB})
  public void insert_with_keys_OverriddenColumn() throws SQLException {
    var originalColumnName = ColumnMetadata.getName(survey.id);
    try {
      configuration.registerColumnOverride(
          survey.getSchemaName(), survey.getTableName(), originalColumnName, "wrongColumnName");

      var sqlInsertClause = new SQLInsertClause(connection, configuration, survey);
      sqlInsertClause.addListener(new TestLoggingListener());
      Object id = sqlInsertClause.set(survey.name, "Hello you").executeWithKey(survey.id);
      assertThat(id).isNotNull();
    } finally {
      configuration.registerColumnOverride(
          survey.getSchemaName(), survey.getTableName(), originalColumnName, originalColumnName);
    }
  }

  // http://sourceforge.net/tracker/index.php?func=detail&aid=3513432&group_id=280608&atid=2377440

  @Test
  public void insert_with_set() {
    assertThat(insert(survey).set(survey.id, 5).set(survey.name, (String) null).execute())
        .isEqualTo(1);
  }

  @Test
  @IncludeIn(MYSQL)
  @SkipForQuoted
  public void insert_with_special_options() {
    var clause = insert(survey).columns(survey.id, survey.name).values(3, "Hello");

    clause.addFlag(Position.START_OVERRIDE, "insert ignore into ");

    assertThat(clause).hasToString("insert ignore into SURVEY (ID, NAME) values (?, ?)");
    assertThat(clause.execute()).isEqualTo(1);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_with_subQuery() {
    var count = (int) query().from(survey).fetchCount();
    assertThat(
            insert(survey)
                .columns(survey.id, survey.name)
                .select(query().from(survey2).select(survey2.id.add(20), survey2.name))
                .execute())
        .isEqualTo(count);
  }

  @Test
  @ExcludeIn({DB2, HSQLDB, CUBRID, DERBY, FIREBIRD})
  public void insert_with_subQuery2() {
    //        insert into modules(name)
    //        select 'MyModule'
    //        where not exists
    //        (select 1 from modules where modules.name = 'MyModule')

    assertThat(
            insert(survey)
                .set(
                    survey.name,
                    query()
                        .where(query().from(survey2).where(survey2.name.eq("MyModule")).notExists())
                        .select(Expressions.constant("MyModule"))
                        .fetchFirst())
                .execute())
        .isEqualTo(1);

    assertThat(query().from(survey).where(survey.name.eq("MyModule")).fetchCount()).isEqualTo(1L);
  }

  @Test
  @ExcludeIn({HSQLDB, CUBRID, DERBY})
  public void insert_with_subQuery3() {
    //        insert into modules(name)
    //        select 'MyModule'
    //        where not exists
    //        (select 1 from modules where modules.name = 'MyModule')

    assertThat(
            insert(survey)
                .columns(survey.name)
                .select(
                    query()
                        .where(
                            query().from(survey2).where(survey2.name.eq("MyModule2")).notExists())
                        .select(Expressions.constant("MyModule2")))
                .execute())
        .isEqualTo(1);

    assertThat(query().from(survey).where(survey.name.eq("MyModule2")).fetchCount()).isEqualTo(1L);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_with_subQuery_Params() {
    var param = new Param<Integer>(Integer.class, "param");
    SQLQuery<?> sq = query().from(survey2);
    sq.set(param, 20);

    var count = (int) query().from(survey).fetchCount();
    assertThat(
            insert(survey)
                .columns(survey.id, survey.name)
                .select(sq.select(survey2.id.add(param), survey2.name))
                .execute())
        .isEqualTo(count);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_with_subQuery_Via_Constructor() {
    var count = (int) query().from(survey).fetchCount();
    var insert = insert(survey, query().from(survey2));
    insert.set(survey.id, survey2.id.add(20));
    insert.set(survey.name, survey2.name);
    assertThat(insert.execute()).isEqualTo(count);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_with_subQuery_Without_Columns() {
    var count = (int) query().from(survey).fetchCount();
    assertThat(
            insert(survey)
                .select(
                    query().from(survey2).select(survey2.id.add(10), survey2.name, survey2.name2))
                .execute())
        .isEqualTo(count);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insert_without_columns() {
    assertThat(insert(survey).values(4, "Hello", "World").execute()).isEqualTo(1);
  }

  @Test
  @ExcludeIn(FIREBIRD) // too slow
  public void insertBatch_with_subquery() {
    var insert =
        insert(survey)
            .columns(survey.id, survey.name)
            .select(query().from(survey2).select(survey2.id.add(20), survey2.name))
            .addBatch();

    insert(survey)
        .columns(survey.id, survey.name)
        .select(query().from(survey2).select(survey2.id.add(40), survey2.name))
        .addBatch();

    assertThat(insert.execute()).isEqualTo(1);
  }

  @Test
  public void like() {
    insert(survey).values(11, "Hello World", "a\\b").execute();
    assertThat(query().from(survey).where(survey.name2.contains("a\\b")).fetchCount())
        .isEqualTo(1L);
  }

  @Test
  public void like_with_escape() {
    var insert = insert(survey);
    insert.set(survey.id, 5).set(survey.name, "aaa").addBatch();
    insert.set(survey.id, 6).set(survey.name, "a_").addBatch();
    insert.set(survey.id, 7).set(survey.name, "a%").addBatch();
    assertThat(insert.execute()).isEqualTo(3);

    assertThat(query().from(survey).where(survey.name.like("a|%", '|')).fetchCount()).isEqualTo(1L);
    assertThat(query().from(survey).where(survey.name.like("a|_", '|')).fetchCount()).isEqualTo(1L);
    assertThat(query().from(survey).where(survey.name.like("a%")).fetchCount()).isEqualTo(3L);
    assertThat(query().from(survey).where(survey.name.like("a_")).fetchCount()).isEqualTo(2L);

    assertThat(query().from(survey).where(survey.name.startsWith("a_")).fetchCount()).isEqualTo(1L);
    assertThat(query().from(survey).where(survey.name.startsWith("a%")).fetchCount()).isEqualTo(1L);
  }

  @Test
  @IncludeIn(MYSQL)
  @SkipForQuoted
  public void replace() {
    var clause = mysqlReplace(survey);
    clause.columns(survey.id, survey.name).values(3, "Hello");

    assertThat(clause).hasToString("replace into SURVEY (ID, NAME) values (?, ?)");
    clause.execute();
  }

  @Test
  public void insert_with_tempateExpression_in_batch() {
    assertThat(
            insert(survey)
                .set(survey.id, 3)
                .set(survey.name, Expressions.stringTemplate("'Hello'"))
                .addBatch()
                .execute())
        .isEqualTo(1);
  }

  @Test
  @IncludeIn({H2, POSTGRESQL})
  @SkipForQuoted
  public void uuids() {
    delete(QUuids.uuids).execute();
    var uuids = QUuids.uuids;
    var uuid = UUID.randomUUID();
    insert(uuids).set(uuids.field, uuid).execute();
    assertThat(query().from(uuids).select(uuids.field).fetchFirst()).isEqualTo(uuid);
  }

  @Test
  @ExcludeIn({ORACLE})
  public void xml() {
    delete(QXmlTest.xmlTest).execute();
    var xmlTest = QXmlTest.xmlTest;
    var contents = "<html><head>a</head><body>b</body></html>";
    insert(xmlTest).set(xmlTest.col, contents).execute();
    assertThat(query().from(xmlTest).select(xmlTest.col).fetchFirst()).isEqualTo(contents);
  }
}
