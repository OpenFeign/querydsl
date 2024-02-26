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

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.Query;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.*;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Default implementation of the Union interface
 *
 * @param <T> result type
 * @param <Q> concrete query type
 * @author mc_fish
 */
public class UnionImpl<T, Q extends ProjectableR2DBCQuery<T, Q> & Query<Q>> implements Union<T> {

  private final Q query;

  public UnionImpl(Q query) {
    this.query = query;
  }

  @Override
  public Flux<T> list() {
    return query.fetch();
  }

  @Override
  public Flux<T> fetch() {
    return query.fetch();
  }

  @Override
  public Mono<T> fetchFirst() {
    return query.fetchFirst();
  }

  @Override
  public Mono<T> fetchOne() throws NonUniqueResultException {
    return query.fetchOne();
  }

  @Override
  public Mono<Long> fetchCount() {
    return query.fetchCount();
  }

  @Override
  public Union<T> groupBy(Expression<?>... o) {
    query.groupBy(o);
    return this;
  }

  @Override
  public Union<T> having(Predicate... o) {
    query.having(o);
    return this;
  }

  @Override
  public Union<T> orderBy(OrderSpecifier<?>... o) {
    query.orderBy(o);
    return this;
  }

  @Override
  public Expression<T> as(String alias) {
    return ExpressionUtils.as(this, alias);
  }

  @Override
  public Expression<T> as(Path<T> alias) {
    return ExpressionUtils.as(this, alias);
  }

  @Override
  public String toString() {
    return query.toString();
  }

  @Nullable
  @Override
  public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
    return query.accept(v, context);
  }

  @Override
  public Class<? extends T> getType() {
    return query.getType();
  }

  @Override
  public QueryMetadata getMetadata() {
    return query.getMetadata();
  }
}
