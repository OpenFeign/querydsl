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
package com.querydsl.core;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ArrayExpression;
import com.querydsl.core.types.dsl.CollectionExpressionBase;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.ListExpression;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.MapExpressionBase;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.TimeExpression;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author tiwe
 */
public class MatchingFiltersFactory {

  private final QuerydslModule module;

  private final Target target;

  public MatchingFiltersFactory(QuerydslModule module, Target target) {
    this.module = module;
    this.target = target;
  }

  public <A> Collection<Predicate> array(
      ArrayExpression<A[], A> expr,
      ArrayExpression<A[], A> other,
      A knownElement,
      A missingElement) {
    var rv = new HashSet<Predicate>();
    //        rv.add(expr.isEmpty().not());
    if (!module.equals(QuerydslModule.RDFBEAN)) {
      rv.add(expr.size().gt(0));
    }
    return Collections.unmodifiableSet(rv);
  }

  public <A> Collection<Predicate> collection(
      CollectionExpressionBase<?, A> expr,
      CollectionExpression<?, A> other,
      A knownElement,
      A missingElement) {
    var rv = new HashSet<Predicate>();
    if (!module.equals(QuerydslModule.RDFBEAN)) {
      rv.add(expr.contains(knownElement));
      rv.add(expr.contains(missingElement).not());
    }
    rv.add(expr.isEmpty().not());
    rv.add(expr.isNotEmpty());
    return Collections.unmodifiableSet(rv);
  }

  @SuppressWarnings("unchecked")
  private <A extends Comparable> Collection<Predicate> comparable(
      ComparableExpression<A> expr, Expression<A> other) {
    var rv = new HashSet<Predicate>();
    rv.add(expr.eq(other));
    rv.add(expr.goe(other));
    rv.add(expr.loe(other));
    rv.add(expr.ne(other).not());
    return Collections.unmodifiableSet(rv);
  }

  public Collection<Predicate> date(
      DateExpression<java.sql.Date> expr, DateExpression<java.sql.Date> other) {
    var rv = new HashSet<>(comparable(expr, other));
    rv.add(expr.dayOfMonth().eq(other.dayOfMonth()));

    if (!target.equals(Target.DERBY)
        && !target.equals(Target.ORACLE)
        && !target.equals(Target.FIREBIRD)
        && (!target.equals(Target.POSTGRESQL) || !module.equals(QuerydslModule.JPA))) {
      rv.add(expr.dayOfWeek().eq(other.dayOfWeek()));
      rv.add(expr.dayOfYear().eq(other.dayOfYear()));

      if (!target.equals(Target.SQLSERVER)
          && !target.equals(Target.MYSQL)
          && !target.equals(Target.CUBRID)
          && !target.equals(Target.POSTGRESQL)
          && !target.equals(Target.HSQLDB)) {
        rv.add(expr.week().eq(other.week()));
      }
    }

    rv.add(expr.month().eq(other.month()));
    rv.add(expr.year().eq(other.year()));
    rv.add(expr.yearMonth().eq(other.yearMonth()));
    return Collections.unmodifiableSet(rv);
  }

  public Collection<Predicate> date(
      DateExpression<java.sql.Date> expr,
      DateExpression<java.sql.Date> other,
      java.sql.Date knownValue) {
    var rv = new HashSet<>(date(expr, other));
    rv.addAll(date(expr, DateConstant.create(knownValue)));
    return Collections.unmodifiableSet(rv);
  }

  public Collection<Predicate> dateTime(
      DateTimeExpression<java.util.Date> expr, DateTimeExpression<java.util.Date> other) {
    var rv = new HashSet<>(comparable(expr, other));
    rv.add(expr.milliSecond().eq(other.milliSecond()));
    rv.add(expr.second().eq(other.second()));
    rv.add(expr.minute().eq(other.minute()));
    rv.add(expr.hour().eq(other.hour()));
    rv.add(expr.dayOfMonth().eq(other.dayOfMonth()));

    if (!target.equals(Target.DERBY)
        && !target.equals(Target.ORACLE)
        && (!target.equals(Target.POSTGRESQL) || !module.equals(QuerydslModule.JPA))) {
      rv.add(expr.dayOfWeek().eq(other.dayOfWeek()));
      rv.add(expr.dayOfYear().eq(other.dayOfYear()));

      if (!target.equals(Target.SQLSERVER)
          && !target.equals(Target.MYSQL)
          && !target.equals(Target.CUBRID)
          && !target.equals(Target.POSTGRESQL)
          && !target.equals(Target.HSQLDB)) {
        rv.add(expr.week().eq(other.week()));
      }
    }

    rv.add(expr.month().eq(other.month()));
    rv.add(expr.year().eq(other.year()));
    rv.add(expr.yearMonth().eq(other.yearMonth()));
    return Collections.unmodifiableSet(rv);
  }

  public Collection<Predicate> dateTime(
      DateTimeExpression<java.util.Date> expr,
      DateTimeExpression<java.util.Date> other,
      java.util.Date knownValue) {
    var rv = new HashSet<>(dateTime(expr, other));
    rv.addAll(dateTime(expr, DateTimeConstant.create(knownValue)));
    return Collections.unmodifiableSet(rv);
  }

  public <A, Q extends SimpleExpression<A>> Collection<Predicate> list(
      ListPath<A, Q> expr, ListExpression<A, Q> other, A knownElement, A missingElement) {
    return collection(expr, other, knownElement, missingElement);
  }

  public <K, V> Collection<Predicate> map(
      MapExpressionBase<K, V, ?> expr,
      MapExpression<K, V> other,
      K knownKey,
      V knownValue,
      K missingKey,
      V missingValue) {
    var rv = new HashSet<Predicate>();
    rv.add(expr.containsKey(knownKey));
    rv.add(expr.containsKey(missingKey).not());
    rv.add(expr.containsValue(knownValue));
    rv.add(expr.containsValue(missingValue).not());
    rv.add(expr.get(knownKey).eq(knownValue));
    rv.add(expr.isEmpty().not());
    rv.add(expr.isNotEmpty());
    return Collections.unmodifiableSet(rv);
  }

  public <A extends Number & Comparable<A>> Collection<Predicate> numeric(
      NumberExpression<A> expr, NumberExpression<A> other, A knownValue) {
    var rv = new HashSet<Predicate>(numeric(expr, other));
    rv.addAll(numeric(expr, NumberConstant.create(knownValue)));
    return Collections.unmodifiableSet(rv);
  }

  public <A extends Number & Comparable<A>> Collection<Predicate> numeric(
      NumberExpression<A> expr, NumberExpression<A> other) {
    var rv = new HashSet<Predicate>();
    rv.add(expr.eq(other));
    rv.add(expr.goe(other));
    rv.add(expr.gt(other.subtract(1)));
    rv.add(expr.gt(other.subtract(2)));
    rv.add(expr.loe(other));
    rv.add(expr.lt(other.add(1)));
    rv.add(expr.lt(other.add(2)));
    rv.add(expr.ne(other).not());
    return Collections.unmodifiableSet(rv);
  }

  public Collection<Predicate> string(StringExpression expr, StringExpression other) {
    var rv = new HashSet<Predicate>();
    rv.addAll(comparable(expr, other));

    rv.add(expr.charAt(0).eq(other.charAt(0)));
    rv.add(expr.charAt(1).eq(other.charAt(1)));

    rv.add(expr.contains(other));
    rv.add(expr.contains(other.substring(0, 1)));
    rv.add(expr.contains(other.substring(0, 2)));
    rv.add(expr.contains(other.substring(1, 2)));
    rv.add(expr.contains(other.substring(1)));
    rv.add(expr.contains(other.substring(2)));

    rv.add(expr.containsIgnoreCase(other));
    rv.add(expr.containsIgnoreCase(other.lower()));
    rv.add(expr.containsIgnoreCase(other.upper()));
    rv.add(expr.containsIgnoreCase(other.substring(0, 1)));
    rv.add(expr.containsIgnoreCase(other.substring(0, 2).lower()));
    rv.add(expr.containsIgnoreCase(other.substring(1, 2).upper()));
    rv.add(expr.containsIgnoreCase(other.substring(1).lower()));
    rv.add(expr.containsIgnoreCase(other.substring(2).upper()));

    rv.add(expr.endsWith(other));
    rv.add(expr.endsWith(other.substring(1)));
    rv.add(expr.endsWith(other.substring(2)));

    rv.add(expr.endsWithIgnoreCase(other));
    rv.add(expr.endsWithIgnoreCase(other.substring(1)));
    rv.add(expr.endsWithIgnoreCase(other.substring(2)));

    rv.add(expr.eq(other));
    rv.add(expr.equalsIgnoreCase(other));

    rv.add(expr.indexOf(other).eq(0));
    rv.add(expr.locate(other).eq(1));

    if (target != Target.DERBY) {
      rv.add(expr.indexOf(other.substring(1)).eq(1));
      rv.add(expr.indexOf(other.substring(2)).eq(2));
    }

    rv.add(expr.isEmpty().not());
    rv.add(expr.isNotEmpty());

    rv.add(expr.length().eq(other.length()));
    rv.add(expr.like(other));

    if (other instanceof Constant<?>) {
      rv.add(expr.like(other.substring(0, 1).append("%")));
      rv.add(expr.like(other.substring(0, 1).append("%").append(other.substring(2))));
      rv.add(expr.like(other.substring(1).prepend("%")));
      rv.add(expr.like(other.substring(1, 2).append("%").prepend("%")));
    }

    rv.add(expr.lower().eq(other.lower()));

    if (!module.equals(QuerydslModule.SQL)
        || (!target.equals(Target.HSQLDB)
            && !target.equals(Target.FIREBIRD)
            && !target.equals(Target.H2)
            && !target.equals(Target.DB2)
            && !target.equals(Target.DERBY)
            && !target.equals(Target.SQLITE)
            && !target.equals(Target.SQLSERVER))) {

      rv.add(expr.matches(other));

      if (other instanceof Constant<?>) {
        rv.add(expr.matches(other.substring(0, 1).append(".*")));
        rv.add(expr.matches(other.substring(0, 1).append(".").append(other.substring(2))));
        rv.add(expr.matches(other.substring(1).prepend(".*")));
        rv.add(expr.matches(other.substring(1, 2).prepend(".*").append(".*")));
      }
    }

    rv.add(expr.ne(other));

    rv.add(expr.startsWith(other));
    rv.add(expr.startsWith(other.substring(0, 1)));
    rv.add(expr.startsWith(other.substring(0, 2)));

    rv.add(expr.startsWithIgnoreCase(other));
    rv.add(expr.startsWithIgnoreCase(other.substring(0, 1)));
    rv.add(expr.startsWithIgnoreCase(other.substring(0, 2)));

    rv.add(expr.substring(0, 1).eq(other.substring(0, 1)));
    rv.add(expr.substring(1, 2).eq(other.substring(1, 2)));
    rv.add(expr.substring(1).eq(other.substring(1)));

    rv.add(expr.trim().eq(other.trim()));

    rv.add(expr.upper().eq(other.upper()));
    return Collections.unmodifiableSet(rv);
  }

  public Collection<Predicate> string(
      StringExpression expr, StringExpression other, String knownValue) {
    var rv = new HashSet<Predicate>(string(expr, other));
    rv.addAll(string(expr, StringConstant.create(knownValue)));
    return Collections.unmodifiableSet(rv);
  }

  public Collection<Predicate> time(
      TimeExpression<java.sql.Time> expr, TimeExpression<java.sql.Time> other) {
    var rv = new HashSet<>(comparable(expr, other));
    rv.add(expr.milliSecond().eq(other.milliSecond()));
    rv.add(expr.second().eq(other.second()));
    rv.add(expr.minute().eq(other.minute()));
    rv.add(expr.hour().eq(other.hour()));
    return Collections.unmodifiableSet(rv);
  }

  public Collection<Predicate> time(
      TimeExpression<java.sql.Time> expr,
      TimeExpression<java.sql.Time> other,
      java.sql.Time knownValue) {
    var rv = new HashSet<>(time(expr, other));
    rv.addAll(time(expr, TimeConstant.create(knownValue)));
    return Collections.unmodifiableSet(rv);
  }
}
