/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.openfeign.querydsl.jpa.spring.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.JPQLSubQuery;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/** A repository which can access {@link JPQLQueryFactory} methods */
@NoRepositoryBean
public interface JPQLRepository<T, ID> extends Repository<T, ID> {

  /**
   * Create a new DELETE clause
   *
   * @param path entity to delete from
   * @return delete clause
   */
  DeleteClause<?> delete(EntityPath<T> path);

  /**
   * Create a new JPQLQuery instance with the given projection
   *
   * @param expr projection
   * @param <T>
   * @return select(expr)
   */
  <U> JPQLQuery<U> select(Expression<U> expr);

  /**
   * Create a new JPQLQuery instance with the given projection
   *
   * @param exprs projection
   * @return select(exprs)
   */
  JPQLQuery<Tuple> select(Expression<?>... exprs);

  /**
   * Create a new JPQLQuery instance with the given projection
   *
   * @param expr projection
   * @param <T>
   * @return select(distinct expr)
   */
  <U> JPQLQuery<U> selectDistinct(Expression<U> expr);

  /**
   * Create a new JPQLQuery instance with the given projection
   *
   * @param exprs projection
   * @return select(distinct exprs)
   */
  JPQLQuery<Tuple> selectDistinct(Expression<?>... exprs);

  /**
   * Create a new JPQLQuery instance with the projection one
   *
   * @return select(1)
   */
  JPQLQuery<Integer> selectOne();

  /**
   * Create a new JPQLQuery instance with the projection zero
   *
   * @return select(0)
   */
  JPQLQuery<Integer> selectZero();

  /**
   * Create a new JPQLQuery instance with the given source and projection
   *
   * @param from projection and source
   * @param <T>
   * @return select(from).from(from)
   */
  <U> JPQLQuery<U> selectFrom(EntityPath<U> from);

  /**
   * Create a new UPDATE clause
   *
   * @param path entity to update
   * @return update clause
   */
  UpdateClause<?> update(EntityPath<T> path);

  /**
   * Create a new INSERT clause
   *
   * @param path entity to insert to
   * @return insert clause
   */
  InsertClause<?> insert(EntityPath<T> path);

  /**
   * Create a new detached JPQLQuery instance with the given projection
   *
   * @param expr projection
   * @param <T>
   * @return select(expr)
   */
  default <U> JPQLSubQuery<U> subSelect(Expression<U> expr) {
    return JPAExpressions.select(expr);
  }

  /**
   * Create a new detached JPQLQuery instance with the given projection
   *
   * @param exprs projection
   * @return select(exprs)
   */
  default JPQLSubQuery<Tuple> subSelect(Expression<?>... exprs) {
    return JPAExpressions.select(exprs);
  }

  /**
   * Create a new detached JPQLQuery instance with the given projection
   *
   * @param expr projection
   * @param <U>
   * @return select(distinct expr)
   */
  default <U> JPQLSubQuery<U> subSelectDistinct(Expression<U> expr) {
    return JPAExpressions.select(expr).distinct();
  }

  /**
   * Create a new detached JPQLQuery instance with the given projection
   *
   * @param exprs projection
   * @return select(distinct expr)
   */
  default JPQLSubQuery<Tuple> subSelectDistinct(Expression<?>... exprs) {
    return JPAExpressions.select(exprs).distinct();
  }

  /**
   * Create a new detached JPQLQuery instance with the projection zero
   *
   * @return select(0)
   */
  default JPQLSubQuery<Integer> subSelectZero() {
    return subSelect(Expressions.ZERO);
  }

  /**
   * Create a new detached JPQLQuery instance with the projection one
   *
   * @return select(1)
   */
  default JPQLSubQuery<Integer> subSelectOne() {
    return subSelect(Expressions.ONE);
  }

  /**
   * Create a new detached JPQLQuery instance with the given projection
   *
   * @param expr projection and source
   * @param <U>
   * @return select(expr).from(expr)
   */
  default <U> JPQLSubQuery<U> subSelectFrom(EntityPath<U> expr) {
    return subSelect(expr).from(expr);
  }
}
