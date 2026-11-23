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
package fluentq.jpa.spring.repository.support;

import fluentq.core.Tuple;
import fluentq.core.dml.DeleteClause;
import fluentq.core.dml.InsertClause;
import fluentq.core.dml.UpdateClause;
import fluentq.core.types.EntityPath;
import fluentq.core.types.Expression;
import fluentq.jpa.JPQLQuery;
import fluentq.jpa.impl.JPAQueryFactory;
import fluentq.jpa.spring.repository.JPQLRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/** Repository implementation using query dsl compiled queries */
@Transactional(readOnly = true)
public class FluentQJpaRepositoryImpl<T, ID> implements JPQLRepository<T, ID> {

  private final EntityInformation<T, ?> entityInformation;
  private final EntityManager entityManager;

  private final JPAQueryFactory jpaQueryFactory;

  /**
   * Creates a new {@link FluentQJpaRepositoryImpl} using the given {@link JpaEntityInformation} and
   * {@link EntityManager}.
   *
   * @param entityInformation must not be {@literal null}.
   * @param entityManager must not be {@literal null}.
   */
  public FluentQJpaRepositoryImpl(
      JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {

    Assert.notNull(entityManager, "entityManager must not be null!");

    this.entityInformation = entityInformation;
    this.entityManager = entityManager;
    this.jpaQueryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public DeleteClause<?> delete(EntityPath<T> path) {
    return jpaQueryFactory.delete(path);
  }

  @Override
  public <U> JPQLQuery<U> select(Expression<U> expr) {
    return jpaQueryFactory.select(expr);
  }

  @Override
  public JPQLQuery<Tuple> select(Expression<?>... exprs) {
    return jpaQueryFactory.select(exprs);
  }

  @Override
  public <U> JPQLQuery<U> selectDistinct(Expression<U> expr) {
    return jpaQueryFactory.selectDistinct(expr);
  }

  @Override
  public JPQLQuery<Tuple> selectDistinct(Expression<?>... exprs) {
    return jpaQueryFactory.selectDistinct(exprs);
  }

  @Override
  public JPQLQuery<Integer> selectOne() {
    return jpaQueryFactory.selectOne();
  }

  @Override
  public JPQLQuery<Integer> selectZero() {
    return jpaQueryFactory.selectZero();
  }

  @Override
  public <U> JPQLQuery<U> selectFrom(EntityPath<U> from) {
    return jpaQueryFactory.selectFrom(from);
  }

  @Override
  public UpdateClause<?> update(EntityPath<T> path) {
    return jpaQueryFactory.update(path);
  }

  @Override
  public InsertClause<?> insert(EntityPath<T> path) {
    return jpaQueryFactory.insert(path);
  }
}
