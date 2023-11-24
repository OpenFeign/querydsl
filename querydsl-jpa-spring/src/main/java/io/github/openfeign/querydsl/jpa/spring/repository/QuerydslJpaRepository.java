/*
 * Copyright 2008-2023 the original author or authors.
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
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

/**
 * QueryDSL JPA specific extension of {@link org.springframework.data.repository.Repository}.
 *
 * @author Marvin Froeder
 */
@NoRepositoryBean
public interface QuerydslJpaRepository<T, ID> extends Repository<T, ID> {

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
  JPQLQuery<T> select(Expression<T> expr);

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
  JPQLQuery<T> selectDistinct(Expression<T> expr);

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
  JPQLQuery<T> selectFrom(EntityPath<T> from);

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
}
