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
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the {@link QuerydslJpaRepository} interface. This will offer you easy
 * access to {@link JPQLQuery}
 *
 * @param <T> the type of the entity to handle
 * @param <ID> the type of the entity's identifier
 * @author Marvin Froeder
 */
@Repository
@Transactional(readOnly = true)
public class QuerydslJpaRepositoryImpl<T, ID> implements QuerydslJpaRepository<T, ID> {

  private final JPQLQueryFactory factory;

  /**
   * Creates a new {@link SimpleJpaRepository} to manage objects of the given {@link
   * JpaEntityInformation}.
   *
   * @param entityInformation must not be {@literal null}.
   * @param entityManager must not be {@literal null}.
   */
  public QuerydslJpaRepositoryImpl(EntityManager entityManager) {
    factory = new JPAQueryFactory(entityManager);
  }

  @Override
  public JPQLQuery<T> select(Expression<T> expr) {
    return factory.select(expr);
  }

  @Override
  public DeleteClause<?> delete(EntityPath<T> path) {
    return factory.delete(path);
  }

  @Override
  public JPQLQuery<Tuple> select(Expression<?>... exprs) {
    return factory.select(exprs);
  }

  @Override
  public JPQLQuery<T> selectDistinct(Expression<T> expr) {
    return factory.selectDistinct(expr);
  }

  @Override
  public JPQLQuery<Tuple> selectDistinct(Expression<?>... exprs) {
    return factory.selectDistinct(exprs);
  }

  @Override
  public JPQLQuery<Integer> selectOne() {
    return factory.selectOne();
  }

  @Override
  public JPQLQuery<Integer> selectZero() {
    return factory.selectZero();
  }

  @Override
  public JPQLQuery<T> selectFrom(EntityPath<T> from) {
    return factory.selectFrom(from);
  }

  @Override
  public UpdateClause<?> update(EntityPath<T> path) {
    return factory.update(path);
  }

  @Override
  public InsertClause<?> insert(EntityPath<T> path) {
    return factory.insert(path);
  }
}
