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
package com.querydsl.core.support;

import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import org.jetbrains.annotations.Range;

/**
 * {@code QueryBase} provides a stub for Query implementations
 *
 * @param <Q> concrete subtype
 * @author tiwe
 */
public abstract class QueryBase<Q extends QueryBase<Q>> {

  public static final String MDC_QUERY = "querydsl_query";

  public static final String MDC_PARAMETERS = "querydsl_parameters";

  protected final QueryMixin<Q> queryMixin;

  public QueryBase(QueryMixin<Q> queryMixin) {
    this.queryMixin = queryMixin;
  }

  /**
   * Set the Query to return distinct results
   *
   * @return the current object
   */
  public Q distinct() {
    return queryMixin.distinct();
  }

  /**
   * Add a single grouping expression
   *
   * @param e group by expression
   * @return the current object
   */
  public Q groupBy(Expression<?> e) {
    return queryMixin.groupBy(e);
  }

  /**
   * Add grouping/aggregation expressions
   *
   * @param o group by expressions
   * @return the current object
   */
  public Q groupBy(Expression<?>... o) {
    return queryMixin.groupBy(o);
  }

  /**
   * Add a single filter for aggregation
   *
   * @param e having condition
   * @return the current object
   */
  public Q having(Predicate e) {
    return queryMixin.having(e);
  }

  /**
   * Add filters for aggregation
   *
   * @param o having conditions
   * @return the current object
   */
  public Q having(Predicate... o) {
    return queryMixin.having(o);
  }

  /**
   * Add a single order expression
   *
   * @param o order
   * @return the current object
   */
  public Q orderBy(OrderSpecifier<?> o) {
    return queryMixin.orderBy(o);
  }

  /**
   * Add order expressions
   *
   * @param o order
   * @return the current object
   */
  public Q orderBy(OrderSpecifier<?>... o) {
    return queryMixin.orderBy(o);
  }

  /**
   * Add the given filter condition
   *
   * <p>Skips null arguments
   *
   * @param o filter conditions to be added
   * @return the current object
   */
  public Q where(Predicate o) {
    return queryMixin.where(o);
  }

  /**
   * Add the given filter conditions
   *
   * <p>Skips null arguments
   *
   * @param o filter conditions to be added
   * @return the current object
   */
  public Q where(Predicate... o) {
    return queryMixin.where(o);
  }

  /**
   * Defines the limit / max results for the query results
   *
   * @param limit max rows
   * @return the current object
   */
  public Q limit(@Range(from = 0, to = Integer.MAX_VALUE) long limit) {
    return queryMixin.limit(limit);
  }

  /**
   * Defines the offset for the query results
   *
   * @param offset row offset
   * @return the current object
   */
  public Q offset(long offset) {
    return queryMixin.offset(offset);
  }

  /**
   * Defines both limit and offset of the query results, use {@link QueryModifiers#EMPTY} to apply
   * no paging.
   *
   * @param modifiers query modifiers
   * @return the current object
   */
  public Q restrict(QueryModifiers modifiers) {
    return queryMixin.restrict(modifiers);
  }

  /**
   * Set the given parameter to the given value
   *
   * @param <P>
   * @param param param
   * @param value binding
   * @return the current object
   */
  public <P> Q set(ParamExpression<P> param, P value) {
    return queryMixin.set(param, value);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof QueryBase) {
      QueryBase q = (QueryBase) o;
      return q.queryMixin.equals(queryMixin);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return queryMixin.hashCode();
  }

  @Override
  public String toString() {
    return queryMixin.toString();
  }
}
