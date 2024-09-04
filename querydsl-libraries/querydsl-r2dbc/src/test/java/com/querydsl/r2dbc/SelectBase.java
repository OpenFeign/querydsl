// CHECKSTYLERULE:OFF: FileLength
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
import static com.querydsl.core.Target.TERADATA;
import static com.querydsl.r2dbc.Constants.date;
import static com.querydsl.r2dbc.Constants.employee;
import static com.querydsl.r2dbc.Constants.employee2;
import static com.querydsl.r2dbc.Constants.survey;
import static com.querydsl.r2dbc.Constants.survey2;
import static com.querydsl.r2dbc.Constants.time;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Maps;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.Pair;
import com.querydsl.core.QueryException;
import com.querydsl.core.QuerydslModule;
import com.querydsl.core.ReactiveQueryExecution;
import com.querydsl.core.Target;
import com.querydsl.core.Tuple;
import com.querydsl.core.dml.reactive.ReactiveFetchable;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.core.testutil.IncludeIn;
import com.querydsl.core.testutil.Serialization;
import com.querydsl.core.types.ArrayConstructorExpression;
import com.querydsl.core.types.Concatenation;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.MappingProjection;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.r2dbc.domain.Employee;
import com.querydsl.r2dbc.domain.IdName;
import com.querydsl.r2dbc.domain.QEmployee;
import com.querydsl.r2dbc.domain.QEmployeeNoPK;
import com.querydsl.r2dbc.domain.QIdName;
import com.querydsl.r2dbc.domain.QNumberTest;
import com.querydsl.sql.Beans;
import com.querydsl.sql.DatePart;
import com.querydsl.sql.QBeans;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.StatementOptions;
import com.querydsl.sql.WithinGroup;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.junit.Ignore;
import org.junit.Test;

public abstract class SelectBase extends AbstractBaseTest {

  private static final Expression<?>[] NO_EXPRESSIONS = new Expression[0];

  private final ReactiveQueryExecution standardTest =
      new ReactiveQueryExecution(QuerydslModule.SQL, Connections.getTarget()) {
        @Override
        protected ReactiveFetchable<?> createQuery() {
          return testQuery().from(employee, employee2);
        }

        @Override
        protected ReactiveFetchable<?> createQuery(Predicate filter) {
          return testQuery().from(employee, employee2).where(filter).select(employee.firstname);
        }
      };

  private <T> T firstResult(Expression<T> expr) {
    return query().select(expr).fetchFirst().block();
  }

  private Tuple firstResult(Expression<?>... exprs) {
    return query().select(exprs).fetchFirst().block();
  }

  @Test
  public void aggregate_list() {
    int min = 30000, avg = 65000, max = 160000;
    // fetch
    assertEquals(
        min,
        query()
            .from(employee)
            .select(employee.salary.min())
            .fetch()
            .collectList()
            .block()
            .get(0)
            .intValue());
    assertEquals(
        avg,
        query()
            .from(employee)
            .select(employee.salary.avg())
            .fetch()
            .collectList()
            .block()
            .get(0)
            .intValue());
    assertEquals(
        max,
        query()
            .from(employee)
            .select(employee.salary.max())
            .fetch()
            .collectList()
            .block()
            .get(0)
            .intValue());
  }

  @Test
  public void aggregate_uniqueResult() {
    int min = 30000, avg = 65000, max = 160000;
    // fetchOne
    assertEquals(
        min, query().from(employee).select(employee.salary.min()).fetchOne().block().intValue());
    assertEquals(
        avg, query().from(employee).select(employee.salary.avg()).fetchOne().block().intValue());
    assertEquals(
        max, query().from(employee).select(employee.salary.max()).fetchOne().block().intValue());
  }

  @Test
  @ExcludeIn(ORACLE)
  @SkipForQuoted
  public void alias() {
    expectedQuery = "select e.ID as id from EMPLOYEE e";
    query().from().select(employee.id.as(employee.id)).from(employee).fetch().collectList().block();
  }

  @Test
  @ExcludeIn({MYSQL, ORACLE})
  @SkipForQuoted
  public void alias_quotes() {
    expectedQuery = "select e.FIRSTNAME as \"First Name\" from EMPLOYEE e";
    query()
        .from(employee)
        .select(employee.firstname.as("First Name"))
        .fetch()
        .collectList()
        .block();
  }

  @Test
  @IncludeIn(MYSQL)
  @SkipForQuoted
  public void alias_quotes_MySQL() {
    expectedQuery = "select e.FIRSTNAME as `First Name` from EMPLOYEE e";
    query()
        .from(employee)
        .select(employee.firstname.as("First Name"))
        .fetch()
        .collectList()
        .block();
  }

  @Test
  @IncludeIn(ORACLE)
  @SkipForQuoted
  public void alias_quotes_Oracle() {
    expectedQuery = "select e.FIRSTNAME \"First Name\" from EMPLOYEE e";
    query().from(employee).select(employee.firstname.as("First Name"));
  }

  @Test
  public void all() {
    for (Expression<?> expr : survey.all()) {
      Path<?> path = (Path<?>) expr;
      assertEquals(survey, path.getMetadata().getParent());
    }
  }

  private void arithmeticTests(
      NumberExpression<Integer> one,
      NumberExpression<Integer> two,
      NumberExpression<Integer> three,
      NumberExpression<Integer> four) {
    assertEquals(1, firstResult(one).intValue());
    assertEquals(2, firstResult(two).intValue());
    assertEquals(4, firstResult(four).intValue());

    assertEquals(3, firstResult(one.subtract(two).add(four)).intValue());
    assertEquals(-5, firstResult(one.subtract(two.add(four))).intValue());
    assertEquals(-1, firstResult(one.add(two).subtract(four)).intValue());
    assertEquals(-1, firstResult(one.add(two.subtract(four))).intValue());

    assertEquals(12, firstResult(one.add(two).multiply(four)).intValue());
    assertEquals(2, firstResult(four.multiply(one).divide(two)).intValue());
    assertEquals(6, firstResult(four.divide(two).multiply(three)).intValue());
    assertEquals(1, firstResult(four.divide(two.multiply(two))).intValue());
  }

  @Test
  public void arithmetic() {
    NumberExpression<Integer> one = Expressions.numberTemplate(Integer.class, "(1.0)");
    NumberExpression<Integer> two = Expressions.numberTemplate(Integer.class, "(2.0)");
    NumberExpression<Integer> three = Expressions.numberTemplate(Integer.class, "(3.0)");
    NumberExpression<Integer> four = Expressions.numberTemplate(Integer.class, "(4.0)");
    arithmeticTests(one, two, three, four);
    // the following one doesn't work with integer arguments
    assertEquals(2, firstResult(four.multiply(one.divide(two))).intValue());
  }

  @Test
  public void arithmetic2() {
    var one = Expressions.ONE;
    var two = Expressions.TWO;
    var three = Expressions.THREE;
    var four = Expressions.FOUR;
    arithmeticTests(one, two, three, four);
  }

  @Test
  public void arithmetic_mod() {
    NumberExpression<Integer> one = Expressions.numberTemplate(Integer.class, "(1)");
    NumberExpression<Integer> two = Expressions.numberTemplate(Integer.class, "(2)");
    NumberExpression<Integer> three = Expressions.numberTemplate(Integer.class, "(3)");
    NumberExpression<Integer> four = Expressions.numberTemplate(Integer.class, "(4)");

    assertEquals(4, firstResult(four.mod(three).add(three)).intValue());
    assertEquals(1, firstResult(four.mod(two.add(one))).intValue());
    assertEquals(0, firstResult(four.mod(two.multiply(one))).intValue());
    assertEquals(2, firstResult(four.add(one).mod(three)).intValue());
  }

  @Test
  @IncludeIn(POSTGRESQL) // TODO generalize array literal projections
  public void array() {
    Expression<Integer[]> expr = Expressions.template(Integer[].class, "'{1,2,3}'::int[]");
    var result = firstResult(expr);
    assertEquals(3, result.length);
    assertEquals(1, result[0].intValue());
    assertEquals(2, result[1].intValue());
    assertEquals(3, result[2].intValue());
  }

  @Test
  @IncludeIn(POSTGRESQL) // TODO generalize array literal projections
  public void array2() {
    Expression<int[]> expr = Expressions.template(int[].class, "'{1,2,3}'::int[]");
    var result = firstResult(expr);
    assertEquals(3, result.length);
    assertEquals(1, result[0]);
    assertEquals(2, result[1]);
    assertEquals(3, result[2]);
  }

  @Test
  @ExcludeIn({DERBY, HSQLDB})
  @Ignore("currently not supported by drivers")
  public void array_null() {
    Expression<Integer[]> expr = Expressions.template(Integer[].class, "null");
    assertThat(firstResult(expr)).isNull();
  }

  @Test
  public void array_projection() {
    var results =
        query()
            .from(employee)
            .select(new ArrayConstructorExpression<>(String[].class, employee.firstname))
            .fetch()
            .collectList()
            .block();
    assertThat(results).isNotEmpty();
    for (String[] result : results) {
      assertThat(result[0]).isNotNull();
    }
  }

  @Test
  public void beans() {
    var rows =
        query()
            .from(employee, employee2)
            .select(new QBeans(employee, employee2))
            .fetch()
            .collectList()
            .block();
    assertThat(rows).isNotEmpty();
    for (Beans row : rows) {
      assertEquals(Employee.class, row.get(employee).getClass());
      assertEquals(Employee.class, row.get(employee2).getClass());
    }
  }

  @Test
  public void between() {
    // 11-13
    assertEquals(
        Arrays.asList(11, 12, 13),
        query()
            .from(employee)
            .where(employee.id.between(11, 13))
            .orderBy(employee.id.asc())
            .select(employee.id)
            .fetch()
            .collectList()
            .block());
  }

  @Test
  @ExcludeIn({ORACLE, CUBRID, FIREBIRD, DB2, DERBY, SQLSERVER, SQLITE, TERADATA})
  public void boolean_all() {
    assertThat(
            query()
                .from(employee)
                .select(R2DBCExpressions.all(employee.firstname.isNotNull()))
                .fetchOne()
                .block())
        .isTrue();
  }

  @Test
  @ExcludeIn({ORACLE, CUBRID, FIREBIRD, DB2, DERBY, SQLSERVER, SQLITE, TERADATA})
  public void boolean_any() {
    assertThat(
            query()
                .from(employee)
                .select(R2DBCExpressions.any(employee.firstname.isNotNull()))
                .fetchOne()
                .block())
        .isTrue();
  }

  @Test
  public void case_() {
    NumberExpression<Float> numExpression =
        employee.salary.floatValue().divide(employee2.salary.floatValue()).multiply(100.1);
    var numExpression2 = employee.id.when(0).then(0.0F).otherwise(numExpression);
    assertEquals(
        Arrays.asList(87, 90, 88, 87, 83, 80, 75),
        query()
            .from(employee, employee2)
            .where(employee.id.eq(employee2.id.add(1)))
            .orderBy(employee.id.asc(), employee2.id.asc())
            .select(numExpression2.floor().intValue())
            .fetch()
            .collectList()
            .block());
  }

  @Test
  public void casts() {
    NumberExpression<?> num = employee.id;
    List<Expression<?>> exprs = new ArrayList<>();

    add(exprs, num.byteValue(), MYSQL);
    add(exprs, num.doubleValue());
    add(exprs, num.floatValue());
    add(exprs, num.intValue());
    add(exprs, num.longValue(), MYSQL);
    add(exprs, num.shortValue(), MYSQL);
    add(exprs, num.stringValue(), DERBY);

    for (Expression<?> expr : exprs) {
      for (Object o : query().from(employee).select(expr).fetch().collectList().block()) {
        assertEquals(expr.getType(), o.getClass());
      }
    }
  }

  @Test
  public void coalesce() {
    var c = new Coalesce<String>(employee.firstname, employee.lastname).add("xxx");
    assertEquals(
        Arrays.asList(),
        query()
            .from(employee)
            .where(c.getValue().eq("xxx"))
            .select(employee.id)
            .fetch()
            .collectList()
            .block());
  }

  @Test
  public void compact_join() {
    // verbose
    assertEquals(
        8,
        query()
            .from(employee)
            .innerJoin(employee2)
            .on(employee.superiorId.eq(employee2.id))
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());

    // compact
    assertEquals(
        8,
        query()
            .from(employee)
            .innerJoin(employee.superiorIdKey, employee2)
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void complex_boolean() {
    var first = employee.firstname.eq("Mike").and(employee.lastname.eq("Smith"));
    var second = employee.firstname.eq("Joe").and(employee.lastname.eq("Divis"));
    assertEquals(2, (long) query().from(employee).where(first.or(second)).fetchCount().block());

    assertEquals(
        0,
        (long)
            query()
                .from(employee)
                .where(
                    employee.firstname.eq("Mike"),
                    employee.lastname.eq("Smith").or(employee.firstname.eq("Joe")),
                    employee.lastname.eq("Divis"))
                .fetchCount()
                .block());
  }

  @Test
  public void complex_subQuery() {
    // alias for the salary
    NumberPath<BigDecimal> sal = Expressions.numberPath(BigDecimal.class, "sal");
    // alias for the subquery
    var sq = new PathBuilder<BigDecimal>(BigDecimal.class, "sq");
    // query execution
    query()
        .from(
            query()
                .from(employee)
                .select(employee.salary.add(employee.salary).add(employee.salary).as(sal))
                .as(sq))
        .select(sq.get(sal).avg(), sq.get(sal).min(), sq.get(sal).max())
        .fetch()
        .collectList()
        .block();
  }

  @Test
  public void constructor_projection() {
    for (IdName idAndName :
        query()
            .from(survey)
            .select(new QIdName(survey.id, survey.name))
            .fetch()
            .collectList()
            .block()) {
      assertThat(idAndName).isNotNull();
      assertThat(idAndName.getId()).isNotNull();
      assertThat(idAndName.getName()).isNotNull();
    }
  }

  @Test
  public void constructor_projection2() {
    var projections =
        query()
            .from(employee)
            .select(
                Projections.constructor(
                    SimpleProjection.class, employee.firstname, employee.lastname))
            .fetch()
            .collectList()
            .block();
    assertThat(projections).isNotEmpty();
    for (SimpleProjection projection : projections) {
      assertThat(projection).isNotNull();
    }
  }

  private double cot(double x) {
    return Math.cos(x) / Math.sin(x);
  }

  private double coth(double x) {
    return Math.cosh(x) / Math.sinh(x);
  }

  @Test
  public void count_with_pK() {
    assertEquals(10, (long) query().from(employee).fetchCount().block());
  }

  @Test
  public void count_without_pK() {
    assertEquals(10, (long) query().from(QEmployeeNoPK.employee).fetchCount().block());
  }

  @Test
  public void count2() {
    assertEquals(
        10, query().from(employee).select(employee.count()).fetchFirst().block().intValue());
  }

  @Test
  @SkipForQuoted
  @ExcludeIn(ORACLE)
  public void count_all() {
    expectedQuery = "select count(*) as rc from EMPLOYEE e";
    NumberPath<Long> rowCount = Expressions.numberPath(Long.class, "rc");
    assertEquals(
        10,
        query().from(employee).select(Wildcard.count.as(rowCount)).fetchOne().block().intValue());
  }

  @Test
  @SkipForQuoted
  @IncludeIn(ORACLE)
  public void count_all_Oracle() {
    expectedQuery = "select count(*) rc from EMPLOYEE e";
    NumberPath<Long> rowCount = Expressions.numberPath(Long.class, "rc");
    assertEquals(
        10,
        query().from(employee).select(Wildcard.count.as(rowCount)).fetchOne().block().intValue());
  }

  @Test
  public void count_distinct_with_pK() {
    assertEquals(10, (long) query().from(employee).distinct().fetchCount().block());
  }

  @Test
  public void count_distinct_without_pK() {
    assertEquals(10, (long) query().from(QEmployeeNoPK.employee).distinct().fetchCount().block());
  }

  @Test
  public void count_distinct2() {
    query().from(employee).select(employee.countDistinct()).fetchFirst().block();
  }

  @Test
  public void custom_projection() {
    var tuples =
        query()
            .from(employee)
            .select(new QProjection(employee.firstname, employee.lastname))
            .fetch()
            .collectList()
            .block();
    assertThat(tuples).isNotEmpty();
    for (Projection tuple : tuples) {
      assertThat(tuple.get(employee.firstname)).isNotNull();
      assertThat(tuple.get(employee.lastname)).isNotNull();
      assertThat(tuple.getExpr(employee.firstname)).isNotNull();
      assertThat(tuple.getExpr(employee.lastname)).isNotNull();
    }
  }

  @Test
  // todo readd mysql after the escape sequence support
  @ExcludeIn({CUBRID, DB2, DERBY, HSQLDB, POSTGRESQL, SQLITE, TERADATA, MYSQL})
  public void dates() {
    var javaInstant = java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
    var javaDateTime = java.time.LocalDateTime.ofInstant(javaInstant, java.time.ZoneId.of("Z"));
    var javaDate = javaDateTime.toLocalDate();
    var javaTime = javaDateTime.toLocalTime();
    var ts = ((long) Math.floor(System.currentTimeMillis() / 1000)) * 1000;

    List<Object> data = new ArrayList<>();
    data.add(Constants.date);
    data.add(Constants.time);
    data.add(new java.util.Date(ts));
    //        data.add(new java.util.Date(tsDate));
    //        data.add(new java.util.Date(tsTime));
    data.add(new Timestamp(ts));
    //        data.add(new Timestamp(tsDate));
    data.add(new Date(110, 0, 1));
    //        data.add(new Date(tsDate));
    data.add(new java.sql.Time(0, 0, 0));
    data.add(new java.sql.Time(12, 30, 0));
    data.add(new java.sql.Time(23, 59, 59));
    // data.add(new java.sql.Time(tsTime));
    //        data.add(new DateTime(ts));
    //        data.add(new DateTime(tsDate));
    //        data.add(new DateTime(tsTime));
    //        data.add(new LocalDateTime(ts));
    //        data.add(new LocalDateTime(tsDate));
    //        data.add(new LocalDateTime(2014, 3, 30, 2, 0));
    //        data.add(new LocalDate(2010, 1, 1));
    //        data.add(new LocalDate(ts));
    //        data.add(new LocalDate(tsDate));
    //        data.add(new LocalTime(0, 0, 0));
    //        data.add(new LocalTime(12, 30, 0));
    //        data.add(new LocalTime(23, 59, 59));
    //        data.add(new LocalTime(ts));
    //        data.add(new LocalTime(tsTime));
    // TODO enable this after r2dbc-mssql adds instant codec
    //        data.add(javaInstant);                                      //java.time.Instant
    data.add(javaDateTime); // java.time.LocalDateTime
    data.add(javaDate); // java.time.LocalDate
    data.add(javaTime); // java.time.LocalTime
    data.add(javaDateTime.atOffset(java.time.ZoneOffset.UTC)); // java.time.OffsetDateTime
    data.add(javaTime.atOffset(java.time.ZoneOffset.UTC)); // java.time.OffsetTime
    //    FIXME re-enable this line data.add(javaDateTime.atZone(java.time.ZoneId.of("Z"))); //
    // java.time.ZonedDateTime

    Map<Object, Object> failures = Maps.newIdentityHashMap();
    for (Object dt : data) {
      var dt2 = firstResult(Expressions.constant(dt));
      if (!dt.equals(dt2)) {
        failures.put(dt, dt2);
      }
    }
    if (!failures.isEmpty()) {
      for (Map.Entry<Object, Object> entry : failures.entrySet()) {
        System.out.println(
            entry.getKey().getClass().getName()
                + ": "
                + entry.getKey()
                + " != "
                + entry.getValue());
      }
      fail("", "Failed with " + failures);
    }
  }

  @Test
  @Ignore // FIXME
  @ExcludeIn({CUBRID, DB2, DERBY, HSQLDB, POSTGRESQL, SQLITE, TERADATA})
  public void dates_cST() {
    var tz = TimeZone.getDefault();
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("CST")); // -6:00
      dates();
    } finally {
      TimeZone.setDefault(tz);
    }
  }

  @Test
  @Ignore // FIXME
  @ExcludeIn({CUBRID, DB2, DERBY, HSQLDB, POSTGRESQL, SQLITE, TERADATA})
  public void dates_iOT() {
    var tz = TimeZone.getDefault();
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("IOT")); // +6:00
      dates();
    } finally {
      TimeZone.setDefault(tz);
    }
  }

  @Test
  @ExcludeIn({CUBRID, DB2, DERBY, SQLITE, TERADATA, FIREBIRD})
  public void dates_literals() {
    if (configuration.getUseLiterals()) {
      dates();
    }
  }

  @Test
  @ExcludeIn({SQLITE})
  public void date_add() {
    R2DBCQuery<?> query = query().from(employee);
    var date1 = query.select(employee.datefield).fetchFirst().block();
    var date2 = query.select(R2DBCExpressions.addYears(employee.datefield, 1)).fetchFirst().block();
    var date3 =
        query.select(R2DBCExpressions.addMonths(employee.datefield, 1)).fetchFirst().block();
    var date4 = query.select(R2DBCExpressions.addDays(employee.datefield, 1)).fetchFirst().block();

    assertThat(date2.getTime() > date1.getTime()).isTrue();
    assertThat(date3.getTime() > date1.getTime()).isTrue();
    assertThat(date4.getTime() > date1.getTime()).isTrue();
  }

  @Test
  @ExcludeIn({SQLITE})
  public void date_add_Timestamp() {
    List<Expression<?>> exprs = new ArrayList<>();
    var dt = Expressions.currentTimestamp();

    add(exprs, R2DBCExpressions.addYears(dt, 1));
    add(exprs, R2DBCExpressions.addMonths(dt, 1), ORACLE);
    add(exprs, R2DBCExpressions.addDays(dt, 1));
    add(exprs, R2DBCExpressions.addHours(dt, 1), TERADATA);
    add(exprs, R2DBCExpressions.addMinutes(dt, 1), TERADATA);
    add(exprs, R2DBCExpressions.addSeconds(dt, 1), TERADATA);

    for (Expression<?> expr : exprs) {
      assertThat(firstResult(expr)).isNotNull();
    }
  }

  @Test
  @ExcludeIn({DB2, SQLITE, TERADATA})
  public void date_diff() {
    var employee2 = new QEmployee("employee2");
    R2DBCQuery<?> query = query().from(employee).orderBy(employee.id.asc());
    R2DBCQuery<?> query2 =
        query().from(employee, employee2).orderBy(employee.id.asc(), employee2.id.desc());

    List<DatePart> dps = new ArrayList<>();
    add(dps, DatePart.year);
    add(dps, DatePart.month);
    add(dps, DatePart.week);
    add(dps, DatePart.day);
    add(dps, DatePart.hour, HSQLDB);
    add(dps, DatePart.minute, HSQLDB);
    add(dps, DatePart.second, HSQLDB);

    var localDate = LocalDate.of(1970, 1, 10);
    var date = new Date(localDate.atStartOfDay().getNano());

    for (DatePart dp : dps) {
      int diff1 =
          query
              .select(R2DBCExpressions.datediff(dp, date, employee.datefield))
              .fetchFirst()
              .block();
      int diff2 =
          query
              .select(R2DBCExpressions.datediff(dp, employee.datefield, date))
              .fetchFirst()
              .block();
      int diff3 =
          query2
              .select(R2DBCExpressions.datediff(dp, employee.datefield, employee2.datefield))
              .fetchFirst()
              .block();
      assertEquals(diff1, -diff2);
    }

    var timestamp = new Timestamp(new java.util.Date().getTime());
    for (DatePart dp : dps) {
      query
          .select(R2DBCExpressions.datediff(dp, Expressions.currentTimestamp(), timestamp))
          .fetchOne()
          .block();
    }
  }

  // TODO Date_diff with timestamps
  @Test
  @ExcludeIn({DB2, HSQLDB, SQLITE, TERADATA})
  public void date_diff2() {
    R2DBCQuery<?> query = query().from(employee).orderBy(employee.id.asc());

    var localDate = LocalDate.of(1970, 1, 10);
    var date = Date.valueOf(localDate);

    int years =
        query
            .select(R2DBCExpressions.datediff(DatePart.year, date, employee.datefield))
            .fetchFirst()
            .block();
    int months =
        query
            .select(R2DBCExpressions.datediff(DatePart.month, date, employee.datefield))
            .fetchFirst()
            .block();
    // weeks
    int days =
        query
            .select(R2DBCExpressions.datediff(DatePart.day, date, employee.datefield))
            .fetchFirst()
            .block();
    int hours =
        query
            .select(R2DBCExpressions.datediff(DatePart.hour, date, employee.datefield))
            .fetchFirst()
            .block();
    int minutes =
        query
            .select(R2DBCExpressions.datediff(DatePart.minute, date, employee.datefield))
            .fetchFirst()
            .block();
    int seconds =
        query
            .select(R2DBCExpressions.datediff(DatePart.second, date, employee.datefield))
            .fetchFirst()
            .block();

    assertEquals(949363200, seconds);
    assertEquals(15822720, minutes);
    assertEquals(263712, hours);
    assertEquals(10988, days);
    assertEquals(361, months);
    assertEquals(30, years);
  }

  @Test
  @ExcludeIn({SQLITE, H2}) // FIXME
  public void date_trunc() {
    var expr = DateTimeExpression.currentTimestamp();

    List<DatePart> dps = new ArrayList<>();
    add(dps, DatePart.year);
    add(dps, DatePart.month);
    add(dps, DatePart.week, DERBY, FIREBIRD, SQLSERVER);
    add(dps, DatePart.day);
    add(dps, DatePart.hour);
    add(dps, DatePart.minute);
    add(dps, DatePart.second);

    for (DatePart dp : dps) {
      firstResult(R2DBCExpressions.datetrunc(dp, expr));
    }
  }

  @Test
  @ExcludeIn({SQLITE, TERADATA, DERBY, H2}) // FIXME
  public void date_trunc2() {
    DateTimeExpression<LocalDateTime> expr =
        DateTimeExpression.currentTimestamp(LocalDateTime.class);

    var tuple =
        firstResult(
            expr,
            R2DBCExpressions.datetrunc(DatePart.year, expr),
            R2DBCExpressions.datetrunc(DatePart.month, expr),
            R2DBCExpressions.datetrunc(DatePart.day, expr),
            R2DBCExpressions.datetrunc(DatePart.hour, expr),
            R2DBCExpressions.datetrunc(DatePart.minute, expr),
            R2DBCExpressions.datetrunc(DatePart.second, expr));
    LocalDateTime date = tuple.get(expr);
    LocalDateTime toYear = tuple.get(R2DBCExpressions.datetrunc(DatePart.year, expr));
    LocalDateTime toMonth = tuple.get(R2DBCExpressions.datetrunc(DatePart.month, expr));
    LocalDateTime toDay = tuple.get(R2DBCExpressions.datetrunc(DatePart.day, expr));
    LocalDateTime toHour = tuple.get(R2DBCExpressions.datetrunc(DatePart.hour, expr));
    LocalDateTime toMinute = tuple.get(R2DBCExpressions.datetrunc(DatePart.minute, expr));
    LocalDateTime toSecond = tuple.get(R2DBCExpressions.datetrunc(DatePart.second, expr));

    // year
    assertEquals(date.getYear(), toYear.getYear());
    assertEquals(date.getYear(), toMonth.getYear());
    assertEquals(date.getYear(), toDay.getYear());
    assertEquals(date.getYear(), toHour.getYear());
    assertEquals(date.getYear(), toMinute.getYear());
    assertEquals(date.getYear(), toSecond.getYear());

    // month
    assertEquals(1, toYear.getMonth().getValue());
    assertEquals(date.getMonth(), toMonth.getMonth());
    assertEquals(date.getMonth(), toDay.getMonth());
    assertEquals(date.getMonth(), toHour.getMonth());
    assertEquals(date.getMonth(), toMinute.getMonth());
    assertEquals(date.getMonth(), toSecond.getMonth());

    // day
    assertEquals(1, toYear.getDayOfMonth());
    assertEquals(1, toMonth.getDayOfMonth());
    assertEquals(date.getDayOfMonth(), toDay.getDayOfMonth());
    assertEquals(date.getDayOfMonth(), toHour.getDayOfMonth());
    assertEquals(date.getDayOfMonth(), toMinute.getDayOfMonth());
    assertEquals(date.getDayOfMonth(), toSecond.getDayOfMonth());

    // hour
    assertEquals(0, toYear.getHour());
    assertEquals(0, toMonth.getHour());
    assertEquals(0, toDay.getHour());
    assertEquals(date.getHour(), toHour.getHour());
    assertEquals(date.getHour(), toMinute.getHour());
    assertEquals(date.getHour(), toSecond.getHour());

    // minute
    assertEquals(0, toYear.getMinute());
    assertEquals(0, toMonth.getMinute());
    assertEquals(0, toDay.getMinute());
    assertEquals(0, toHour.getMinute());
    assertEquals(date.getMinute(), toMinute.getMinute());
    assertEquals(date.getMinute(), toSecond.getMinute());

    // second
    assertEquals(0, toYear.getSecond());
    assertEquals(0, toMonth.getSecond());
    assertEquals(0, toDay.getSecond());
    assertEquals(0, toHour.getSecond());
    assertEquals(0, toMinute.getSecond());
    assertEquals(date.getSecond(), toSecond.getSecond());
  }

  @Test
  public void dateTime() {
    R2DBCQuery<?> query = query().from(employee).orderBy(employee.id.asc());
    assertEquals(
        Integer.valueOf(10), query.select(employee.datefield.dayOfMonth()).fetchFirst().block());
    assertEquals(Integer.valueOf(2), query.select(employee.datefield.month()).fetchFirst().block());
    assertEquals(
        Integer.valueOf(2000), query.select(employee.datefield.year()).fetchFirst().block());
    assertEquals(
        Integer.valueOf(200002), query.select(employee.datefield.yearMonth()).fetchFirst().block());
  }

  @Test
  public void dateTime_to_date() {
    firstResult(R2DBCExpressions.date(DateTimeExpression.currentTimestamp()));
  }

  private double degrees(double x) {
    return x * 180.0 / Math.PI;
  }

  @Test
  public void distinct_count() {
    long count1 = query().from(employee).distinct().fetchCount().block();
    long count2 = query().from(employee).distinct().fetchCount().block();
    assertEquals(count1, count2);
  }

  @Test
  public void distinct_list() {
    var lengths1 =
        query()
            .from(employee)
            .distinct()
            .select(employee.firstname.length())
            .fetch()
            .collectList()
            .block();
    var lengths2 =
        query()
            .from(employee)
            .distinct()
            .select(employee.firstname.length())
            .fetch()
            .collectList()
            .block();
    assertEquals(lengths1, lengths2);
  }

  @Test
  public void duplicate_columns() {
    assertEquals(
        10,
        query()
            .from(employee)
            .select(employee.id, employee.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void duplicate_columns_In_Subquery() {
    var employee2 = new QEmployee("e2");
    assertEquals(
        10,
        (long)
            query()
                .from(employee)
                .where(
                    query()
                        .from(employee2)
                        .where(employee2.id.eq(employee.id))
                        .select(employee2.id, employee2.id)
                        .exists())
                .fetchCount()
                .block());
  }

  @Test
  public void factoryExpression_in_groupBy() {
    Expression<Employee> empBean =
        Projections.bean(Employee.class, employee.id, employee.superiorId);
    assertThat(query().from(employee).groupBy(empBean).select(empBean).fetchFirst().block() != null)
        .isTrue();
  }

  @Test
  @ExcludeIn({H2, SQLITE, DERBY, CUBRID, MYSQL})
  public void full_join() {
    assertEquals(
        18,
        query()
            .from(employee)
            .fullJoin(employee2)
            .on(employee.superiorIdKey.on(employee2))
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  // group by not supported
  //    @Test
  //    public void groupBy_superior() {
  //        R2DBCQuery<?> qry = query()
  //                .from(employee)
  //                .innerJoin(employee._superiorIdKey, employee2);
  //
  //        QTuple subordinates = Projections.tuple(employee2.id, employee2.firstname,
  // employee2.lastname);
  //
  //        Map<Integer, Group> results = qry.transform(
  //                GroupBy.reactiveGroupBy(employee.id).as(employee.firstname, employee.lastname,
  //                        GroupBy.map(employee2.id, subordinates)));
  //
  //        assertEquals(2, results.size());
  //
  //        // Mike Smith
  //        Group group = results.get(1);
  //        assertEquals("Mike", group.getOne(employee.firstname));
  //        assertEquals("Smith", group.getOne(employee.lastname));
  //
  //        Map<Integer, Tuple> emps = group.getMap(employee2.id, subordinates);
  //        assertEquals(4, emps.size());
  //        assertEquals("Steve", emps.get(12).get(employee2.firstname));
  //
  //        // Mary Smith
  //        group = results.get(2);
  //        assertEquals("Mary", group.getOne(employee.firstname));
  //        assertEquals("Smith", group.getOne(employee.lastname));
  //
  //        emps = group.getMap(employee2.id, subordinates);
  //        assertEquals(4, emps.size());
  //        assertEquals("Mason", emps.get(21).get(employee2.lastname));
  //    }

  @Test
  public void groupBy_yearMonth() {
    assertEquals(
        Arrays.asList(10L),
        query()
            .from(employee)
            .groupBy(employee.datefield.yearMonth())
            .orderBy(employee.datefield.yearMonth().asc())
            .select(employee.id.count())
            .fetch()
            .collectList()
            .block());
  }

  @Test
  @ExcludeIn({H2, DB2, DERBY, ORACLE, SQLSERVER})
  public void groupBy_validate() {
    NumberPath<BigDecimal> alias = Expressions.numberPath(BigDecimal.class, "alias");
    assertEquals(
        8,
        query()
            .from(employee)
            .groupBy(alias)
            .select(employee.salary.multiply(100).as(alias), employee.salary.avg())
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IllegalArgumentException.class)
  public void illegalUnion() {
    SubQueryExpression<Integer> sq1 = query().from(employee).select(employee.id.max());
    SubQueryExpression<Integer> sq2 = query().from(employee).select(employee.id.max());
    assertEquals(0, query().from(employee).union(sq1, sq2).fetch().collectList().block().size());
  }

  @Test
  public void in() {
    assertEquals(
        2,
        query()
            .from(employee)
            .where(employee.id.in(Arrays.asList(1, 2)))
            .select(employee)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @ExcludeIn({DERBY, FIREBIRD, SQLITE, SQLSERVER, TERADATA})
  public void in_long_list() {
    List<Integer> ids = new ArrayList<>();
    for (var i = 0; i < 20000; i++) {
      ids.add(i);
    }
    assertEquals(
        query().from(employee).fetchCount().block(),
        query().from(employee).where(employee.id.in(ids)).fetchCount().block());
  }

  @Test
  @ExcludeIn({DERBY, FIREBIRD, SQLITE, SQLSERVER, TERADATA})
  public void notIn_long_list() {
    List<Integer> ids = new ArrayList<>();
    for (var i = 0; i < 20000; i++) {
      ids.add(i);
    }
    assertEquals(
        0, (long) query().from(employee).where(employee.id.notIn(ids)).fetchCount().block());
  }

  @Test
  public void in_empty() {
    assertEquals(
        0,
        (long)
            query()
                .from(employee)
                .where(employee.id.in(Collections.emptyList()))
                .fetchCount()
                .block());
  }

  @Test
  @ExcludeIn(DERBY)
  public void in_null() {
    assertEquals(
        1, (long) query().from(employee).where(employee.id.in(1, null)).fetchCount().block());
  }

  @Test
  @ExcludeIn({MYSQL, TERADATA})
  public void in_subqueries() {
    var e1 = new QEmployee("e1");
    var e2 = new QEmployee("e2");
    assertEquals(
        2,
        (long)
            query()
                .from(employee)
                .where(
                    employee.id.in(
                        query().from(e1).where(e1.firstname.eq("Mike")).select(e1.id),
                        query().from(e2).where(e2.firstname.eq("Mary")).select(e2.id)))
                .fetchCount()
                .block());
  }

  @Test
  public void notIn_empty() {
    long count = query().from(employee).fetchCount().block();
    assertEquals(
        count,
        (long)
            query()
                .from(employee)
                .where(employee.id.notIn(Collections.emptyList()))
                .fetchCount()
                .block());
  }

  @Test
  public void inner_join() {
    assertEquals(
        8,
        query()
            .from(employee)
            .innerJoin(employee2)
            .on(employee.superiorIdKey.on(employee2))
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void inner_join_2Conditions() {
    assertEquals(
        8,
        query()
            .from(employee)
            .innerJoin(employee2)
            .on(employee.superiorIdKey.on(employee2))
            .on(employee2.firstname.isNotNull())
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void join() throws Exception {
    for (String name :
        query()
            .from(survey, survey2)
            .where(survey.id.eq(survey2.id))
            .select(survey.name)
            .fetch()
            .collectList()
            .block()) {
      assertThat(name).isNotNull();
    }
  }

  @Test
  public void joins() {
    for (Tuple row :
        query()
            .from(employee)
            .innerJoin(employee2)
            .on(employee.superiorId.eq(employee2.superiorId))
            .where(employee2.id.eq(10))
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()) {
      assertThat(row.get(employee.id)).isNotNull();
      assertThat(row.get(employee2.id)).isNotNull();
    }
  }

  @Test
  public void left_join() {
    assertEquals(
        10,
        query()
            .from(employee)
            .leftJoin(employee2)
            .on(employee.superiorIdKey.on(employee2))
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void like() {
    assertEquals(
        0, (long) query().from(employee).where(employee.firstname.like("\\")).fetchCount().block());
    assertEquals(
        0,
        (long) query().from(employee).where(employee.firstname.like("\\\\")).fetchCount().block());
  }

  @Test
  public void like_ignore_case() {
    assertEquals(
        3,
        (long)
            query()
                .from(employee)
                .where(employee.firstname.likeIgnoreCase("%m%"))
                .fetchCount()
                .block());
  }

  @Test
  @ExcludeIn(FIREBIRD)
  public void like_escape() {
    List<String> strs = Arrays.asList("%a", "a%", "%a%", "_a", "a_", "_a_", "[C-P]arsen", "a\nb");

    for (String str : strs) {
      assertThat(
              query()
                      .from(employee)
                      .where(
                          Expressions.predicate(
                              Ops.STRING_CONTAINS,
                              Expressions.constant(str),
                              Expressions.constant(str)))
                      .fetchCount()
                      .block()
                  > 0)
          .as(str)
          .isTrue();
    }
  }

  @Test
  @ExcludeIn({DB2, DERBY})
  public void like_number() {
    assertEquals(
        5, (long) query().from(employee).where(employee.id.like("1%")).fetchCount().block());
  }

  @Test
  public void limit() {
    assertEquals(
        Arrays.asList(23, 22, 21, 20),
        query()
            .from(employee)
            .orderBy(employee.firstname.asc())
            .limit(4)
            .select(employee.id)
            .fetch()
            .collectList()
            .block());
  }

  @Test
  public void limit_and_offset() {
    assertEquals(
        Arrays.asList(20, 13, 10, 2),
        query()
            .from(employee)
            .orderBy(employee.firstname.asc())
            .limit(4)
            .offset(3)
            .select(employee.id)
            .fetch()
            .collectList()
            .block());
  }

  // gorup by not supported
  //    @Test
  //    public void limit_and_offset_Group() {
  //        assertEquals(9,
  //                query().from(employee)
  //                        .orderBy(employee.id.asc())
  //                        .limit(100).offset(1)
  //                        .transform(GroupBy.reactiveGroupBy(employee.id).as(employee)).size());
  //    }

  @Test
  public void limit_and_offset_and_Order() {
    List<String> names2 = Arrays.asList("Helen", "Jennifer", "Jim", "Joe");
    assertEquals(
        names2,
        query()
            .from(employee)
            .orderBy(employee.firstname.asc())
            .limit(4)
            .offset(2)
            .select(employee.firstname)
            .fetch()
            .collectList()
            .block());
  }

  @Test
  @IncludeIn(DERBY)
  public void limit_and_offset_In_Derby() {
    expectedQuery = "select e.ID from EMPLOYEE e offset 3 rows fetch next 4 rows only";
    query().from(employee).limit(4).offset(3).select(employee.id).fetch().collectList().block();

    // limit
    expectedQuery = "select e.ID from EMPLOYEE e fetch first 4 rows only";
    query().from(employee).limit(4).select(employee.id).fetch().collectList().block();

    // offset
    expectedQuery = "select e.ID from EMPLOYEE e offset 3 rows";
    query().from(employee).offset(3).select(employee.id).fetch().collectList().block();
  }

  @Test
  @IncludeIn(ORACLE)
  @SkipForQuoted
  public void limit_and_offset_In_Oracle() {
    if (configuration.getUseLiterals()) {
      return;
    }

    // limit
    expectedQuery = "select * from (   select e.ID from EMPLOYEE e ) where rownum <= ?";
    query().from(employee).limit(4).select(employee.id).fetch().collectList().block();

    // offset
    expectedQuery =
        """
        select * from (  select a.*, rownum rn from (   select e.ID from EMPLOYEE e  ) a) where rn\
         > ?\
        """;
    query().from(employee).offset(3).select(employee.id).fetch().collectList().block();

    // limit offset
    expectedQuery =
        """
        select * from (  select a.*, rownum rn from (   select e.ID from EMPLOYEE e  ) a) where rn\
         > 3 and rownum <= 4\
        """;
    query().from(employee).limit(4).offset(3).select(employee.id).fetch().collectList().block();
  }

  @Test
  @ExcludeIn({ORACLE, DB2, DERBY, FIREBIRD, SQLSERVER, CUBRID, TERADATA})
  @SkipForQuoted
  public void limit_and_offset2() {
    // limit
    expectedQuery = "select e.ID from EMPLOYEE e limit ?";
    query().from(employee).limit(4).select(employee.id).fetch().collectList().block();

    // limit offset
    expectedQuery = "select e.ID from EMPLOYEE e limit ? offset ?";
    query().from(employee).limit(4).offset(3).select(employee.id).fetch().collectList().block();
  }

  @Test
  public void limit_and_order() {
    List<String> names1 = Arrays.asList("Barbara", "Daisy", "Helen", "Jennifer");
    assertEquals(
        names1,
        query()
            .from(employee)
            .orderBy(employee.firstname.asc())
            .limit(4)
            .select(employee.firstname)
            .fetch()
            .collectList()
            .block());
  }

  @Test
  @ExcludeIn({DB2, DERBY})
  public void literals() {
    assertEquals(1L, firstResult(ConstantImpl.create(1)).intValue());
    assertEquals(2L, firstResult(ConstantImpl.create(2L)).longValue());
    assertEquals(3.0, firstResult(ConstantImpl.create(3.0)), 0.001);
    assertEquals(4.0f, firstResult(ConstantImpl.create(4.0f)), 0.001);
    assertThat(firstResult(ConstantImpl.create(true))).isTrue();
    assertThat(firstResult(ConstantImpl.create(false))).isFalse();
    assertEquals("abc", firstResult(ConstantImpl.create("abc")));
    assertEquals("'", firstResult(ConstantImpl.create("'")));
    assertEquals("\"", firstResult(ConstantImpl.create("\"")));
    assertEquals("\n", firstResult(ConstantImpl.create("\n")));
    assertEquals("\r\n", firstResult(ConstantImpl.create("\r\n")));
    assertEquals("\t", firstResult(ConstantImpl.create("\t")));
  }

  @Test
  public void literals_literals() {
    if (configuration.getUseLiterals()) {
      literals();
    }
  }

  private double log(double x, int y) {
    return Math.log(x) / Math.log(y);
  }

  @Test
  @ExcludeIn({SQLITE, DERBY})
  public void lPad() {
    assertEquals("  ab", firstResult(StringExpressions.lpad(ConstantImpl.create("ab"), 4)));
    assertEquals("!!ab", firstResult(StringExpressions.lpad(ConstantImpl.create("ab"), 4, '!')));
  }

  //    @Test
  //    public void map() {
  //        Map<Integer, String> idToName = query().from(employee).map(employee.id.as("id"),
  // employee.firstname);
  //        for (Map.Entry<Integer, String> entry : idToName.entrySet()) {
  //            assertNotNull(entry.getKey());
  //            assertNotNull(entry.getValue());
  //        }
  //    }

  @Test
  @SuppressWarnings("serial")
  public void mappingProjection() {
    var pairs =
        query()
            .from(employee)
            .select(
                new MappingProjection<Pair<String, String>>(
                    Pair.class, employee.firstname, employee.lastname) {
                  @Override
                  protected Pair<String, String> map(Tuple row) {
                    return Pair.of(row.get(employee.firstname), row.get(employee.lastname));
                  }
                })
            .fetch()
            .collectList()
            .block();

    for (Pair<String, String> pair : pairs) {
      assertThat(pair.getFirst()).isNotNull();
      assertThat(pair.getSecond()).isNotNull();
    }
  }

  @Test
  public void math() {
    math(Expressions.numberTemplate(Double.class, "0.50"));
  }

  @Test
  @ExcludeIn({FIREBIRD, SQLSERVER}) // FIXME
  public void math2() {
    math(Expressions.constant(0.5));
  }

  private void math(Expression<Double> expr) {
    var precision = 0.001;
    assertEquals(Math.acos(0.5), firstResult(MathExpressions.acos(expr)), precision);
    assertEquals(Math.asin(0.5), firstResult(MathExpressions.asin(expr)), precision);
    assertEquals(Math.atan(0.5), firstResult(MathExpressions.atan(expr)), precision);
    assertEquals(Math.cos(0.5), firstResult(MathExpressions.cos(expr)), precision);
    assertEquals(Math.cosh(0.5), firstResult(MathExpressions.cosh(expr)), precision);
    assertEquals(cot(0.5), firstResult(MathExpressions.cot(expr)), precision);
    if (target != Target.DERBY || expr instanceof Constant) {
      // FIXME: The resulting value is outside the range for the data type DECIMAL/NUMERIC(4,4).
      assertEquals(coth(0.5), firstResult(MathExpressions.coth(expr)), precision);
    }

    assertEquals(degrees(0.5), firstResult(MathExpressions.degrees(expr)), precision);
    assertEquals(Math.exp(0.5), firstResult(MathExpressions.exp(expr)), precision);
    assertEquals(Math.log(0.5), firstResult(MathExpressions.ln(expr)), precision);
    assertEquals(log(0.5, 10), firstResult(MathExpressions.log(expr, 10)), precision);
    assertEquals(0.25, firstResult(MathExpressions.power(expr, 2)), precision);
    assertEquals(radians(0.5), firstResult(MathExpressions.radians(expr)), precision);
    assertEquals(Integer.valueOf(1), firstResult(MathExpressions.sign(expr)));
    assertEquals(Math.sin(0.5), firstResult(MathExpressions.sin(expr)), precision);
    assertEquals(Math.sinh(0.5), firstResult(MathExpressions.sinh(expr)), precision);
    assertEquals(Math.tan(0.5), firstResult(MathExpressions.tan(expr)), precision);
    assertEquals(Math.tanh(0.5), firstResult(MathExpressions.tanh(expr)), precision);
  }

  @Test
  @ExcludeIn(DERBY) // Derby doesn't support mod with decimal operands
  public void math3() {
    // 1.0 + 2.0 * 3.0 - 4.0 / 5.0 + 6.0 % 3.0
    NumberTemplate<Double> one = Expressions.numberTemplate(Double.class, "1.0");
    NumberTemplate<Double> two = Expressions.numberTemplate(Double.class, "2.0");
    NumberTemplate<Double> three = Expressions.numberTemplate(Double.class, "3.0");
    NumberTemplate<Double> four = Expressions.numberTemplate(Double.class, "4.0");
    NumberTemplate<Double> five = Expressions.numberTemplate(Double.class, "5.0");
    NumberTemplate<Double> six = Expressions.numberTemplate(Double.class, "6.0");
    var num =
        query()
            .select(one.add(two.multiply(three)).subtract(four.divide(five)).add(six.mod(three)))
            .fetchFirst()
            .block();
    assertEquals(6.2, num, 0.001);
  }

  @Test
  public void nested_tuple_projection() {
    var concat = new Concatenation(employee.firstname, employee.lastname);
    var tuples =
        query()
            .from(employee)
            .select(employee.firstname, employee.lastname, concat)
            .fetch()
            .collectList()
            .block();
    assertThat(tuples).isNotEmpty();
    for (Tuple tuple : tuples) {
      String firstName = tuple.get(employee.firstname);
      String lastName = tuple.get(employee.lastname);
      assertEquals(firstName + lastName, tuple.get(concat));
    }
  }

  @Test
  public void no_from() {
    assertThat(firstResult(DateExpression.currentDate())).isNotNull();
  }

  @Test
  public void nullif() {
    query()
        .from(employee)
        .select(employee.firstname.nullif(employee.lastname))
        .fetch()
        .collectList()
        .block();
  }

  @Test
  public void nullif_constant() {
    query().from(employee).select(employee.firstname.nullif("xxx")).fetch().collectList().block();
  }

  @Test
  public void num_cast() {
    query().from(employee).select(employee.id.castToNum(Long.class)).fetch().collectList().block();
    query().from(employee).select(employee.id.castToNum(Float.class)).fetch().collectList().block();
    query()
        .from(employee)
        .select(employee.id.castToNum(Double.class))
        .fetch()
        .collectList()
        .block();
  }

  @Test
  public void num_cast2() {
    NumberExpression<Integer> num = Expressions.numberTemplate(Integer.class, "0");
    firstResult(num.castToNum(Byte.class));
    firstResult(num.castToNum(Short.class));
    firstResult(num.castToNum(Integer.class));
    firstResult(num.castToNum(Long.class));
    firstResult(num.castToNum(Float.class));
    firstResult(num.castToNum(Double.class));
  }

  @Test
  public void num_date_operation() {
    long result =
        query().select(employee.datefield.year().mod(1)).from(employee).fetchFirst().block();
    assertEquals(0, result);
  }

  @Test
  @ExcludeIn({DERBY, FIREBIRD, POSTGRESQL})
  public void number_as_boolean() {
    var numberTest = QNumberTest.numberTest;
    delete(numberTest).execute().block();
    insert(numberTest).set(numberTest.col1Boolean, true).execute().block();
    insert(numberTest).set(numberTest.col1Number, (byte) 1).execute().block();
    assertEquals(
        2,
        query()
            .from(numberTest)
            .select(numberTest.col1Boolean)
            .fetch()
            .collectList()
            .block()
            .size());
    assertEquals(
        2,
        query()
            .from(numberTest)
            .select(numberTest.col1Number)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @Ignore("not valid as streams cannot have nulls")
  public void number_as_boolean_Null() {
    var numberTest = QNumberTest.numberTest;
    delete(numberTest).execute().block();
    insert(numberTest).setNull(numberTest.col1Boolean).execute().block();
    insert(numberTest).setNull(numberTest.col1Number).execute().block();
    assertEquals(
        2,
        query()
            .from(numberTest)
            .select(numberTest.col1Boolean)
            .fetch()
            .collectList()
            .block()
            .size());
    assertEquals(
        2,
        query()
            .from(numberTest)
            .select(numberTest.col1Number)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void offset_only() {
    assertEquals(
        Arrays.asList(20, 13, 10, 2, 1, 11, 12),
        query()
            .from(employee)
            .orderBy(employee.firstname.asc())
            .offset(3)
            .select(employee.id)
            .fetch()
            .collectList()
            .block());
  }

  @Test
  public void operation_in_constant_list() {
    assertEquals(
        0,
        (long)
            query()
                .from(survey)
                .where(survey.name.charAt(0).in(Arrays.asList('a')))
                .fetchCount()
                .block());
    assertEquals(
        0,
        (long)
            query()
                .from(survey)
                .where(survey.name.charAt(0).in(Arrays.asList('a', 'b')))
                .fetchCount()
                .block());
    assertEquals(
        0,
        (long)
            query()
                .from(survey)
                .where(survey.name.charAt(0).in(Arrays.asList('a', 'b', 'c')))
                .fetchCount()
                .block());
  }

  @Test
  public void order_nullsFirst() {
    assertEquals(
        Arrays.asList("Hello World"),
        query()
            .from(survey)
            .orderBy(survey.name.asc().nullsFirst())
            .select(survey.name)
            .fetch()
            .collectList()
            .block());
  }

  @Test
  public void order_nullsLast() {
    assertEquals(
        Arrays.asList("Hello World"),
        query()
            .from(survey)
            .orderBy(survey.name.asc().nullsLast())
            .select(survey.name)
            .fetch()
            .collectList()
            .block());
  }

  @Test
  public void params() {
    var name = new Param<String>(String.class, "name");
    assertEquals(
        "Mike",
        query()
            .from(employee)
            .where(employee.firstname.eq(name))
            .set(name, "Mike")
            .select(employee.firstname)
            .fetchFirst()
            .block());
  }

  @Test
  public void params_anon() {
    var name = new Param<String>(String.class);
    assertEquals(
        "Mike",
        query()
            .from(employee)
            .where(employee.firstname.eq(name))
            .set(name, "Mike")
            .select(employee.firstname)
            .fetchFirst()
            .block());
  }

  @Test(expected = ParamNotSetException.class)
  public void params_not_set() {
    var name = new Param<String>(String.class, "name");
    assertEquals(
        "Mike",
        query()
            .from(employee)
            .where(employee.firstname.eq(name))
            .select(employee.firstname)
            .fetchFirst()
            .block());
  }

  @Test
  @ExcludeIn({DB2, DERBY, FIREBIRD, HSQLDB, ORACLE, SQLSERVER})
  @SkipForQuoted
  public void path_alias() {
    expectedQuery =
        """
        select e.LASTNAME, sum(e.SALARY) as salarySum \
        from EMPLOYEE e \
        group by e.LASTNAME having salarySum > ?\
        """;

    var salarySum = employee.salary.sumBigDecimal().as("salarySum");
    query()
        .from(employee)
        .groupBy(employee.lastname)
        .having(salarySum.gt(10000))
        .select(employee.lastname, salarySum)
        .fetch()
        .collectList()
        .block();
  }

  @Test
  public void path_in_constant_list() {
    assertEquals(
        0,
        (long) query().from(survey).where(survey.name.in(Arrays.asList("a"))).fetchCount().block());
    assertEquals(
        0,
        (long)
            query()
                .from(survey)
                .where(survey.name.in(Arrays.asList("a", "b")))
                .fetchCount()
                .block());
    assertEquals(
        0,
        (long)
            query()
                .from(survey)
                .where(survey.name.in(Arrays.asList("a", "b", "c")))
                .fetchCount()
                .block());
  }

  @Test
  public void precedence() {
    var fn = employee.firstname;
    var ln = employee.lastname;
    Predicate where = fn.eq("Mike").and(ln.eq("Smith")).or(fn.eq("Joe").and(ln.eq("Divis")));
    assertEquals(2L, (long) query().from(employee).where(where).fetchCount().block());
  }

  @Test
  public void precedence2() {
    var fn = employee.firstname;
    var ln = employee.lastname;
    Predicate where = fn.eq("Mike").and(ln.eq("Smith").or(fn.eq("Joe")).and(ln.eq("Divis")));
    assertEquals(0L, (long) query().from(employee).where(where).fetchCount().block());
  }

  @Test
  public void projection_and_twoColumns() {
    // projection and two columns
    for (Tuple row :
        query()
            .from(survey)
            .select(new QIdName(survey.id, survey.name), survey.id, survey.name)
            .fetch()
            .collectList()
            .block()) {
      assertEquals(3, row.size());
      assertEquals(IdName.class, row.get(0, Object.class).getClass());
      assertEquals(Integer.class, row.get(1, Object.class).getClass());
      assertEquals(String.class, row.get(2, Object.class).getClass());
    }
  }

  @Test
  public void qBeanUsage() {
    var sq = new PathBuilder<Object[]>(Object[].class, "sq");
    var surveys =
        query()
            .from(query().from(survey).select(survey.all()).as("sq"))
            .select(
                Projections.bean(
                    Survey.class, Collections.singletonMap("name", sq.get(survey.name))))
            .fetch()
            .collectList()
            .block();
    assertThat(surveys).isNotEmpty();
  }

  @Test
  public void query_with_constant() throws Exception {
    for (Tuple row :
        query()
            .from(survey)
            .where(survey.id.eq(1))
            .select(survey.id, survey.name)
            .fetch()
            .collectList()
            .block()) {
      assertThat(row.get(survey.id)).isNotNull();
      assertThat(row.get(survey.name)).isNotNull();
    }
  }

  @Test
  public void query1() throws Exception {
    for (String s : query().from(survey).select(survey.name).fetch().collectList().block()) {
      assertThat(s).isNotNull();
    }
  }

  @Test
  public void query2() throws Exception {
    for (Tuple row :
        query().from(survey).select(survey.id, survey.name).fetch().collectList().block()) {
      assertThat(row.get(survey.id)).isNotNull();
      assertThat(row.get(survey.name)).isNotNull();
    }
  }

  private double radians(double x) {
    return x * Math.PI / 180.0;
  }

  @Test
  public void random() {
    firstResult(MathExpressions.random());
  }

  @Test
  @ExcludeIn({FIREBIRD, ORACLE, POSTGRESQL, SQLITE, TERADATA})
  public void random2() {
    firstResult(MathExpressions.random(10));
  }

  @Test
  public void relationalPath_projection() {
    var results =
        query()
            .from(employee, employee2)
            .where(employee.id.eq(employee2.id))
            .select(employee, employee2)
            .fetch()
            .collectList()
            .block();
    assertThat(results).isNotEmpty();
    for (Tuple row : results) {
      Employee e1 = row.get(employee);
      Employee e2 = row.get(employee2);
      assertEquals(e1.getId(), e2.getId());
    }
  }

  @Test
  public void relationalPath_eq() {
    assertEquals(
        10,
        query()
            .from(employee, employee2)
            .where(employee.eq(employee2))
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void relationalPath_ne() {
    assertEquals(
        90,
        query()
            .from(employee, employee2)
            .where(employee.ne(employee2))
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void relationalPath_eq2() {
    assertEquals(
        1,
        query()
            .from(survey, survey2)
            .where(survey.eq(survey2))
            .select(survey.id, survey2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void relationalPath_ne2() {
    assertEquals(
        0,
        query()
            .from(survey, survey2)
            .where(survey.ne(survey2))
            .select(survey.id, survey2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @ExcludeIn(SQLITE)
  public void right_join() {
    assertEquals(
        16,
        query()
            .from(employee)
            .rightJoin(employee2)
            .on(employee.superiorIdKey.on(employee2))
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @ExcludeIn(DERBY)
  public void round() {
    Expression<Double> expr = Expressions.numberTemplate(Double.class, "1.32");

    assertEquals(Double.valueOf(1.0), firstResult(MathExpressions.round(expr)));
    assertEquals(Double.valueOf(1.3), firstResult(MathExpressions.round(expr, 1)));
  }

  @Test
  @ExcludeIn({SQLITE, DERBY})
  public void rpad() {
    assertEquals("ab  ", firstResult(StringExpressions.rpad(ConstantImpl.create("ab"), 4)));
    assertEquals("ab!!", firstResult(StringExpressions.rpad(ConstantImpl.create("ab"), 4, '!')));
  }

  @Test
  @Ignore
  @ExcludeIn({ORACLE, DERBY, SQLSERVER})
  public void select_booleanExpr() {
    // TODO : FIXME
    System.out.println(query().from(survey).select(survey.id.eq(0)).fetch().collectList().block());
  }

  @Test
  @Ignore
  @ExcludeIn({ORACLE, DERBY, SQLSERVER})
  public void select_booleanExpr2() {
    // TODO : FIXME
    System.out.println(query().from(survey).select(survey.id.gt(0)).fetch().collectList().block());
  }

  @Test
  public void select_booleanExpr3() {
    assertThat(query().select(Expressions.TRUE).fetchFirst().block()).isTrue();
    assertThat(query().select(Expressions.FALSE).fetchFirst().block()).isFalse();
  }

  @Test
  public void select_concat() {
    for (Tuple row :
        query()
            .from(survey)
            .select(survey.name, survey.name.append("Hello World"))
            .fetch()
            .collectList()
            .block()) {
      assertEquals(
          row.get(survey.name) + "Hello World", row.get(survey.name.append("Hello World")));
    }
  }

  @Test
  @ExcludeIn({SQLITE, CUBRID, TERADATA})
  public void select_for_update() {
    assertEquals(
        1, query().from(survey).forUpdate().select(survey.id).fetch().collectList().block().size());
  }

  @Test
  @ExcludeIn({SQLITE, CUBRID, TERADATA})
  public void select_for_update_Where() {
    assertEquals(
        1,
        query()
            .from(survey)
            .forUpdate()
            .where(survey.id.isNotNull())
            .select(survey.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @ExcludeIn({SQLITE, CUBRID, TERADATA})
  public void select_for_update_UniqueResult() {
    query().from(survey).forUpdate().select(survey.id).fetchOne().block();
  }

  @Test
  public void select_for_share() {
    if (configuration.getTemplates().isForShareSupported()) {
      assertEquals(
          1,
          query()
              .from(survey)
              .forShare()
              .where(survey.id.isNotNull())
              .select(survey.id)
              .fetch()
              .collectList()
              .block()
              .size());
    } else {
      try {
        query()
            .from(survey)
            .forShare()
            .where(survey.id.isNotNull())
            .select(survey.id)
            .fetch()
            .collectList()
            .block()
            .size();
        fail("");
      } catch (QueryException e) {
        assertEquals("Using forShare() is not supported", e.getMessage());
      }
    }
  }

  @Test
  @SkipForQuoted
  public void serialization() {
    R2DBCQuery<?> query = query();
    query.from(survey);
    assertEquals("from SURVEY s", query.toString());
    query.from(survey2);
    assertEquals("from SURVEY s, SURVEY s2", query.toString());
  }

  @Test
  public void serialization2() throws Exception {
    var rows = query().from(survey).select(survey.id, survey.name).fetch().collectList().block();
    serialize(rows);
  }

  private void serialize(List<Tuple> rows) throws IOException {
    rows = Serialization.serialize(rows);
    for (Tuple row : rows) {
      row.hashCode();
    }
  }

  @Test
  public void single() {
    assertThat(query().from(survey).select(survey.name).fetchFirst().block()).isNotNull();
  }

  @Test
  public void single_array() {
    assertThat(query().from(survey).select(new Expression<?>[] {survey.name}).fetchFirst().block())
        .isNotNull();
  }

  @Test
  public void single_column() {
    // single column
    for (String s : query().from(survey).select(survey.name).fetch().collectList().block()) {
      assertThat(s).isNotNull();
    }
  }

  @Test
  public void single_column_via_Object_type() {
    for (Object s :
        query()
            .from(survey)
            .select(ExpressionUtils.path(Object.class, survey.name.getMetadata()))
            .fetch()
            .collectList()
            .block()) {
      assertEquals(String.class, s.getClass());
    }
  }

  @Test
  public void specialChars() {
    assertEquals(
        0,
        (long)
            query()
                .from(survey)
                .where(survey.name.in("\n", "\r", "\\", "\'", "\""))
                .fetchCount()
                .block());
  }

  @Test
  public void standardTest() {
    standardTest.runBooleanTests(employee.firstname.isNull(), employee2.lastname.isNotNull());
    // datetime
    standardTest.runDateTests(employee.datefield, employee2.datefield, date);

    // numeric
    standardTest.runNumericCasts(employee.id, employee2.id, 1);
    standardTest.runNumericTests(employee.id, employee2.id, 1);
    // BigDecimal
    standardTest.runNumericTests(employee.salary, employee2.salary, new BigDecimal("30000.00"));

    standardTest.runStringTests(employee.firstname, employee2.firstname, "Jennifer");
    var target = Connections.getTarget();
    if (target != SQLITE && target != SQLSERVER) {
      // jTDS driver does not support TIME SQL data type
      standardTest.runTimeTests(employee.timefield, employee2.timefield, time);
    }

    standardTest.report();
  }

  @Test
  @IncludeIn(H2)
  public void standardTest_turkish() {
    var defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.of("tr", "TR"));
    try {
      standardTest();
    } finally {
      Locale.setDefault(defaultLocale);
    }
  }

  @Test
  @ExcludeIn(SQLITE)
  public void string() {
    StringExpression str = Expressions.stringTemplate("'  abcd  '");

    assertEquals("abcd  ", firstResult(StringExpressions.ltrim(str)));
    assertEquals(Integer.valueOf(3), firstResult(str.locate("a")));
    assertEquals(Integer.valueOf(0), firstResult(str.locate("a", 4)));
    assertEquals(Integer.valueOf(4), firstResult(str.locate("b", 2)));
    assertEquals("  abcd", firstResult(StringExpressions.rtrim(str)));
    assertEquals("abc", firstResult(str.substring(2, 5)));
  }

  @Test
  @ExcludeIn(SQLITE)
  public void string_withTemplate() {
    StringExpression str = Expressions.stringTemplate("'  abcd  '");

    NumberExpression<Integer> four = Expressions.numberTemplate(Integer.class, "4");
    NumberExpression<Integer> two = Expressions.numberTemplate(Integer.class, "2");
    NumberExpression<Integer> five = Expressions.numberTemplate(Integer.class, "5");

    assertEquals("abcd  ", firstResult(StringExpressions.ltrim(str)));
    assertEquals(Integer.valueOf(3), firstResult(str.locate("a")));
    assertEquals(Integer.valueOf(0), firstResult(str.locate("a", four)));
    assertEquals(Integer.valueOf(4), firstResult(str.locate("b", two)));
    assertEquals("  abcd", firstResult(StringExpressions.rtrim(str)));
    assertEquals("abc", firstResult(str.substring(two, five)));
  }

  @Test
  @ExcludeIn({POSTGRESQL, SQLITE})
  public void string_indexOf() {
    StringExpression str = Expressions.stringTemplate("'  abcd  '");

    assertEquals(Integer.valueOf(2), firstResult(str.indexOf("a")));
    assertEquals(Integer.valueOf(-1), firstResult(str.indexOf("a", 4)));
    assertEquals(Integer.valueOf(3), firstResult(str.indexOf("b", 2)));
  }

  @Test
  public void stringFunctions2() {
    for (BooleanExpression where :
        Arrays.asList(
            employee.firstname.startsWith("a"),
            employee.firstname.startsWithIgnoreCase("a"),
            employee.firstname.endsWith("a"),
            employee.firstname.endsWithIgnoreCase("a"))) {
      query().from(employee).where(where).select(employee.firstname).fetch().collectList().block();
    }
  }

  @Test
  @ExcludeIn(SQLITE)
  public void string_left() {
    assertEquals(
        "John",
        query()
            .from(employee)
            .where(employee.lastname.eq("Johnson"))
            .select(R2DBCExpressions.left(employee.lastname, 4))
            .fetchFirst()
            .block());
  }

  @Test
  @ExcludeIn({DERBY, SQLITE})
  public void string_right() {
    assertEquals(
        "son",
        query()
            .from(employee)
            .where(employee.lastname.eq("Johnson"))
            .select(R2DBCExpressions.right(employee.lastname, 3))
            .fetchFirst()
            .block());
  }

  @Test
  @ExcludeIn({DERBY, SQLITE})
  public void string_left_Right() {
    assertEquals(
        "hn",
        query()
            .from(employee)
            .where(employee.lastname.eq("Johnson"))
            .select(R2DBCExpressions.right(R2DBCExpressions.left(employee.lastname, 4), 2))
            .fetchFirst()
            .block());
  }

  @Test
  @ExcludeIn({DERBY, SQLITE})
  public void string_right_Left() {
    assertEquals(
        "ns",
        query()
            .from(employee)
            .where(employee.lastname.eq("Johnson"))
            .select(R2DBCExpressions.left(R2DBCExpressions.right(employee.lastname, 4), 2))
            .fetchFirst()
            .block());
  }

  @Test
  @ExcludeIn({DB2, DERBY, FIREBIRD})
  public void substring() {
    // SELECT * FROM account where SUBSTRING(name, -x, 1) = SUBSTRING(name, -y, 1)
    query()
        .from(employee)
        .where(employee.firstname.substring(-3, 1).eq(employee.firstname.substring(-2, 1)))
        .select(employee.id)
        .fetch()
        .collectList()
        .block();
  }

  @Test
  public void syntax_for_employee() {
    assertEquals(
        3,
        query()
            .from(employee)
            .groupBy(employee.superiorId)
            .orderBy(employee.superiorId.asc())
            .select(employee.salary.avg(), employee.id.max())
            .fetch()
            .collectList()
            .block()
            .size());

    assertEquals(
        2,
        query()
            .from(employee)
            .groupBy(employee.superiorId)
            .having(employee.id.max().gt(5))
            .orderBy(employee.superiorId.asc())
            .select(employee.salary.avg(), employee.id.max())
            .fetch()
            .collectList()
            .block()
            .size());

    assertEquals(
        2,
        query()
            .from(employee)
            .groupBy(employee.superiorId)
            .having(employee.superiorId.isNotNull())
            .orderBy(employee.superiorId.asc())
            .select(employee.salary.avg(), employee.id.max())
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void templateExpression() {
    NumberExpression<Integer> one = Expressions.numberTemplate(Integer.class, "1");
    assertEquals(
        Arrays.asList(1),
        query().from(survey).select(one.as("col1")).fetch().collectList().block());
  }

  // group by not supporetd
  //    @Test
  //    public void transform_groupBy() {
  //        QEmployee employee = new QEmployee("employee");
  //        QEmployee employee2 = new QEmployee("employee2");
  //        Map<Integer, Map<Integer, Employee>> results = query().from(employee, employee2)
  //                .transform(GroupBy.groupBy(employee.id).as(GroupBy.map(employee2.id,
  // employee2)));
  //
  //        long count = query().from(employee).fetchCount().block();
  //        assertEquals(count, results.size());
  //        for (Map.Entry<Integer, Map<Integer, Employee>> entry : results.entrySet()) {
  //            Map<Integer, Employee> employees = entry.getValue();
  //            assertEquals(count, employees.size());
  //        }
  //
  //    }

  @Test
  public void tuple_projection() {
    var tuples =
        query()
            .from(employee)
            .select(employee.firstname, employee.lastname)
            .fetch()
            .collectList()
            .block();
    assertThat(tuples).isNotEmpty();
    for (Tuple tuple : tuples) {
      assertThat(tuple.get(employee.firstname)).isNotNull();
      assertThat(tuple.get(employee.lastname)).isNotNull();
    }
  }

  @Test
  @ExcludeIn({DB2, DERBY})
  public void tuple2() {
    assertEquals(
        10,
        query()
            .from(employee)
            .select(Expressions.as(ConstantImpl.create("1"), "code"), employee.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void twoColumns() {
    // two columns
    for (Tuple row :
        query().from(survey).select(survey.id, survey.name).fetch().collectList().block()) {
      assertEquals(2, row.size());
      assertEquals(Integer.class, row.get(0, Object.class).getClass());
      assertEquals(String.class, row.get(1, Object.class).getClass());
    }
  }

  @Test
  public void twoColumns_and_projection() {
    // two columns and projection
    for (Tuple row :
        query()
            .from(survey)
            .select(survey.id, survey.name, new QIdName(survey.id, survey.name))
            .fetch()
            .collectList()
            .block()) {
      assertEquals(3, row.size());
      assertEquals(Integer.class, row.get(0, Object.class).getClass());
      assertEquals(String.class, row.get(1, Object.class).getClass());
      assertEquals(IdName.class, row.get(2, Object.class).getClass());
    }
  }

  @Test
  public void unique_Constructor_projection() {
    var idAndName =
        query()
            .from(survey)
            .limit(1)
            .select(new QIdName(survey.id, survey.name))
            .fetchFirst()
            .block();
    assertThat(idAndName).isNotNull();
    assertThat(idAndName.getId()).isNotNull();
    assertThat(idAndName.getName()).isNotNull();
  }

  @Test
  public void unique_single() {
    var s = query().from(survey).limit(1).select(survey.name).fetchFirst().block();
    assertThat(s).isNotNull();
  }

  @Test
  public void unique_wildcard() {
    // unique wildcard
    var row = query().from(survey).limit(1).select(survey.all()).fetchFirst().block();
    assertThat(row).isNotNull();
    assertEquals(3, row.size());
    assertThat(row.get(0, Object.class)).isNotNull();
    assertThat(row.get(1, Object.class)).as(row.get(0, Object.class) + " is not null").isNotNull();
  }

  @Ignore("we select the first result if one selected")
  @Test(expected = NonUniqueResultException.class)
  public void uniqueResultContract() {
    query().from(employee).select(employee.all()).fetchOne().block();
  }

  @Test
  public void various() {
    for (String s :
        query().from(survey).select(survey.name.lower()).fetch().collectList().block()) {
      assertEquals(s, s.toLowerCase());
    }

    for (String s :
        query().from(survey).select(survey.name.append("abc")).fetch().collectList().block()) {
      assertThat(s).endsWith("abc");
    }

    System.out.println(query().from(survey).select(survey.id.sqrt()).fetch().collectList().block());
  }

  @Test
  public void where_exists() {
    R2DBCQuery<Integer> sq1 = query().from(employee).select(employee.id.max());
    assertEquals(10, (long) query().from(employee).where(sq1.exists()).fetchCount().block());
  }

  @Test
  public void where_exists_Not() {
    R2DBCQuery<Integer> sq1 = query().from(employee).select(employee.id.max());
    assertEquals(0, (long) query().from(employee).where(sq1.exists().not()).fetchCount().block());
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with() {
    assertEquals(
        10,
        query()
            .with(
                employee2,
                query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
            .from(employee, employee2)
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with2() {
    var employee3 = new QEmployee("e3");
    assertEquals(
        100,
        query()
            .with(
                employee2,
                query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
            .with(
                employee2,
                query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
            .from(employee, employee2, employee3)
            .select(employee.id, employee2.id, employee3.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with3() {
    assertEquals(
        10,
        query()
            .with(employee2, employee2.all())
            .as(query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
            .from(employee, employee2)
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with_limit() {
    assertEquals(
        5,
        query()
            .with(employee2, employee2.all())
            .as(query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
            .from(employee, employee2)
            .limit(5)
            .orderBy(employee.id.asc(), employee2.id.asc())
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with_limitOffset() {
    assertEquals(
        5,
        query()
            .with(employee2, employee2.all())
            .as(query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
            .from(employee, employee2)
            .limit(10)
            .offset(5)
            .orderBy(employee.id.asc(), employee2.id.asc())
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @IncludeIn({ORACLE, POSTGRESQL})
  public void with_recursive() {
    assertEquals(
        10,
        query()
            .withRecursive(
                employee2,
                query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
            .from(employee, employee2)
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  @IncludeIn({ORACLE, POSTGRESQL})
  public void with_recursive2() {
    assertEquals(
        10,
        query()
            .withRecursive(employee2, employee2.all())
            .as(query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
            .from(employee, employee2)
            .select(employee.id, employee2.id)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void wildcard() {
    // wildcard
    for (Tuple row : query().from(survey).select(survey.all()).fetch().collectList().block()) {
      assertThat(row).isNotNull();
      assertEquals(3, row.size());
      assertThat(row.get(0, Object.class)).isNotNull();
      assertThat(row.get(1, Object.class))
          .as(row.get(0, Object.class) + " is not null")
          .isNotNull();
    }
  }

  @Test
  @SkipForQuoted
  public void wildcard_all() {
    expectedQuery = "select * from EMPLOYEE e";
    query().from(employee).select(Wildcard.all).fetch().collectList().block();
  }

  @Test
  public void wildcard_all2() {
    assertEquals(
        10,
        query()
            .from(new RelationalPathBase<>(Object.class, "employee", "public", "EMPLOYEE"))
            .select(Wildcard.all)
            .fetch()
            .collectList()
            .block()
            .size());
  }

  @Test
  public void wildcard_and_qTuple() {
    // wildcard and QTuple
    for (Tuple tuple : query().from(survey).select(survey.all()).fetch().collectList().block()) {
      assertThat(tuple.get(survey.id)).isNotNull();
      assertThat(tuple.get(survey.name)).isNotNull();
    }
  }

  @Test
  @IncludeIn(ORACLE)
  public void withinGroup() {
    List<WithinGroup<?>> exprs = new ArrayList<>();
    var path = survey.id;

    // two args
    add(exprs, R2DBCExpressions.cumeDist(2, 3));
    add(exprs, R2DBCExpressions.denseRank(4, 5));
    add(exprs, R2DBCExpressions.listagg(path, ","));
    add(exprs, R2DBCExpressions.percentRank(6, 7));
    add(exprs, R2DBCExpressions.rank(8, 9));

    for (WithinGroup<?> wg : exprs) {
      query()
          .from(survey)
          .select(wg.withinGroup().orderBy(survey.id, survey.id))
          .fetch()
          .collectList()
          .block();
      query()
          .from(survey)
          .select(wg.withinGroup().orderBy(survey.id.asc(), survey.id.asc()))
          .fetch()
          .collectList()
          .block();
    }

    // one arg
    exprs.clear();
    add(exprs, R2DBCExpressions.percentileCont(0.1));
    add(exprs, R2DBCExpressions.percentileDisc(0.9));

    for (WithinGroup<?> wg : exprs) {
      query()
          .from(survey)
          .select(wg.withinGroup().orderBy(survey.id))
          .fetch()
          .collectList()
          .block();
      query()
          .from(survey)
          .select(wg.withinGroup().orderBy(survey.id.asc()))
          .fetch()
          .collectList()
          .block();
    }
  }

  @Test
  @ExcludeIn({DB2, DERBY, H2})
  public void yearWeek() {
    R2DBCQuery<?> query = query().from(employee).orderBy(employee.id.asc());
    assertEquals(
        Integer.valueOf(200006), query.select(employee.datefield.yearWeek()).fetchFirst().block());
  }

  @Test
  @IncludeIn({H2})
  public void yearWeek_h2() {
    R2DBCQuery<?> query = query().from(employee).orderBy(employee.id.asc());
    assertEquals(
        Integer.valueOf(200007), query.select(employee.datefield.yearWeek()).fetchFirst().block());
  }

  @Test
  public void statementOptions() {
    var options = StatementOptions.builder().setFetchSize(15).setMaxRows(150).build();
    var query =
        query()
            .from(employee)
            .orderBy(employee.id.asc())
            .statementOptions(options)
            .select(employee.id);
    query.fetch().collectList().block();
  }

  @Test
  @ExcludeIn({DB2, DERBY, ORACLE, SQLSERVER})
  public void groupConcat() {
    List<String> expected =
        Arrays.asList("Mike,Mary", "Joe,Peter,Steve,Jim", "Jennifer,Helen,Daisy,Barbara");
    if (Connections.getTarget() == POSTGRESQL) {
      expected = Arrays.asList("Steve,Jim,Joe,Peter", "Barbara,Helen,Daisy,Jennifer", "Mary,Mike");
    }
    assertEquals(
        expected,
        query()
            .select(R2DBCExpressions.groupConcat(employee.firstname))
            .from(employee)
            .groupBy(employee.superiorId)
            .fetch()
            .collectList()
            .block());
  }

  @Test
  @ExcludeIn({DB2, DERBY, ORACLE, SQLSERVER})
  public void groupConcat2() {
    List<String> expected =
        Arrays.asList("Mike-Mary", "Joe-Peter-Steve-Jim", "Jennifer-Helen-Daisy-Barbara");
    if (Connections.getTarget() == POSTGRESQL) {
      expected = Arrays.asList("Steve-Jim-Joe-Peter", "Barbara-Helen-Daisy-Jennifer", "Mary-Mike");
    }
    assertEquals(
        expected,
        query()
            .select(R2DBCExpressions.groupConcat(employee.firstname, "-"))
            .from(employee)
            .groupBy(employee.superiorId)
            .fetch()
            .collectList()
            .block());
  }
}
// CHECKSTYLERULE:ON: FileLength
