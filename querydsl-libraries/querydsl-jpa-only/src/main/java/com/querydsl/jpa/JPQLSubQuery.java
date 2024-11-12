package com.querydsl.jpa;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import java.util.Collection;

public interface JPQLSubQuery<T> extends SubQueryExpression<T> {

  JPQLSubQuery<T> clone();

  <U> JPQLSubQuery<U> select(Expression<U> expr);

  JPQLSubQuery<Tuple> select(Expression<?>... exprs);

  JPQLSubQuery<T> fetchJoin();

  JPQLSubQuery<T> from(EntityPath<?> arg);

  JPQLSubQuery<T> from(EntityPath<?>... args);

  <P> JPQLSubQuery<T> from(CollectionExpression<?, P> target, Path<P> alias);

  <P> JPQLSubQuery<T> innerJoin(CollectionExpression<?, P> target);

  <P> JPQLSubQuery<T> innerJoin(CollectionExpression<?, P> target, Path<P> alias);

  <P> JPQLSubQuery<T> innerJoin(EntityPath<P> target);

  <P> JPQLSubQuery<T> innerJoin(EntityPath<P> target, Path<P> alias);

  <P> JPQLSubQuery<T> innerJoin(MapExpression<?, P> target);

  <P> JPQLSubQuery<T> innerJoin(MapExpression<?, P> target, Path<P> alias);

  <P> JPQLSubQuery<T> join(CollectionExpression<?, P> target);

  <P> JPQLSubQuery<T> join(CollectionExpression<?, P> target, Path<P> alias);

  <P> JPQLSubQuery<T> join(EntityPath<P> target);

  <P> JPQLSubQuery<T> join(EntityPath<P> target, Path<P> alias);

  <P> JPQLSubQuery<T> join(MapExpression<?, P> target);

  <P> JPQLSubQuery<T> join(MapExpression<?, P> target, Path<P> alias);

  <P> JPQLSubQuery<T> leftJoin(CollectionExpression<?, P> target);

  <P> JPQLSubQuery<T> leftJoin(CollectionExpression<?, P> target, Path<P> alias);

  <P> JPQLSubQuery<T> leftJoin(EntityPath<P> target);

  <P> JPQLSubQuery<T> leftJoin(EntityPath<P> target, Path<P> alias);

  <P> JPQLSubQuery<T> leftJoin(MapExpression<?, P> target);

  <P> JPQLSubQuery<T> leftJoin(MapExpression<?, P> target, Path<P> alias);

  <P> JPQLSubQuery<T> rightJoin(CollectionExpression<?, P> target);

  <P> JPQLSubQuery<T> rightJoin(CollectionExpression<?, P> target, Path<P> alias);

  <P> JPQLSubQuery<T> rightJoin(EntityPath<P> target);

  <P> JPQLSubQuery<T> rightJoin(EntityPath<P> target, Path<P> alias);

  <P> JPQLSubQuery<T> rightJoin(MapExpression<?, P> target);

  <P> JPQLSubQuery<T> rightJoin(MapExpression<?, P> target, Path<P> alias);

  JPQLSubQuery<T> on(Predicate condition);

  JPQLSubQuery<T> on(Predicate... conditions);

  BooleanExpression contains(Expression<? extends T> right);

  BooleanExpression contains(T constant);

  BooleanExpression exists();

  BooleanExpression eq(Expression<? extends T> expr);

  BooleanExpression eq(T constant);

  BooleanExpression ne(Expression<? extends T> expr);

  BooleanExpression ne(T constant);

  BooleanExpression notExists();

  BooleanExpression lt(Expression<? extends T> expr);

  BooleanExpression lt(T constant);

  BooleanExpression gt(Expression<? extends T> expr);

  BooleanExpression gt(T constant);

  BooleanExpression loe(Expression<? extends T> expr);

  BooleanExpression loe(T constant);

  BooleanExpression goe(Expression<? extends T> expr);

  BooleanExpression goe(T constant);

  BooleanOperation isNull();

  BooleanOperation isNotNull();

  @Override
  QueryMetadata getMetadata();

  @Override
  <R, C> R accept(Visitor<R, C> v, C context);

  @Override
  Class<T> getType();

  BooleanExpression in(Collection<? extends T> right);

  BooleanExpression in(T... right);

  /**
   * Set the Query to return distinct results
   *
   * @return the current object
   */
  JPQLSubQuery<T> distinct();

  /**
   * Add a single grouping expression
   *
   * @param e group by expression
   * @return the current object
   */
  JPQLSubQuery<T> groupBy(Expression<?> e);

  /**
   * Add grouping/aggregation expressions
   *
   * @param o group by expressions
   * @return the current object
   */
  JPQLSubQuery<T> groupBy(Expression<?>... o);

  /**
   * Add a single filter for aggregation
   *
   * @param e having condition
   * @return the current object
   */
  JPQLSubQuery<T> having(Predicate e);

  /**
   * Add filters for aggregation
   *
   * @param o having conditions
   * @return the current object
   */
  JPQLSubQuery<T> having(Predicate... o);

  /**
   * Add a single order expression
   *
   * @param o order
   * @return the current object
   */
  JPQLSubQuery<T> orderBy(OrderSpecifier<?> o);

  /**
   * Add order expressions
   *
   * @param o order
   * @return the current object
   */
  JPQLSubQuery<T> orderBy(OrderSpecifier<?>... o);

  /**
   * Add the given filter condition
   *
   * <p>Skips null arguments
   *
   * @param o filter conditions to be added
   * @return the current object
   */
  JPQLSubQuery<T> where(Predicate o);

  /**
   * Add the given filter conditions
   *
   * <p>Skips null arguments
   *
   * @param o filter conditions to be added
   * @return the current object
   */
  JPQLSubQuery<T> where(Predicate... o);

  /**
   * Defines the limit / max results for the query results
   *
   * @param limit max rows
   * @return the current object
   */
  JPQLSubQuery<T> limit(long limit);

  /**
   * Defines the offset for the query results
   *
   * @param offset row offset
   * @return the current object
   */
  JPQLSubQuery<T> offset(long offset);

  /**
   * Defines both limit and offset of the query results, use {@link QueryModifiers#EMPTY} to apply
   * no paging.
   *
   * @param modifiers query modifiers
   * @return the current object
   */
  JPQLSubQuery<T> restrict(QueryModifiers modifiers);

  /**
   * Set the given parameter to the given value
   *
   * @param <P>
   * @param param param
   * @param value binding
   * @return the current object
   */
  <P> JPQLSubQuery<T> set(ParamExpression<P> param, P value);
}
