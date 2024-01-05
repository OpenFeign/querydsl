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
import static com.querydsl.core.Target.TERADATA;
import static com.querydsl.sql.Constants.date;
import static com.querydsl.sql.Constants.employee;
import static com.querydsl.sql.Constants.employee2;
import static com.querydsl.sql.Constants.survey;
import static com.querydsl.sql.Constants.survey2;
import static com.querydsl.sql.Constants.time;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.Pair;
import com.querydsl.core.Fetchable;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryException;
import com.querydsl.core.QueryExecution;
import com.querydsl.core.QueryResults;
import com.querydsl.core.QuerydslModule;
import com.querydsl.core.Target;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupBy;
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
import com.querydsl.core.types.QTuple;
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
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.sql.domain.Employee;
import com.querydsl.sql.domain.IdName;
import com.querydsl.sql.domain.QEmployee;
import com.querydsl.sql.domain.QEmployeeNoPK;
import com.querydsl.sql.domain.QIdName;
import com.querydsl.sql.domain.QNumberTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.compress.utils.Sets;
import org.junit.Ignore;
import org.junit.Test;

public class SelectBase extends AbstractBaseTest {

  private static final Expression<?>[] NO_EXPRESSIONS = new Expression[0];

  private final QueryExecution standardTest =
      new QueryExecution(QuerydslModule.SQL, Connections.getTarget()) {
        @Override
        protected Fetchable<?> createQuery() {
          return testQuery().from(employee, employee2);
        }

        @Override
        protected Fetchable<?> createQuery(Predicate filter) {
          return testQuery().from(employee, employee2).where(filter).select(employee.firstname);
        }
      };

  private <T> T firstResult(Expression<T> expr) {
    return query().select(expr).fetchFirst();
  }

  private Tuple firstResult(Expression<?>... exprs) {
    return query().select(exprs).fetchFirst();
  }

  @Test
  public void aggregate_list() {
    int min = 30000, avg = 65000, max = 160000;
    // fetch
    assertThat(query().from(employee).select(employee.salary.min()).fetch().get(0).intValue())
        .isEqualTo(min);
    assertThat(query().from(employee).select(employee.salary.avg()).fetch().get(0).intValue())
        .isEqualTo(avg);
    assertThat(query().from(employee).select(employee.salary.max()).fetch().get(0).intValue())
        .isEqualTo(max);
  }

  @Test
  public void aggregate_uniqueResult() {
    int min = 30000, avg = 65000, max = 160000;
    // fetchOne
    assertThat(query().from(employee).select(employee.salary.min()).fetchOne().intValue())
        .isEqualTo(min);
    assertThat(query().from(employee).select(employee.salary.avg()).fetchOne().intValue())
        .isEqualTo(avg);
    assertThat(query().from(employee).select(employee.salary.max()).fetchOne().intValue())
        .isEqualTo(max);
  }

  @Test
  @ExcludeIn(ORACLE)
  @SkipForQuoted
  public void alias() {
    expectedQuery = "select e.ID as id from EMPLOYEE e";
    query().from().select(employee.id.as(employee.id)).from(employee).fetch();
  }

  @Test
  @ExcludeIn({MYSQL, ORACLE})
  @SkipForQuoted
  public void alias_quotes() {
    expectedQuery = "select e.FIRSTNAME as \"First Name\" from EMPLOYEE e";
    query().from(employee).select(employee.firstname.as("First Name")).fetch();
  }

  @Test
  @IncludeIn(MYSQL)
  @SkipForQuoted
  public void alias_quotes_MySQL() {
    expectedQuery = "select e.FIRSTNAME as `First Name` from EMPLOYEE e";
    query().from(employee).select(employee.firstname.as("First Name")).fetch();
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
      assertThat(path.getMetadata().getParent()).isEqualTo(survey);
    }
  }

  private void arithmeticTests(
      NumberExpression<Integer> one,
      NumberExpression<Integer> two,
      NumberExpression<Integer> three,
      NumberExpression<Integer> four) {
    assertThat(firstResult(one).intValue()).isEqualTo(1);
    assertThat(firstResult(two).intValue()).isEqualTo(2);
    assertThat(firstResult(four).intValue()).isEqualTo(4);

    assertThat(firstResult(one.subtract(two).add(four)).intValue()).isEqualTo(3);
    assertThat(firstResult(one.subtract(two.add(four))).intValue()).isEqualTo(-5);
    assertThat(firstResult(one.add(two).subtract(four)).intValue()).isEqualTo(-1);
    assertThat(firstResult(one.add(two.subtract(four))).intValue()).isEqualTo(-1);

    assertThat(firstResult(one.add(two).multiply(four)).intValue()).isEqualTo(12);
    assertThat(firstResult(four.multiply(one).divide(two)).intValue()).isEqualTo(2);
    assertThat(firstResult(four.divide(two).multiply(three)).intValue()).isEqualTo(6);
    assertThat(firstResult(four.divide(two.multiply(two))).intValue()).isEqualTo(1);
  }

  @Test
  public void arithmetic() {
    NumberExpression<Integer> one = Expressions.numberTemplate(Integer.class, "(1.0)");
    NumberExpression<Integer> two = Expressions.numberTemplate(Integer.class, "(2.0)");
    NumberExpression<Integer> three = Expressions.numberTemplate(Integer.class, "(3.0)");
    NumberExpression<Integer> four = Expressions.numberTemplate(Integer.class, "(4.0)");
    arithmeticTests(one, two, three, four);
    // the following one doesn't work with integer arguments
    assertThat(firstResult(four.multiply(one.divide(two))).intValue()).isEqualTo(2);
  }

  @Test
  public void arithmetic2() {
    NumberExpression<Integer> one = Expressions.ONE;
    NumberExpression<Integer> two = Expressions.TWO;
    NumberExpression<Integer> three = Expressions.THREE;
    NumberExpression<Integer> four = Expressions.FOUR;
    arithmeticTests(one, two, three, four);
  }

  @Test
  public void arithmetic_mod() {
    NumberExpression<Integer> one = Expressions.numberTemplate(Integer.class, "(1)");
    NumberExpression<Integer> two = Expressions.numberTemplate(Integer.class, "(2)");
    NumberExpression<Integer> three = Expressions.numberTemplate(Integer.class, "(3)");
    NumberExpression<Integer> four = Expressions.numberTemplate(Integer.class, "(4)");

    assertThat(firstResult(four.mod(three).add(three)).intValue()).isEqualTo(4);
    assertThat(firstResult(four.mod(two.add(one))).intValue()).isEqualTo(1);
    assertThat(firstResult(four.mod(two.multiply(one))).intValue()).isEqualTo(0);
    assertThat(firstResult(four.add(one).mod(three)).intValue()).isEqualTo(2);
  }

  @Test
  @IncludeIn(POSTGRESQL) // TODO generalize array literal projections
  public void array() {
    Expression<Integer[]> expr = Expressions.template(Integer[].class, "'{1,2,3}'::int[]");
    Integer[] result = firstResult(expr);
    assertThat(result.length).isEqualTo(3);
    assertThat(result[0].intValue()).isEqualTo(1);
    assertThat(result[1].intValue()).isEqualTo(2);
    assertThat(result[2].intValue()).isEqualTo(3);
  }

  @Test
  @IncludeIn(POSTGRESQL) // TODO generalize array literal projections
  public void array2() {
    Expression<int[]> expr = Expressions.template(int[].class, "'{1,2,3}'::int[]");
    int[] result = firstResult(expr);
    assertThat(result.length).isEqualTo(3);
    assertThat(result[0]).isEqualTo(1);
    assertThat(result[1]).isEqualTo(2);
    assertThat(result[2]).isEqualTo(3);
  }

  @Test
  @ExcludeIn({DERBY, HSQLDB})
  public void array_null() {
    Expression<Integer[]> expr = Expressions.template(Integer[].class, "null");
    assertThat(firstResult(expr)).isNull();
  }

  @Test
  public void array_projection() {
    List<String[]> results =
        query()
            .from(employee)
            .select(new ArrayConstructorExpression<String>(String[].class, employee.firstname))
            .fetch();
    assertThat(results).isNotEmpty();
    for (String[] result : results) {
      assertNotNull(result[0]);
    }
  }

  @Test
  public void beans() {
    List<Beans> rows =
        query().from(employee, employee2).select(new QBeans(employee, employee2)).fetch();
    assertThat(rows).isNotEmpty();
    for (Beans row : rows) {
      assertThat(row.get(employee).getClass()).isEqualTo(Employee.class);
      assertThat(row.get(employee2).getClass()).isEqualTo(Employee.class);
    }
  }

  @Test
  public void between() {
    // 11-13
    assertThat(
            query()
                .from(employee)
                .where(employee.id.between(11, 13))
                .orderBy(employee.id.asc())
                .select(employee.id)
                .fetch())
        .isEqualTo(Arrays.asList(11, 12, 13));
  }

  @Test
  @ExcludeIn({ORACLE, CUBRID, FIREBIRD, DB2, DERBY, SQLSERVER, SQLITE, TERADATA})
  public void boolean_all() {
    assertThat(
            query()
                .from(employee)
                .select(SQLExpressions.all(employee.firstname.isNotNull()))
                .fetchOne())
        .isTrue();
  }

  @Test
  @ExcludeIn({ORACLE, CUBRID, FIREBIRD, DB2, DERBY, SQLSERVER, SQLITE, TERADATA})
  public void boolean_any() {
    assertThat(
            query()
                .from(employee)
                .select(SQLExpressions.any(employee.firstname.isNotNull()))
                .fetchOne())
        .isTrue();
  }

  @Test
  public void case_() {
    NumberExpression<Float> numExpression =
        employee.salary.floatValue().divide(employee2.salary.floatValue()).multiply(100.1);
    NumberExpression<Float> numExpression2 =
        employee.id.when(0).then(0.0F).otherwise(numExpression);
    assertThat(
            query()
                .from(employee, employee2)
                .where(employee.id.eq(employee2.id.add(1)))
                .orderBy(employee.id.asc(), employee2.id.asc())
                .select(numExpression2.floor().intValue())
                .fetch())
        .isEqualTo(Arrays.asList(87, 90, 88, 87, 83, 80, 75));
  }

  @Test
  public void casts() throws SQLException {
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
      for (Object o : query().from(employee).select(expr).fetch()) {
        assertThat(o.getClass()).isEqualTo(expr.getType());
      }
    }
  }

  @Test
  public void coalesce() {
    Coalesce<String> c = new Coalesce<String>(employee.firstname, employee.lastname).add("xxx");
    assertThat(query().from(employee).where(c.getValue().eq("xxx")).select(employee.id).fetch())
        .isEqualTo(Collections.emptyList());
  }

  @Test
  public void compact_join() {
    // verbose
    assertThat(
            query()
                .from(employee)
                .innerJoin(employee2)
                .on(employee.superiorId.eq(employee2.id))
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(8);

    // compact
    assertThat(
            query()
                .from(employee)
                .innerJoin(employee.superiorIdKey, employee2)
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(8);
  }

  @Test
  public void complex_boolean() {
    BooleanExpression first = employee.firstname.eq("Mike").and(employee.lastname.eq("Smith"));
    BooleanExpression second = employee.firstname.eq("Joe").and(employee.lastname.eq("Divis"));
    assertThat(query().from(employee).where(first.or(second)).fetchCount()).isEqualTo(2);

    assertThat(
            query()
                .from(employee)
                .where(
                    employee.firstname.eq("Mike"),
                    employee.lastname.eq("Smith").or(employee.firstname.eq("Joe")),
                    employee.lastname.eq("Divis"))
                .fetchCount())
        .isEqualTo(0);
  }

  @Test
  public void complex_subQuery() {
    // alias for the salary
    NumberPath<BigDecimal> sal = Expressions.numberPath(BigDecimal.class, "sal");
    // alias for the subquery
    PathBuilder<BigDecimal> sq = new PathBuilder<BigDecimal>(BigDecimal.class, "sq");
    // query execution
    query()
        .from(
            query()
                .from(employee)
                .select(employee.salary.add(employee.salary).add(employee.salary).as(sal))
                .as(sq))
        .select(sq.get(sal).avg(), sq.get(sal).min(), sq.get(sal).max())
        .fetch();
  }

  @Test
  public void constructor_projection() {
    for (IdName idAndName :
        query().from(survey).select(new QIdName(survey.id, survey.name)).fetch()) {
      assertNotNull(idAndName);
      assertNotNull(idAndName.getId());
      assertNotNull(idAndName.getName());
    }
  }

  @Test
  public void constructor_projection2() {
    List<SimpleProjection> projections =
        query()
            .from(employee)
            .select(
                Projections.constructor(
                    SimpleProjection.class, employee.firstname, employee.lastname))
            .fetch();
    assertThat(projections).isNotEmpty();
    for (SimpleProjection projection : projections) {
      assertNotNull(projection);
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
    assertThat(query().from(employee).fetchCount()).isEqualTo(10);
  }

  @Test
  public void count_without_pK() {
    assertThat(query().from(QEmployeeNoPK.employee).fetchCount()).isEqualTo(10);
  }

  @Test
  public void count2() {
    assertThat(query().from(employee).select(employee.count()).fetchFirst().intValue())
        .isEqualTo(10);
  }

  @Test
  @SkipForQuoted
  @ExcludeIn(ORACLE)
  public void count_all() {
    expectedQuery = "select count(*) as rc from EMPLOYEE e";
    NumberPath<Long> rowCount = Expressions.numberPath(Long.class, "rc");
    assertThat(query().from(employee).select(Wildcard.count.as(rowCount)).fetchOne().intValue())
        .isEqualTo(10);
  }

  @Test
  @SkipForQuoted
  @IncludeIn(ORACLE)
  public void count_all_Oracle() {
    expectedQuery = "select count(*) rc from EMPLOYEE e";
    NumberPath<Long> rowCount = Expressions.numberPath(Long.class, "rc");
    assertThat(query().from(employee).select(Wildcard.count.as(rowCount)).fetchOne().intValue())
        .isEqualTo(10);
  }

  @Test
  public void count_distinct_with_pK() {
    assertThat(query().from(employee).distinct().fetchCount()).isEqualTo(10);
  }

  @Test
  public void count_distinct_without_pK() {
    assertThat(query().from(QEmployeeNoPK.employee).distinct().fetchCount()).isEqualTo(10);
  }

  @Test
  public void count_distinct2() {
    query().from(employee).select(employee.countDistinct()).fetchFirst();
  }

  @Test
  public void custom_projection() {
    List<Projection> tuples =
        query()
            .from(employee)
            .select(new QProjection(employee.firstname, employee.lastname))
            .fetch();
    assertThat(tuples).isNotEmpty();
    for (Projection tuple : tuples) {
      assertNotNull(tuple.get(employee.firstname));
      assertNotNull(tuple.get(employee.lastname));
      assertNotNull(tuple.getExpr(employee.firstname));
      assertNotNull(tuple.getExpr(employee.lastname));
    }
  }

  @Test
  @ExcludeIn({CUBRID, DB2, DERBY, HSQLDB, POSTGRESQL, SQLITE, TERADATA, H2, FIREBIRD})
  public void dates() throws SQLException {
    if (!configuration.getUseLiterals()) {
      dates(false);
    }
  }

  @Test
  @ExcludeIn({CUBRID, DB2, DERBY, SQLITE, TERADATA, FIREBIRD})
  public void dates_literals() throws SQLException {
    if (configuration.getUseLiterals()) {
      dates(true);
    }
  }

  private void dates(boolean literals) throws SQLException {
    java.time.Instant javaInstant =
        java.time.Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS);
    java.time.LocalDateTime javaDateTime =
        java.time.LocalDateTime.ofInstant(javaInstant, java.time.ZoneId.of("Z"));
    java.time.LocalDate javaDate = javaDateTime.toLocalDate();
    java.time.LocalTime javaTime = javaDateTime.toLocalTime();
    long ts = javaInstant.toEpochMilli();
    long tsDate = javaInstant.truncatedTo(ChronoUnit.DAYS).toEpochMilli();
    long tsTime = javaTime.toNanoOfDay() / 1000;

    List<Object> data = new ArrayList<>();
    data.add(Constants.date);
    data.add(Constants.time);
    data.add(new java.util.Date(ts));
    data.add(new java.util.Date(tsDate));
    data.add(new java.util.Date(tsTime));
    data.add(new java.sql.Timestamp(ts));
    data.add(new java.sql.Timestamp(tsDate));
    data.add(new java.sql.Date(110, 0, 1));
    data.add(new java.sql.Date(tsDate));
    data.add(new java.sql.Time(0, 0, 0));
    data.add(new java.sql.Time(12, 30, 0));
    data.add(new java.sql.Time(23, 59, 59));
    // data.add(new java.sql.Time(tsTime));

    data.add(javaInstant); // java.time.Instant
    data.add(javaDateTime); // java.time.LocalDateTime
    data.add(javaDate); // java.time.LocalDate
    data.add(javaTime); // java.time.LocalTime

    // have to explicitly list these
    // connection.getMetaData().getTypeInfo() is not helpful in this case for most drivers
    boolean supportsTimeZones;
    switch (target) {
      case FIREBIRD:
      case H2:
      case HSQLDB:
      case ORACLE:
      case SQLSERVER:
        supportsTimeZones = true;
        break;
      default:
        supportsTimeZones = false;
        break;
    }
    if (supportsTimeZones) {
      // java.time.OffsetTime
      // SQL Server does not support TIME WITH TIME ZONE
      // H2 supports it starting in 1.4.200 but we are stuck on 1.4.197 due to h2gis
      if (target != SQLSERVER && target != H2) {
        data.add(javaTime.atOffset(java.time.ZoneOffset.UTC));
        data.add(javaTime.atOffset(java.time.ZoneOffset.ofHours(-6)));
      }

      /*
       * TIMESTAMP WITH TIME ZONE is complicated.
       * Contrary to the name, most databases that support this type do not actually support time zones.
       * Instead, they support zone offsets which is not the same thing.
       * E.g. America/New_York could be UTC-5 or UTC-4 depending on the time of year and acts of Congress.
       * Which means if a database actually stores the zone ID, the time value can change.
       * E.g. you put an entry in the database for 10 years in the future but then Congress abolishes DST.
       * Other than Oracle, it does not look like any databases support storing the actual zone ID.
       * Some databases, like H2, support specifying a time zone but convert it to an offset before storing it.
       * Also, the JDBC specification does not say anything about ZonedDateTime.
       * Therefore, we only test ZonedDateTime on Oracle because its behavior is poorly defined on anything else.
       */

      // java.time.OffsetDateTime
      data.add(javaDateTime.atOffset(java.time.ZoneOffset.UTC));
      data.add(javaDateTime.atOffset(java.time.ZoneOffset.ofHours(-6)));

      // java.time.ZonedDateTime
      if (target == ORACLE) {
        data.add(javaDateTime.atZone(java.time.ZoneId.of("UTC")));
        data.add(javaDateTime.atZone(java.time.ZoneId.of("America/Chicago")));
      }
    }

    Map<Object, Object> failures = new IdentityHashMap<>();
    for (Object dt : data) {
      try {
        Object dt2 = firstResult(Expressions.constant(dt));
        if (!dt.equals(dt2)) {
          failures.put(dt, dt2);
        }
      } catch (Exception e) {
        throw new RuntimeException(
            "Error executing query for " + dt.getClass().getName() + " " + dt, e);
      }
    }
    if (!failures.isEmpty()) {
      StringBuilder message = new StringBuilder();
      for (Map.Entry<Object, Object> entry : failures.entrySet()) {
        message
            .append(
                entry.getKey().getClass().getName()
                    + " != "
                    + entry.getValue().getClass().getName()
                    + ": "
                    + entry.getKey()
                    + " != "
                    + entry.getValue())
            .append('\n');
      }
      fail("Failed with " + message);
    }
  }

  @Test
  @ExcludeIn({SQLITE})
  public void date_add() {
    SQLQuery<?> query = query().from(employee);
    Date date1 = query.select(employee.datefield).fetchFirst();
    Date date2 = query.select(SQLExpressions.addYears(employee.datefield, 1)).fetchFirst();
    Date date3 = query.select(SQLExpressions.addMonths(employee.datefield, 1)).fetchFirst();
    Date date4 = query.select(SQLExpressions.addDays(employee.datefield, 1)).fetchFirst();

    assertThat(date2.getTime() > date1.getTime()).isTrue();
    assertThat(date3.getTime() > date1.getTime()).isTrue();
    assertThat(date4.getTime() > date1.getTime()).isTrue();
  }

  @Test
  @ExcludeIn({SQLITE})
  public void date_add_Timestamp() {
    List<Expression<?>> exprs = new ArrayList<>();
    DateTimeExpression<java.util.Date> dt = Expressions.currentTimestamp();

    add(exprs, SQLExpressions.addYears(dt, 1));
    add(exprs, SQLExpressions.addMonths(dt, 1), ORACLE);
    add(exprs, SQLExpressions.addDays(dt, 1));
    add(exprs, SQLExpressions.addHours(dt, 1), TERADATA);
    add(exprs, SQLExpressions.addMinutes(dt, 1), TERADATA);
    add(exprs, SQLExpressions.addSeconds(dt, 1), TERADATA);

    for (Expression<?> expr : exprs) {
      assertNotNull(firstResult(expr));
    }
  }

  @Test
  @ExcludeIn({DB2, SQLITE, TERADATA})
  public void date_diff() {
    QEmployee employee2 = new QEmployee("employee2");
    SQLQuery<?> query = query().from(employee).orderBy(employee.id.asc());
    SQLQuery<?> query2 =
        query().from(employee, employee2).orderBy(employee.id.asc(), employee2.id.desc());

    List<DatePart> dps = new ArrayList<>();
    add(dps, DatePart.year);
    add(dps, DatePart.month);
    add(dps, DatePart.week);
    add(dps, DatePart.day);
    add(dps, DatePart.hour, HSQLDB);
    add(dps, DatePart.minute, HSQLDB);
    add(dps, DatePart.second, HSQLDB);

    LocalDate localDate = LocalDate.of(1970, 1, 10);
    Date date =
        new Date(localDate.atStartOfDay(java.time.ZoneId.of("Z")).toInstant().toEpochMilli());

    for (DatePart dp : dps) {
      int diff1 = query.select(SQLExpressions.datediff(dp, date, employee.datefield)).fetchFirst();
      int diff2 = query.select(SQLExpressions.datediff(dp, employee.datefield, date)).fetchFirst();
      int diff3 =
          query2
              .select(SQLExpressions.datediff(dp, employee.datefield, employee2.datefield))
              .fetchFirst();
      assertThat(-diff2).isEqualTo(diff1);
    }

    Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
    for (DatePart dp : dps) {
      query
          .select(SQLExpressions.datediff(dp, Expressions.currentTimestamp(), timestamp))
          .fetchOne();
    }
  }

  // TDO Date_diff with timestamps

  @Test
  @ExcludeIn({DB2, HSQLDB, SQLITE, TERADATA, ORACLE})
  public void date_diff2() {
    SQLQuery<?> query = query().from(employee).orderBy(employee.id.asc());

    LocalDate localDate = LocalDate.of(1970, 1, 10);
    Date date =
        new Date(localDate.atStartOfDay(java.time.ZoneId.of("Z")).toInstant().toEpochMilli());

    int years =
        query.select(SQLExpressions.datediff(DatePart.year, date, employee.datefield)).fetchFirst();
    int months =
        query
            .select(SQLExpressions.datediff(DatePart.month, date, employee.datefield))
            .fetchFirst();
    // weeks
    int days =
        query.select(SQLExpressions.datediff(DatePart.day, date, employee.datefield)).fetchFirst();
    int hours =
        query.select(SQLExpressions.datediff(DatePart.hour, date, employee.datefield)).fetchFirst();
    int minutes =
        query
            .select(SQLExpressions.datediff(DatePart.minute, date, employee.datefield))
            .fetchFirst();
    int seconds =
        query
            .select(SQLExpressions.datediff(DatePart.second, date, employee.datefield))
            .fetchFirst();

    assertThat(seconds).isEqualTo(949363200);
    assertThat(minutes).isEqualTo(15822720);
    assertThat(hours).isEqualTo(263712);
    assertThat(days).isEqualTo(10988);
    assertThat(months).isEqualTo(361);
    assertThat(years).isEqualTo(30);
  }

  @Test
  @ExcludeIn({SQLITE, H2}) // FIXME
  public void date_trunc() {
    DateTimeExpression<java.util.Date> expr = DateTimeExpression.currentTimestamp();

    List<DatePart> dps = new ArrayList<>();
    add(dps, DatePart.year);
    add(dps, DatePart.month);
    add(dps, DatePart.week, DERBY, FIREBIRD, SQLSERVER);
    add(dps, DatePart.day);
    add(dps, DatePart.hour);
    add(dps, DatePart.minute);
    add(dps, DatePart.second);

    for (DatePart dp : dps) {
      firstResult(SQLExpressions.datetrunc(dp, expr));
    }
  }

  @Test
  @ExcludeIn({SQLITE, TERADATA, DERBY, H2}) // FIXME
  public void date_trunc2() {
    DateTimeExpression<LocalDateTime> expr =
        DateTimeExpression.currentTimestamp(LocalDateTime.class);

    Tuple tuple =
        firstResult(
            expr,
            SQLExpressions.datetrunc(DatePart.year, expr),
            SQLExpressions.datetrunc(DatePart.month, expr),
            SQLExpressions.datetrunc(DatePart.day, expr),
            SQLExpressions.datetrunc(DatePart.hour, expr),
            SQLExpressions.datetrunc(DatePart.minute, expr),
            SQLExpressions.datetrunc(DatePart.second, expr));
    LocalDateTime date = tuple.get(expr);
    LocalDateTime toYear = tuple.get(SQLExpressions.datetrunc(DatePart.year, expr));
    LocalDateTime toMonth = tuple.get(SQLExpressions.datetrunc(DatePart.month, expr));
    LocalDateTime toDay = tuple.get(SQLExpressions.datetrunc(DatePart.day, expr));
    LocalDateTime toHour = tuple.get(SQLExpressions.datetrunc(DatePart.hour, expr));
    LocalDateTime toMinute = tuple.get(SQLExpressions.datetrunc(DatePart.minute, expr));
    LocalDateTime toSecond = tuple.get(SQLExpressions.datetrunc(DatePart.second, expr));

    //    assertEquals(date.getZone(), toYear.getZone());
    //    assertEquals(date.getZone(), toMonth.getZone());
    //    assertEquals(date.getZone(), toDay.getZone());
    //    assertEquals(date.getZone(), toHour.getZone());
    //    assertEquals(date.getZone(), toMinute.getZone());
    //    assertEquals(date.getZone(), toSecond.getZone());

    // year
    assertThat(toYear.getYear()).isEqualTo(date.getYear());
    assertThat(toMonth.getYear()).isEqualTo(date.getYear());
    assertThat(toDay.getYear()).isEqualTo(date.getYear());
    assertThat(toHour.getYear()).isEqualTo(date.getYear());
    assertThat(toMinute.getYear()).isEqualTo(date.getYear());
    assertThat(toSecond.getYear()).isEqualTo(date.getYear());

    // month
    assertThat(toYear.getMonthValue()).isEqualTo(1);
    assertThat(toMonth.getMonthValue()).isEqualTo(date.getMonthValue());
    assertThat(toDay.getMonthValue()).isEqualTo(date.getMonthValue());
    assertThat(toHour.getMonthValue()).isEqualTo(date.getMonthValue());
    assertThat(toMinute.getMonthValue()).isEqualTo(date.getMonthValue());
    assertThat(toSecond.getMonthValue()).isEqualTo(date.getMonthValue());

    // day
    assertThat(toYear.getDayOfMonth()).isEqualTo(1);
    assertThat(toMonth.getDayOfMonth()).isEqualTo(1);
    assertThat(toDay.getDayOfMonth()).isEqualTo(date.getDayOfMonth());
    assertThat(toHour.getDayOfMonth()).isEqualTo(date.getDayOfMonth());
    assertThat(toMinute.getDayOfMonth()).isEqualTo(date.getDayOfMonth());
    assertThat(toSecond.getDayOfMonth()).isEqualTo(date.getDayOfMonth());

    // hour
    assertThat(toYear.getHour()).isEqualTo(0);
    assertThat(toMonth.getHour()).isEqualTo(0);
    assertThat(toDay.getHour()).isEqualTo(0);
    assertThat(toHour.getHour()).isEqualTo(date.getHour());
    assertThat(toMinute.getHour()).isEqualTo(date.getHour());
    assertThat(toSecond.getHour()).isEqualTo(date.getHour());

    // minute
    assertThat(toYear.getMinute()).isEqualTo(0);
    assertThat(toMonth.getMinute()).isEqualTo(0);
    assertThat(toDay.getMinute()).isEqualTo(0);
    assertThat(toHour.getMinute()).isEqualTo(0);
    assertThat(toMinute.getMinute()).isEqualTo(date.getMinute());
    assertThat(toSecond.getMinute()).isEqualTo(date.getMinute());

    // second
    assertThat(toYear.getSecond()).isEqualTo(0);
    assertThat(toMonth.getSecond()).isEqualTo(0);
    assertThat(toDay.getSecond()).isEqualTo(0);
    assertThat(toHour.getSecond()).isEqualTo(0);
    assertThat(toMinute.getSecond()).isEqualTo(0);
    assertThat(toSecond.getSecond()).isEqualTo(date.getSecond());
  }

  @Test
  public void dateTime() {
    SQLQuery<?> query = query().from(employee).orderBy(employee.id.asc());
    assertThat(query.select(employee.datefield.dayOfMonth()).fetchFirst())
        .isEqualTo(Integer.valueOf(10));
    assertThat(query.select(employee.datefield.month()).fetchFirst()).isEqualTo(Integer.valueOf(2));
    assertThat(query.select(employee.datefield.year()).fetchFirst())
        .isEqualTo(Integer.valueOf(2000));
    assertThat(query.select(employee.datefield.yearMonth()).fetchFirst())
        .isEqualTo(Integer.valueOf(200002));
  }

  @Test
  @ExcludeIn({SQLITE})
  public void dateTime_to_date() {
    firstResult(SQLExpressions.date(DateTimeExpression.currentTimestamp()));
  }

  private double degrees(double x) {
    return x * 180.0 / Math.PI;
  }

  @Test
  public void distinct_count() {
    long count1 = query().from(employee).distinct().fetchCount();
    long count2 = query().from(employee).distinct().fetchCount();
    assertThat(count2).isEqualTo(count1);
  }

  @Test
  public void distinct_list() {
    List<Integer> lengths1 =
        query().from(employee).distinct().select(employee.firstname.length()).fetch();
    List<Integer> lengths2 =
        query().from(employee).distinct().select(employee.firstname.length()).fetch();
    assertThat(lengths2).isEqualTo(lengths1);
  }

  @Test
  public void duplicate_columns() {
    assertThat(query().from(employee).select(employee.id, employee.id).fetch()).hasSize(10);
  }

  @Test
  public void duplicate_columns_In_Subquery() {
    QEmployee employee2 = new QEmployee("e2");
    assertThat(
            query()
                .from(employee)
                .where(
                    query()
                        .from(employee2)
                        .where(employee2.id.eq(employee.id))
                        .select(employee2.id, employee2.id)
                        .exists())
                .fetchCount())
        .isEqualTo(10);
  }

  @Test
  public void factoryExpression_in_groupBy() {
    Expression<Employee> empBean =
        Projections.bean(Employee.class, employee.id, employee.superiorId);
    assertThat(query().from(employee).groupBy(empBean).select(empBean).fetchFirst() != null)
        .isTrue();
  }

  @Test
  @ExcludeIn({H2, SQLITE, DERBY, CUBRID, MYSQL})
  public void full_join() throws SQLException {
    assertThat(
            query()
                .from(employee)
                .fullJoin(employee2)
                .on(employee.superiorIdKey.on(employee2))
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(18);
  }

  @Test
  public void getResultSet() throws IOException, SQLException {
    ResultSet results = query().select(survey.id, survey.name).from(survey).getResults();
    while (results.next()) {
      assertNotNull(results.getObject(1));
      assertNotNull(results.getObject(2));
    }
    results.close();
  }

  @Test
  public void groupBy_superior() {
    SQLQuery<?> qry = query().from(employee).innerJoin(employee._superiorIdKey, employee2);

    QTuple subordinates = Projections.tuple(employee2.id, employee2.firstname, employee2.lastname);

    Map<Integer, Group> results =
        qry.transform(
            GroupBy.groupBy(employee.id)
                .as(
                    employee.firstname,
                    employee.lastname,
                    GroupBy.map(employee2.id, subordinates)));

    assertThat(results).hasSize(2);

    // Mike Smith
    Group group = results.get(1);
    assertThat(group.getOne(employee.firstname)).isEqualTo("Mike");
    assertThat(group.getOne(employee.lastname)).isEqualTo("Smith");

    Map<Integer, Tuple> emps = group.getMap(employee2.id, subordinates);
    assertThat(emps).hasSize(4);
    assertThat(emps.get(12).get(employee2.firstname)).isEqualTo("Steve");

    // Mary Smith
    group = results.get(2);
    assertThat(group.getOne(employee.firstname)).isEqualTo("Mary");
    assertThat(group.getOne(employee.lastname)).isEqualTo("Smith");

    emps = group.getMap(employee2.id, subordinates);
    assertThat(emps).hasSize(4);
    assertThat(emps.get(21).get(employee2.lastname)).isEqualTo("Mason");
  }

  @Test
  public void groupBy_yearMonth() {
    assertThat(
            query()
                .from(employee)
                .groupBy(employee.datefield.yearMonth())
                .orderBy(employee.datefield.yearMonth().asc())
                .select(employee.id.count())
                .fetch())
        .isEqualTo(Collections.singletonList(10L));
  }

  @Test
  @ExcludeIn({H2, DB2, DERBY, ORACLE, SQLSERVER})
  public void groupBy_validate() {
    NumberPath<BigDecimal> alias = Expressions.numberPath(BigDecimal.class, "alias");
    assertThat(
            query()
                .from(employee)
                .groupBy(alias)
                .select(employee.salary.multiply(100).as(alias), employee.salary.avg())
                .fetch())
        .hasSize(8);
  }

  @Test
  @ExcludeIn({FIREBIRD})
  public void groupBy_count() {
    List<Integer> ids = query().from(employee).groupBy(employee.id).select(employee.id).fetch();
    long count = query().from(employee).groupBy(employee.id).fetchCount();
    QueryResults<Integer> results =
        query().from(employee).groupBy(employee.id).limit(1).select(employee.id).fetchResults();

    assertThat(ids).hasSize(10);
    assertThat(count).isEqualTo(10);
    assertThat(results.getResults()).hasSize(1);
    assertThat(results.getTotal()).isEqualTo(10);
  }

  @Test
  @ExcludeIn({FIREBIRD, SQLSERVER, TERADATA})
  public void groupBy_Distinct_count() {
    List<Integer> ids =
        query().from(employee).groupBy(employee.id).distinct().select(Expressions.ONE).fetch();
    QueryResults<Integer> results =
        query()
            .from(employee)
            .groupBy(employee.id)
            .limit(1)
            .distinct()
            .select(Expressions.ONE)
            .fetchResults();

    assertThat(ids).hasSize(1);
    assertThat(results.getResults()).hasSize(1);
    assertThat(results.getTotal()).isEqualTo(1);
  }

  @Test
  @ExcludeIn({FIREBIRD})
  public void having_count() {
    // Produces empty resultset https://github.com/querydsl/querydsl/issues/1055
    query()
        .from(employee)
        .innerJoin(employee2)
        .on(employee.id.eq(employee2.id))
        .groupBy(employee.id)
        .having(Wildcard.count.eq(4L))
        .select(employee.id, employee.firstname)
        .fetchResults();
  }

  @SuppressWarnings("unchecked")
  @Test(expected = IllegalArgumentException.class)
  public void illegalUnion() throws SQLException {
    SubQueryExpression<Integer> sq1 = query().from(employee).select(employee.id.max());
    SubQueryExpression<Integer> sq2 = query().from(employee).select(employee.id.max());
    assertThat(query().from(employee).union(sq1, sq2).list()).isEmpty();
  }

  @Test
  public void in() {
    assertThat(
            query()
                .from(employee)
                .where(employee.id.in(Arrays.asList(1, 2)))
                .select(employee)
                .fetch())
        .hasSize(2);
  }

  @Test
  @ExcludeIn({DERBY, FIREBIRD, SQLITE, SQLSERVER, TERADATA})
  public void in_long_list() {
    List<Integer> ids = new ArrayList<>();
    for (int i = 0; i < 20000; i++) {
      ids.add(i);
    }
    assertThat(query().from(employee).where(employee.id.in(ids)).fetchCount())
        .isEqualTo(query().from(employee).fetchCount());
  }

  @Test
  @ExcludeIn({DERBY, FIREBIRD, SQLITE, SQLSERVER, TERADATA})
  public void notIn_long_list() {
    List<Integer> ids = new ArrayList<>();
    for (int i = 0; i < 20000; i++) {
      ids.add(i);
    }
    assertThat(query().from(employee).where(employee.id.notIn(ids)).fetchCount()).isEqualTo(0);
  }

  @Test
  public void in_empty() {
    assertThat(query().from(employee).where(employee.id.in(Collections.emptyList())).fetchCount())
        .isEqualTo(0);
  }

  @Test
  @ExcludeIn(DERBY)
  public void in_null() {
    assertThat(query().from(employee).where(employee.id.in(1, null)).fetchCount()).isEqualTo(1);
  }

  @Test
  @ExcludeIn({MYSQL, TERADATA})
  public void in_subqueries() {
    QEmployee e1 = new QEmployee("e1");
    QEmployee e2 = new QEmployee("e2");
    assertThat(
            query()
                .from(employee)
                .where(
                    employee.id.in(
                        query().from(e1).where(e1.firstname.eq("Mike")).select(e1.id),
                        query().from(e2).where(e2.firstname.eq("Mary")).select(e2.id)))
                .fetchCount())
        .isEqualTo(2);
  }

  @Test
  public void notIn_empty() {
    long count = query().from(employee).fetchCount();
    assertThat(
            query().from(employee).where(employee.id.notIn(Collections.emptyList())).fetchCount())
        .isEqualTo(count);
  }

  @Test
  public void inner_join() throws SQLException {
    assertThat(
            query()
                .from(employee)
                .innerJoin(employee2)
                .on(employee.superiorIdKey.on(employee2))
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(8);
  }

  @Test
  public void inner_join_2Conditions() {
    assertThat(
            query()
                .from(employee)
                .innerJoin(employee2)
                .on(employee.superiorIdKey.on(employee2))
                .on(employee2.firstname.isNotNull())
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(8);
  }

  @Test
  public void join() throws Exception {
    for (String name :
        query().from(survey, survey2).where(survey.id.eq(survey2.id)).select(survey.name).fetch()) {
      assertNotNull(name);
    }
  }

  @Test
  public void joins() throws SQLException {
    for (Tuple row :
        query()
            .from(employee)
            .innerJoin(employee2)
            .on(employee.superiorId.eq(employee2.superiorId))
            .where(employee2.id.eq(10))
            .select(employee.id, employee2.id)
            .fetch()) {
      assertNotNull(row.get(employee.id));
      assertNotNull(row.get(employee2.id));
    }
  }

  @Test
  public void left_join() throws SQLException {
    assertThat(
            query()
                .from(employee)
                .leftJoin(employee2)
                .on(employee.superiorIdKey.on(employee2))
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(10);
  }

  @Test
  public void like() {
    assertThat(query().from(employee).where(employee.firstname.like("\\")).fetchCount())
        .isEqualTo(0);
    assertThat(query().from(employee).where(employee.firstname.like("\\\\")).fetchCount())
        .isEqualTo(0);
  }

  @Test
  public void like_ignore_case() {
    assertThat(query().from(employee).where(employee.firstname.likeIgnoreCase("%m%")).fetchCount())
        .isEqualTo(3);
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
                  > 0)
          .as(str)
          .isTrue();
    }
  }

  @Test
  @ExcludeIn({DB2, DERBY})
  public void like_number() {
    assertThat(query().from(employee).where(employee.id.like("1%")).fetchCount()).isEqualTo(5);
  }

  @Test
  public void limit() throws SQLException {
    assertThat(
            query()
                .from(employee)
                .orderBy(employee.firstname.asc())
                .limit(4)
                .select(employee.id)
                .fetch())
        .isEqualTo(Arrays.asList(23, 22, 21, 20));
  }

  @Test
  public void limit_and_offset() throws SQLException {
    assertThat(
            query()
                .from(employee)
                .orderBy(employee.firstname.asc())
                .limit(4)
                .offset(3)
                .select(employee.id)
                .fetch())
        .isEqualTo(Arrays.asList(20, 13, 10, 2));
  }

  @Test
  public void limit_and_offset_Group() {
    assertThat(
            query()
                .from(employee)
                .orderBy(employee.id.asc())
                .limit(100)
                .offset(1)
                .transform(GroupBy.groupBy(employee.id).as(employee)))
        .hasSize(9);
  }

  @Test
  public void limit_and_offset_and_Order() {
    List<String> names2 = Arrays.asList("Helen", "Jennifer", "Jim", "Joe");
    assertThat(
            query()
                .from(employee)
                .orderBy(employee.firstname.asc())
                .limit(4)
                .offset(2)
                .select(employee.firstname)
                .fetch())
        .isEqualTo(names2);
  }

  @Test
  @IncludeIn(DERBY)
  public void limit_and_offset_In_Derby() throws SQLException {
    expectedQuery = "select e.ID from EMPLOYEE e offset 3 rows fetch next 4 rows only";
    query().from(employee).limit(4).offset(3).select(employee.id).fetch();

    // limit
    expectedQuery = "select e.ID from EMPLOYEE e fetch first 4 rows only";
    query().from(employee).limit(4).select(employee.id).fetch();

    // offset
    expectedQuery = "select e.ID from EMPLOYEE e offset 3 rows";
    query().from(employee).offset(3).select(employee.id).fetch();
  }

  @Test
  @IncludeIn(ORACLE)
  @SkipForQuoted
  public void limit_and_offset_In_Oracle() throws SQLException {
    if (configuration.getUseLiterals()) {
      return;
    }

    // limit
    expectedQuery = "select * from (   select e.ID from EMPLOYEE e ) where rownum <= ?";
    query().from(employee).limit(4).select(employee.id).fetch();

    // offset
    expectedQuery =
        "select * from (  select a.*, rownum rn from (   select e.ID from EMPLOYEE e  ) a) where rn > ?";
    query().from(employee).offset(3).select(employee.id).fetch();

    // limit offset
    expectedQuery =
        "select * from (  select a.*, rownum rn from (   select e.ID from EMPLOYEE e  ) a) where rn > 3 and rownum <= 4";
    query().from(employee).limit(4).offset(3).select(employee.id).fetch();
  }

  @Test
  @ExcludeIn({ORACLE, DB2, DERBY, FIREBIRD, SQLSERVER, CUBRID, TERADATA})
  @SkipForQuoted
  public void limit_and_offset2() throws SQLException {
    // limit
    expectedQuery = "select e.ID from EMPLOYEE e limit ?";
    query().from(employee).limit(4).select(employee.id).fetch();

    // limit offset
    expectedQuery = "select e.ID from EMPLOYEE e limit ? offset ?";
    query().from(employee).limit(4).offset(3).select(employee.id).fetch();
  }

  @Test
  public void limit_and_order() {
    List<String> names1 = Arrays.asList("Barbara", "Daisy", "Helen", "Jennifer");
    assertThat(
            query()
                .from(employee)
                .orderBy(employee.firstname.asc())
                .limit(4)
                .select(employee.firstname)
                .fetch())
        .isEqualTo(names1);
  }

  @Test
  public void listResults() {
    QueryResults<Integer> results =
        query()
            .from(employee)
            .limit(10)
            .offset(1)
            .orderBy(employee.id.asc())
            .select(employee.id)
            .fetchResults();
    assertThat(results.getTotal()).isEqualTo(10);
  }

  @Test
  public void listResults2() {
    QueryResults<Integer> results =
        query()
            .from(employee)
            .limit(2)
            .offset(10)
            .orderBy(employee.id.asc())
            .select(employee.id)
            .fetchResults();
    assertThat(results.getTotal()).isEqualTo(10);
  }

  @Test
  public void listResults_factoryExpression() {
    QueryResults<Employee> results =
        query()
            .from(employee)
            .limit(10)
            .offset(1)
            .orderBy(employee.id.asc())
            .select(employee)
            .fetchResults();
    assertThat(results.getTotal()).isEqualTo(10);
  }

  @Test
  @ExcludeIn({DB2, DERBY})
  public void literals() {
    assertThat(firstResult(ConstantImpl.create(1)).intValue()).isEqualTo(1L);
    assertThat(firstResult(ConstantImpl.create(2L)).longValue()).isEqualTo(2L);
    assertThat(firstResult(ConstantImpl.create(3.0))).isCloseTo(3.0, within(0.001));
    assertThat(firstResult(ConstantImpl.create(4.0f))).isCloseTo(4.0f, within(0.001f));
    assertThat(firstResult(ConstantImpl.create(true))).isEqualTo(true);
    assertThat(firstResult(ConstantImpl.create(false))).isEqualTo(false);
    assertThat(firstResult(ConstantImpl.create("abc"))).isEqualTo("abc");
    assertThat(firstResult(ConstantImpl.create("'"))).isEqualTo("'");
    assertThat(firstResult(ConstantImpl.create("\""))).isEqualTo("\"");
    assertThat(firstResult(ConstantImpl.create("\n"))).isEqualTo("\n");
    assertThat(firstResult(ConstantImpl.create("\r\n"))).isEqualTo("\r\n");
    assertThat(firstResult(ConstantImpl.create("\t"))).isEqualTo("\t");
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
    assertThat(firstResult(StringExpressions.lpad(ConstantImpl.create("ab"), 4))).isEqualTo("  ab");
    assertThat(firstResult(StringExpressions.lpad(ConstantImpl.create("ab"), 4, '!')))
        .isEqualTo("!!ab");
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
    List<Pair<String, String>> pairs =
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
            .fetch();

    for (Pair<String, String> pair : pairs) {
      assertNotNull(pair.getFirst());
      assertNotNull(pair.getSecond());
    }
  }

  @Test
  @ExcludeIn({HSQLDB, SQLITE}) // FIXME
  public void math() {
    math(Expressions.numberTemplate(Double.class, "0.50"));
  }

  @Test
  @ExcludeIn({FIREBIRD, SQLSERVER, HSQLDB, SQLITE}) // FIXME
  public void math2() {
    math(Expressions.constant(0.5));
  }

  private void math(Expression<Double> expr) {
    double precision = 0.001;
    assertThat(firstResult(MathExpressions.acos(expr)))
        .isCloseTo(Math.acos(0.5), within(precision));
    assertThat(firstResult(MathExpressions.asin(expr)))
        .isCloseTo(Math.asin(0.5), within(precision));
    assertThat(firstResult(MathExpressions.atan(expr)))
        .isCloseTo(Math.atan(0.5), within(precision));
    assertThat(firstResult(MathExpressions.cos(expr))).isCloseTo(Math.cos(0.5), within(precision));
    assertThat(firstResult(MathExpressions.cosh(expr)))
        .isCloseTo(Math.cosh(0.5), within(precision));
    assertThat(firstResult(MathExpressions.cot(expr))).isCloseTo(cot(0.5), within(precision));
    if (target != Target.DERBY || expr instanceof Constant) {
      // FIXME: The resulting value is outside the range for the data type DECIMAL/NUMERIC(4,4).
      assertThat(firstResult(MathExpressions.coth(expr))).isCloseTo(coth(0.5), within(precision));
    }

    assertThat(firstResult(MathExpressions.degrees(expr)))
        .isCloseTo(degrees(0.5), within(precision));
    assertThat(firstResult(MathExpressions.exp(expr))).isCloseTo(Math.exp(0.5), within(precision));
    assertThat(firstResult(MathExpressions.ln(expr))).isCloseTo(Math.log(0.5), within(precision));
    assertThat(firstResult(MathExpressions.log(expr, 10)))
        .isCloseTo(log(0.5, 10), within(precision));
    assertThat(firstResult(MathExpressions.power(expr, 2))).isCloseTo(0.25, within(precision));
    assertThat(firstResult(MathExpressions.radians(expr)))
        .isCloseTo(radians(0.5), within(precision));
    assertThat(firstResult(MathExpressions.sign(expr))).isEqualTo(Integer.valueOf(1));
    assertThat(firstResult(MathExpressions.sin(expr))).isCloseTo(Math.sin(0.5), within(precision));
    assertThat(firstResult(MathExpressions.sinh(expr)))
        .isCloseTo(Math.sinh(0.5), within(precision));
    assertThat(firstResult(MathExpressions.tan(expr))).isCloseTo(Math.tan(0.5), within(precision));
    assertThat(firstResult(MathExpressions.tanh(expr)))
        .isCloseTo(Math.tanh(0.5), within(precision));
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
    Double num =
        query()
            .select(one.add(two.multiply(three)).subtract(four.divide(five)).add(six.mod(three)))
            .fetchFirst();
    assertThat(num).isCloseTo(6.2, within(0.001));
  }

  @Test
  public void nested_tuple_projection() {
    Concatenation concat = new Concatenation(employee.firstname, employee.lastname);
    List<Tuple> tuples =
        query().from(employee).select(employee.firstname, employee.lastname, concat).fetch();
    assertThat(tuples).isNotEmpty();
    for (Tuple tuple : tuples) {
      String firstName = tuple.get(employee.firstname);
      String lastName = tuple.get(employee.lastname);
      assertThat(tuple.get(concat)).isEqualTo(firstName + lastName);
    }
  }

  @Test
  @ExcludeIn({SQLITE})
  public void no_from() {
    assertNotNull(firstResult(DateExpression.currentDate()));
  }

  @Test
  public void nullif() {
    query().from(employee).select(employee.firstname.nullif(employee.lastname)).fetch();
  }

  @Test
  public void nullif_constant() {
    query().from(employee).select(employee.firstname.nullif("xxx")).fetch();
  }

  @Test
  public void num_cast() {
    query().from(employee).select(employee.id.castToNum(Long.class)).fetch();
    query().from(employee).select(employee.id.castToNum(Float.class)).fetch();
    query().from(employee).select(employee.id.castToNum(Double.class)).fetch();
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
    long result = query().select(employee.datefield.year().mod(1)).from(employee).fetchFirst();
    assertThat(result).isEqualTo(0);
  }

  @Test
  @ExcludeIn({DERBY, FIREBIRD, POSTGRESQL})
  public void number_as_boolean() {
    QNumberTest numberTest = QNumberTest.numberTest;
    delete(numberTest).execute();
    insert(numberTest).set(numberTest.col1Boolean, true).execute();
    insert(numberTest).set(numberTest.col1Number, (byte) 1).execute();
    assertThat(query().from(numberTest).select(numberTest.col1Boolean).fetch()).hasSize(2);
    assertThat(query().from(numberTest).select(numberTest.col1Number).fetch()).hasSize(2);
  }

  @Test
  public void number_as_boolean_Null() {
    QNumberTest numberTest = QNumberTest.numberTest;
    delete(numberTest).execute();
    insert(numberTest).setNull(numberTest.col1Boolean).execute();
    insert(numberTest).setNull(numberTest.col1Number).execute();
    assertThat(query().from(numberTest).select(numberTest.col1Boolean).fetch()).hasSize(2);
    assertThat(query().from(numberTest).select(numberTest.col1Number).fetch()).hasSize(2);
  }

  @Test
  public void offset_only() {
    assertThat(
            query()
                .from(employee)
                .orderBy(employee.firstname.asc())
                .offset(3)
                .select(employee.id)
                .fetch())
        .isEqualTo(Arrays.asList(20, 13, 10, 2, 1, 11, 12));
  }

  @Test
  public void operation_in_constant_list() {
    assertThat(
            query()
                .from(survey)
                .where(survey.name.charAt(0).in(Collections.singletonList('a')))
                .fetchCount())
        .isEqualTo(0);
    assertThat(
            query()
                .from(survey)
                .where(survey.name.charAt(0).in(Arrays.asList('a', 'b')))
                .fetchCount())
        .isEqualTo(0);
    assertThat(
            query()
                .from(survey)
                .where(survey.name.charAt(0).in(Arrays.asList('a', 'b', 'c')))
                .fetchCount())
        .isEqualTo(0);
  }

  @Test
  public void order_nullsFirst() {
    assertThat(
            query()
                .from(survey)
                .orderBy(survey.name.asc().nullsFirst())
                .select(survey.name)
                .fetch())
        .isEqualTo(Collections.singletonList("Hello World"));
  }

  @Test
  public void order_nullsLast() {
    assertThat(
            query().from(survey).orderBy(survey.name.asc().nullsLast()).select(survey.name).fetch())
        .isEqualTo(Collections.singletonList("Hello World"));
  }

  @Test
  public void params() {
    Param<String> name = new Param<String>(String.class, "name");
    assertThat(
            query()
                .from(employee)
                .where(employee.firstname.eq(name))
                .set(name, "Mike")
                .select(employee.firstname)
                .fetchFirst())
        .isEqualTo("Mike");
  }

  @Test
  public void params_anon() {
    Param<String> name = new Param<String>(String.class);
    assertThat(
            query()
                .from(employee)
                .where(employee.firstname.eq(name))
                .set(name, "Mike")
                .select(employee.firstname)
                .fetchFirst())
        .isEqualTo("Mike");
  }

  @Test(expected = ParamNotSetException.class)
  public void params_not_set() {
    Param<String> name = new Param<String>(String.class, "name");
    assertThat(
            query()
                .from(employee)
                .where(employee.firstname.eq(name))
                .select(employee.firstname)
                .fetchFirst())
        .isEqualTo("Mike");
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

    NumberExpression<BigDecimal> salarySum = employee.salary.sumBigDecimal().as("salarySum");
    query()
        .from(employee)
        .groupBy(employee.lastname)
        .having(salarySum.gt(10000))
        .select(employee.lastname, salarySum)
        .fetch();
  }

  @Test
  public void path_in_constant_list() {
    assertThat(
            query().from(survey).where(survey.name.in(Collections.singletonList("a"))).fetchCount())
        .isEqualTo(0);
    assertThat(query().from(survey).where(survey.name.in(Arrays.asList("a", "b"))).fetchCount())
        .isEqualTo(0);
    assertThat(
            query().from(survey).where(survey.name.in(Arrays.asList("a", "b", "c"))).fetchCount())
        .isEqualTo(0);
  }

  @Test
  public void precedence() {
    StringPath fn = employee.firstname;
    StringPath ln = employee.lastname;
    Predicate where = fn.eq("Mike").and(ln.eq("Smith")).or(fn.eq("Joe").and(ln.eq("Divis")));
    assertThat(query().from(employee).where(where).fetchCount()).isEqualTo(2L);
  }

  @Test
  public void precedence2() {
    StringPath fn = employee.firstname;
    StringPath ln = employee.lastname;
    Predicate where = fn.eq("Mike").and(ln.eq("Smith").or(fn.eq("Joe")).and(ln.eq("Divis")));
    assertThat(query().from(employee).where(where).fetchCount()).isEqualTo(0L);
  }

  @Test
  public void projection() throws IOException {
    CloseableIterator<Tuple> results = query().from(survey).select(survey.all()).iterate();
    assertThat(results.hasNext()).isTrue();
    while (results.hasNext()) {
      assertThat(results.next().size()).isEqualTo(3);
    }
    results.close();
  }

  @Test
  public void projection_and_twoColumns() {
    // projection and two columns
    for (Tuple row :
        query()
            .from(survey)
            .select(new QIdName(survey.id, survey.name), survey.id, survey.name)
            .fetch()) {
      assertThat(row.size()).isEqualTo(3);
      assertThat(row.get(0, Object.class).getClass()).isEqualTo(IdName.class);
      assertThat(row.get(1, Object.class).getClass()).isEqualTo(Integer.class);
      assertThat(row.get(2, Object.class).getClass()).isEqualTo(String.class);
    }
  }

  @Test
  public void projection2() throws IOException {
    CloseableIterator<Tuple> results =
        query().from(survey).select(survey.id, survey.name).iterate();
    assertThat(results.hasNext()).isTrue();
    while (results.hasNext()) {
      assertThat(results.next().size()).isEqualTo(2);
    }
    results.close();
  }

  @Test
  public void projection3() throws IOException {
    CloseableIterator<String> names = query().from(survey).select(survey.name).iterate();
    assertThat(names.hasNext()).isTrue();
    while (names.hasNext()) {
      System.out.println(names.next());
    }
    names.close();
  }

  @Test
  public void qBeanUsage() {
    PathBuilder<Object[]> sq = new PathBuilder<Object[]>(Object[].class, "sq");
    List<Survey> surveys =
        query()
            .from(query().from(survey).select(survey.all()).as("sq"))
            .select(
                Projections.bean(
                    Survey.class, Collections.singletonMap("name", sq.get(survey.name))))
            .fetch();
    assertThat(surveys).isNotEmpty();
  }

  @Test
  public void query_with_constant() throws Exception {
    for (Tuple row :
        query().from(survey).where(survey.id.eq(1)).select(survey.id, survey.name).fetch()) {
      assertNotNull(row.get(survey.id));
      assertNotNull(row.get(survey.name));
    }
  }

  @Test
  public void query1() throws Exception {
    for (String s : query().from(survey).select(survey.name).fetch()) {
      assertNotNull(s);
    }
  }

  @Test
  public void query2() throws Exception {
    for (Tuple row : query().from(survey).select(survey.id, survey.name).fetch()) {
      assertNotNull(row.get(survey.id));
      assertNotNull(row.get(survey.name));
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
    List<Tuple> results =
        query()
            .from(employee, employee2)
            .where(employee.id.eq(employee2.id))
            .select(employee, employee2)
            .fetch();
    assertThat(results).isNotEmpty();
    for (Tuple row : results) {
      Employee e1 = row.get(employee);
      Employee e2 = row.get(employee2);
      assertThat(e2.getId()).isEqualTo(e1.getId());
    }
  }

  @Test
  public void relationalPath_eq() {
    assertThat(
            query()
                .from(employee, employee2)
                .where(employee.eq(employee2))
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(10);
  }

  @Test
  public void relationalPath_ne() {
    assertThat(
            query()
                .from(employee, employee2)
                .where(employee.ne(employee2))
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(90);
  }

  @Test
  public void relationalPath_eq2() {
    assertThat(
            query()
                .from(survey, survey2)
                .where(survey.eq(survey2))
                .select(survey.id, survey2.id)
                .fetch())
        .hasSize(1);
  }

  @Test
  public void relationalPath_ne2() {
    assertThat(
            query()
                .from(survey, survey2)
                .where(survey.ne(survey2))
                .select(survey.id, survey2.id)
                .fetch())
        .isEmpty();
  }

  @Test
  @ExcludeIn(SQLITE)
  public void right_join() throws SQLException {
    assertThat(
            query()
                .from(employee)
                .rightJoin(employee2)
                .on(employee.superiorIdKey.on(employee2))
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(16);
  }

  @Test
  @ExcludeIn(DERBY)
  public void round() {
    Expression<Double> expr = Expressions.numberTemplate(Double.class, "1.32");

    assertThat(firstResult(MathExpressions.round(expr))).isEqualTo(Double.valueOf(1.0));
    assertThat(firstResult(MathExpressions.round(expr, 1))).isEqualTo(Double.valueOf(1.3));
  }

  @Test
  @ExcludeIn({SQLITE, DERBY})
  public void rpad() {
    assertThat(firstResult(StringExpressions.rpad(ConstantImpl.create("ab"), 4))).isEqualTo("ab  ");
    assertThat(firstResult(StringExpressions.rpad(ConstantImpl.create("ab"), 4, '!')))
        .isEqualTo("ab!!");
  }

  @Test
  @Ignore
  @ExcludeIn({ORACLE, DERBY, SQLSERVER})
  public void select_booleanExpr() throws SQLException {
    // TODO : FIXME
    System.out.println(query().from(survey).select(survey.id.eq(0)).fetch());
  }

  @Test
  @Ignore
  @ExcludeIn({ORACLE, DERBY, SQLSERVER})
  public void select_booleanExpr2() throws SQLException {
    // TODO : FIXME
    System.out.println(query().from(survey).select(survey.id.gt(0)).fetch());
  }

  @Test
  public void select_booleanExpr3() {
    assertThat(query().select(Expressions.TRUE).fetchFirst()).isTrue();
    assertThat(query().select(Expressions.FALSE).fetchFirst()).isFalse();
  }

  @Test
  public void select_concat() throws SQLException {
    for (Tuple row :
        query().from(survey).select(survey.name, survey.name.append("Hello World")).fetch()) {
      assertThat(row.get(survey.name.append("Hello World")))
          .isEqualTo(row.get(survey.name) + "Hello World");
    }
  }

  @Test
  @ExcludeIn({SQLITE, CUBRID, TERADATA})
  public void select_for_update() {
    assertThat(query().from(survey).forUpdate().select(survey.id).fetch()).hasSize(1);
  }

  @Test
  @ExcludeIn({SQLITE, CUBRID, TERADATA})
  public void select_for_update_Where() {
    assertThat(
            query().from(survey).forUpdate().where(survey.id.isNotNull()).select(survey.id).fetch())
        .hasSize(1);
  }

  @Test
  @ExcludeIn({SQLITE, CUBRID, TERADATA})
  public void select_for_update_UniqueResult() {
    query().from(survey).forUpdate().select(survey.id).fetchOne();
  }

  @Test
  public void select_for_share() {
    if (configuration.getTemplates().isForShareSupported()) {
      assertThat(
              query()
                  .from(survey)
                  .forShare()
                  .where(survey.id.isNotNull())
                  .select(survey.id)
                  .fetch())
          .hasSize(1);
    } else {
      QueryException e =
          assertThrows(
              QueryException.class,
              () ->
                  query()
                      .from(survey)
                      .forShare()
                      .where(survey.id.isNotNull())
                      .select(survey.id)
                      .fetch()
                      .size());
      assertThat(e.getMessage()).isEqualTo("Using forShare() is not supported");
    }
  }

  @Test
  @SkipForQuoted
  public void serialization() {
    SQLQuery<?> query = query();
    query.from(survey);
    assertThat(query.toString()).isEqualTo("from SURVEY s");
    query.from(survey2);
    assertThat(query.toString()).isEqualTo("from SURVEY s, SURVEY s2");
  }

  @Test
  public void serialization2() throws Exception {
    List<Tuple> rows = query().from(survey).select(survey.id, survey.name).fetch();
    serialize(rows);
  }

  private void serialize(List<Tuple> rows) throws IOException, ClassNotFoundException {
    rows = Serialization.serialize(rows);
    for (Tuple row : rows) {
      row.hashCode();
    }
  }

  @Test
  public void single() {
    assertNotNull(query().from(survey).select(survey.name).fetchFirst());
  }

  @Test
  public void single_array() {
    assertNotNull(query().from(survey).select(new Expression<?>[] {survey.name}).fetchFirst());
  }

  @Test
  public void single_column() {
    // single column
    for (String s : query().from(survey).select(survey.name).fetch()) {
      assertNotNull(s);
    }
  }

  @Test
  public void single_column_via_Object_type() {
    for (Object s :
        query()
            .from(survey)
            .select(ExpressionUtils.path(Object.class, survey.name.getMetadata()))
            .fetch()) {
      assertThat(s.getClass()).isEqualTo(String.class);
    }
  }

  @Test
  public void specialChars() {
    assertThat(
            query().from(survey).where(survey.name.in("\n", "\r", "\\", "\'", "\"")).fetchCount())
        .isEqualTo(0);
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
    Target target = Connections.getTarget();
    if (target != SQLITE) {
      standardTest.runTimeTests(employee.timefield, employee2.timefield, time);
    }

    standardTest.report();
  }

  @Test
  @IncludeIn(H2)
  public void standardTest_turkish() {
    Locale defaultLocale = Locale.getDefault();
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

    assertThat(firstResult(StringExpressions.ltrim(str))).isEqualTo("abcd  ");
    assertThat(firstResult(str.locate("a"))).isEqualTo(Integer.valueOf(3));
    assertThat(firstResult(str.locate("a", 4))).isEqualTo(Integer.valueOf(0));
    assertThat(firstResult(str.locate("b", 2))).isEqualTo(Integer.valueOf(4));
    assertThat(firstResult(StringExpressions.rtrim(str))).isEqualTo("  abcd");
    assertThat(firstResult(str.substring(2, 5))).isEqualTo("abc");
  }

  @Test
  @ExcludeIn(SQLITE)
  public void string_withTemplate() {
    StringExpression str = Expressions.stringTemplate("'  abcd  '");

    NumberExpression<Integer> four = Expressions.numberTemplate(Integer.class, "4");
    NumberExpression<Integer> two = Expressions.numberTemplate(Integer.class, "2");
    NumberExpression<Integer> five = Expressions.numberTemplate(Integer.class, "5");

    assertThat(firstResult(StringExpressions.ltrim(str))).isEqualTo("abcd  ");
    assertThat(firstResult(str.locate("a"))).isEqualTo(Integer.valueOf(3));
    assertThat(firstResult(str.locate("a", four))).isEqualTo(Integer.valueOf(0));
    assertThat(firstResult(str.locate("b", two))).isEqualTo(Integer.valueOf(4));
    assertThat(firstResult(StringExpressions.rtrim(str))).isEqualTo("  abcd");
    assertThat(firstResult(str.substring(two, five))).isEqualTo("abc");
  }

  @Test
  @ExcludeIn({POSTGRESQL, SQLITE})
  public void string_indexOf() {
    StringExpression str = Expressions.stringTemplate("'  abcd  '");

    assertThat(firstResult(str.indexOf("a"))).isEqualTo(Integer.valueOf(2));
    assertThat(firstResult(str.indexOf("a", 4))).isEqualTo(Integer.valueOf(-1));
    assertThat(firstResult(str.indexOf("b", 2))).isEqualTo(Integer.valueOf(3));
  }

  @Test
  public void stringFunctions2() throws SQLException {
    for (BooleanExpression where :
        Arrays.asList(
            employee.firstname.startsWith("a"),
            employee.firstname.startsWithIgnoreCase("a"),
            employee.firstname.endsWith("a"),
            employee.firstname.endsWithIgnoreCase("a"))) {
      query().from(employee).where(where).select(employee.firstname).fetch();
    }
  }

  @Test
  @ExcludeIn(SQLITE)
  public void string_left() {
    assertThat(
            query()
                .from(employee)
                .where(employee.lastname.eq("Johnson"))
                .select(SQLExpressions.left(employee.lastname, 4))
                .fetchFirst())
        .isEqualTo("John");
  }

  @Test
  @ExcludeIn({DERBY, SQLITE})
  public void string_right() {
    assertThat(
            query()
                .from(employee)
                .where(employee.lastname.eq("Johnson"))
                .select(SQLExpressions.right(employee.lastname, 3))
                .fetchFirst())
        .isEqualTo("son");
  }

  @Test
  @ExcludeIn({DERBY, SQLITE})
  public void string_left_Right() {
    assertThat(
            query()
                .from(employee)
                .where(employee.lastname.eq("Johnson"))
                .select(SQLExpressions.right(SQLExpressions.left(employee.lastname, 4), 2))
                .fetchFirst())
        .isEqualTo("hn");
  }

  @Test
  @ExcludeIn({DERBY, SQLITE})
  public void string_right_Left() {
    assertThat(
            query()
                .from(employee)
                .where(employee.lastname.eq("Johnson"))
                .select(SQLExpressions.left(SQLExpressions.right(employee.lastname, 4), 2))
                .fetchFirst())
        .isEqualTo("ns");
  }

  @Test
  @ExcludeIn({DB2, DERBY, FIREBIRD})
  public void substring() {
    // SELECT * FROM account where SUBSTRING(name, -x, 1) = SUBSTRING(name, -y, 1)
    query()
        .from(employee)
        .where(employee.firstname.substring(-3, 1).eq(employee.firstname.substring(-2, 1)))
        .select(employee.id)
        .fetch();
  }

  @Test
  public void syntax_for_employee() throws SQLException {
    assertThat(
            query()
                .from(employee)
                .groupBy(employee.superiorId)
                .orderBy(employee.superiorId.asc())
                .select(employee.salary.avg(), employee.id.max())
                .fetch())
        .hasSize(3);

    assertThat(
            query()
                .from(employee)
                .groupBy(employee.superiorId)
                .having(employee.id.max().gt(5))
                .orderBy(employee.superiorId.asc())
                .select(employee.salary.avg(), employee.id.max())
                .fetch())
        .hasSize(2);

    assertThat(
            query()
                .from(employee)
                .groupBy(employee.superiorId)
                .having(employee.superiorId.isNotNull())
                .orderBy(employee.superiorId.asc())
                .select(employee.salary.avg(), employee.id.max())
                .fetch())
        .hasSize(2);
  }

  @Test
  public void templateExpression() {
    NumberExpression<Integer> one = Expressions.numberTemplate(Integer.class, "1");
    assertThat(query().from(survey).select(one.as("col1")).fetch())
        .isEqualTo(Collections.singletonList(1));
  }

  @Test
  public void transform_groupBy() {
    QEmployee employee = new QEmployee("employee");
    QEmployee employee2 = new QEmployee("employee2");
    Map<Integer, Map<Integer, Employee>> results =
        query()
            .from(employee, employee2)
            .transform(GroupBy.groupBy(employee.id).as(GroupBy.map(employee2.id, employee2)));

    int count = (int) query().from(employee).fetchCount();
    assertThat(results).hasSize(count);
    for (Map.Entry<Integer, Map<Integer, Employee>> entry : results.entrySet()) {
      Map<Integer, Employee> employees = entry.getValue();
      assertThat(employees).hasSize(count);
    }
  }

  @Test
  public void tuple_projection() {
    List<Tuple> tuples =
        query().from(employee).select(employee.firstname, employee.lastname).fetch();
    assertThat(tuples).isNotEmpty();
    for (Tuple tuple : tuples) {
      assertNotNull(tuple.get(employee.firstname));
      assertNotNull(tuple.get(employee.lastname));
    }
  }

  @Test
  @ExcludeIn({DB2, DERBY})
  public void tuple2() {
    assertThat(
            query()
                .from(employee)
                .select(Expressions.as(ConstantImpl.create("1"), "code"), employee.id)
                .fetch())
        .hasSize(10);
  }

  @Test
  public void twoColumns() {
    // two columns
    for (Tuple row : query().from(survey).select(survey.id, survey.name).fetch()) {
      assertThat(row.size()).isEqualTo(2);
      assertThat(row.get(0, Object.class).getClass()).isEqualTo(Integer.class);
      assertThat(row.get(1, Object.class).getClass()).isEqualTo(String.class);
    }
  }

  @Test
  public void twoColumns_and_projection() {
    // two columns and projection
    for (Tuple row :
        query()
            .from(survey)
            .select(survey.id, survey.name, new QIdName(survey.id, survey.name))
            .fetch()) {
      assertThat(row.size()).isEqualTo(3);
      assertThat(row.get(0, Object.class).getClass()).isEqualTo(Integer.class);
      assertThat(row.get(1, Object.class).getClass()).isEqualTo(String.class);
      assertThat(row.get(2, Object.class).getClass()).isEqualTo(IdName.class);
    }
  }

  @Test
  public void unique_Constructor_projection() {
    IdName idAndName =
        query().from(survey).limit(1).select(new QIdName(survey.id, survey.name)).fetchFirst();
    assertNotNull(idAndName);
    assertNotNull(idAndName.getId());
    assertNotNull(idAndName.getName());
  }

  @Test
  public void unique_single() {
    String s = query().from(survey).limit(1).select(survey.name).fetchFirst();
    assertNotNull(s);
  }

  @Test
  public void unique_wildcard() {
    // unique wildcard
    Tuple row = query().from(survey).limit(1).select(survey.all()).fetchFirst();
    assertNotNull(row);
    assertThat(row.size()).isEqualTo(3);
    assertNotNull(row.get(0, Object.class));
    assertNotNull(row.get(1, Object.class), row.get(0, Object.class) + " is not null");
  }

  @Test(expected = NonUniqueResultException.class)
  public void uniqueResultContract() {
    query().from(employee).select(employee.all()).fetchOne();
  }

  @Test
  public void various() throws SQLException {
    for (String s : query().from(survey).select(survey.name.lower()).fetch()) {
      assertThat(s.toLowerCase()).isEqualTo(s);
    }

    for (String s : query().from(survey).select(survey.name.append("abc")).fetch()) {
      assertThat(s).endsWith("abc");
    }

    System.out.println(query().from(survey).select(survey.id.sqrt()).fetch());
  }

  @Test
  public void where_exists() throws SQLException {
    SQLQuery<Integer> sq1 = query().from(employee).select(employee.id.max());
    assertThat(query().from(employee).where(sq1.exists()).fetchCount()).isEqualTo(10);
  }

  @Test
  public void where_exists_Not() throws SQLException {
    SQLQuery<Integer> sq1 = query().from(employee).select(employee.id.max());
    assertThat(query().from(employee).where(sq1.exists().not()).fetchCount()).isEqualTo(0);
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with() {
    assertThat(
            query()
                .with(
                    employee2,
                    query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
                .from(employee, employee2)
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(10);
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with2() {
    QEmployee employee3 = new QEmployee("e3");
    assertThat(
            query()
                .with(
                    employee2,
                    query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
                .with(
                    employee2,
                    query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
                .from(employee, employee2, employee3)
                .select(employee.id, employee2.id, employee3.id)
                .fetch())
        .hasSize(100);
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with3() {
    assertThat(
            query()
                .with(employee2, employee2.all())
                .as(query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
                .from(employee, employee2)
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(10);
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with_limit() {
    assertThat(
            query()
                .with(employee2, employee2.all())
                .as(query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
                .from(employee, employee2)
                .limit(5)
                .orderBy(employee.id.asc(), employee2.id.asc())
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(5);
  }

  @Test
  @IncludeIn({HSQLDB, ORACLE, POSTGRESQL})
  public void with_limitOffset() {
    assertThat(
            query()
                .with(employee2, employee2.all())
                .as(query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
                .from(employee, employee2)
                .limit(10)
                .offset(5)
                .orderBy(employee.id.asc(), employee2.id.asc())
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(5);
  }

  @Test
  @IncludeIn({ORACLE, POSTGRESQL})
  public void with_recursive() {
    assertThat(
            query()
                .withRecursive(
                    employee2,
                    query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
                .from(employee, employee2)
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(10);
  }

  @Test
  @IncludeIn({ORACLE, POSTGRESQL})
  public void with_recursive2() {
    assertThat(
            query()
                .withRecursive(employee2, employee2.all())
                .as(query().from(employee).where(employee.firstname.eq("Jim")).select(Wildcard.all))
                .from(employee, employee2)
                .select(employee.id, employee2.id)
                .fetch())
        .hasSize(10);
  }

  @Test
  public void wildcard() {
    // wildcard
    for (Tuple row : query().from(survey).select(survey.all()).fetch()) {
      assertNotNull(row);
      assertThat(row.size()).isEqualTo(3);
      assertNotNull(row.get(0, Object.class));
      assertNotNull(row.get(1, Object.class), row.get(0, Object.class) + " is not null");
    }
  }

  @Test
  @SkipForQuoted
  public void wildcard_all() {
    expectedQuery = "select * from EMPLOYEE e";
    query().from(employee).select(Wildcard.all).fetch();
  }

  @Test
  public void wildcard_all2() {
    assertThat(
            query()
                .from(
                    new RelationalPathBase<Object>(Object.class, "employee", "public", "EMPLOYEE"))
                .select(Wildcard.all)
                .fetch())
        .hasSize(10);
  }

  @Test
  public void wildcard_and_qTuple() {
    // wildcard and QTuple
    for (Tuple tuple : query().from(survey).select(survey.all()).fetch()) {
      assertNotNull(tuple.get(survey.id));
      assertNotNull(tuple.get(survey.name));
    }
  }

  @Test
  @IncludeIn(ORACLE)
  public void withinGroup() {
    List<WithinGroup<?>> exprs = new ArrayList<WithinGroup<?>>();
    NumberPath<Integer> path = survey.id;

    // two args
    add(exprs, SQLExpressions.cumeDist(2, 3));
    add(exprs, SQLExpressions.denseRank(4, 5));
    add(exprs, SQLExpressions.listagg(path, ","));
    add(exprs, SQLExpressions.percentRank(6, 7));
    add(exprs, SQLExpressions.rank(8, 9));

    for (WithinGroup<?> wg : exprs) {
      query().from(survey).select(wg.withinGroup().orderBy(survey.id, survey.id)).fetch();
      query()
          .from(survey)
          .select(wg.withinGroup().orderBy(survey.id.asc(), survey.id.asc()))
          .fetch();
    }

    // one arg
    exprs.clear();
    add(exprs, SQLExpressions.percentileCont(0.1));
    add(exprs, SQLExpressions.percentileDisc(0.9));

    for (WithinGroup<?> wg : exprs) {
      query().from(survey).select(wg.withinGroup().orderBy(survey.id)).fetch();
      query().from(survey).select(wg.withinGroup().orderBy(survey.id.asc())).fetch();
    }
  }

  @Test
  @ExcludeIn({DB2, DERBY, H2})
  public void yearWeek() {
    SQLQuery<?> query = query().from(employee).orderBy(employee.id.asc());
    assertThat(query.select(employee.datefield.yearWeek()).fetchFirst())
        .isEqualTo(Integer.valueOf(200006));
  }

  @Test
  @IncludeIn({H2})
  public void yearWeek_h2() {
    SQLQuery<?> query = query().from(employee).orderBy(employee.id.asc());
    assertThat(query.select(employee.datefield.yearWeek()).fetchFirst())
        .isEqualTo(Integer.valueOf(200007));
  }

  @Test
  public void statementOptions() {
    StatementOptions options = StatementOptions.builder().setFetchSize(15).setMaxRows(150).build();
    SQLQuery<?> query = query().from(employee).orderBy(employee.id.asc());
    query.setStatementOptions(options);
    query.addListener(
        new SQLBaseListener() {
          public void preExecute(SQLListenerContext context) {
            try {
              assertEquals(15, context.getPreparedStatement().getFetchSize());
              assertEquals(150, context.getPreparedStatement().getMaxRows());
            } catch (SQLException e) {
              throw new RuntimeException(e);
            }
          }
        });
    query.select(employee.id).fetch();
  }

  @Test
  public void getResults() throws SQLException, InterruptedException {
    final AtomicLong endCalled = new AtomicLong(0);
    SQLQuery<Integer> query = query().select(employee.id).from(employee);
    query.addListener(
        new SQLBaseListener() {
          @Override
          public void end(SQLListenerContext context) {
            endCalled.set(System.currentTimeMillis());
          }
        });
    ResultSet results = query.getResults(employee.id);
    long getResultsCalled = System.currentTimeMillis();
    Thread.sleep(100);
    results.close();
    assertThat(endCalled.get() - getResultsCalled >= 100).isTrue();
  }

  @Test
  @ExcludeIn({DB2, DERBY, ORACLE, SQLSERVER})
  public void groupConcat() {
    HashSet<String> expected =
        Sets.newHashSet("Mike,Mary", "Joe,Peter,Steve,Jim", "Jennifer,Helen,Daisy,Barbara");
    assertThat(
            new HashSet<>(
                query()
                    .select(SQLExpressions.groupConcat(employee.firstname))
                    .from(employee)
                    .groupBy(employee.superiorId)
                    .fetch()))
        .isEqualTo(expected);
  }

  @Test
  @ExcludeIn({DB2, DERBY, ORACLE, SQLSERVER})
  public void groupConcat2() {
    HashSet<String> expected =
        Sets.newHashSet("Mike-Mary", "Joe-Peter-Steve-Jim", "Jennifer-Helen-Daisy-Barbara");
    assertThat(
            new HashSet<>(
                query()
                    .select(SQLExpressions.groupConcat(employee.firstname, "-"))
                    .from(employee)
                    .groupBy(employee.superiorId)
                    .fetch()))
        .isEqualTo(expected);
  }
}
// CHECKSTYLERULE:ON: FileLength
