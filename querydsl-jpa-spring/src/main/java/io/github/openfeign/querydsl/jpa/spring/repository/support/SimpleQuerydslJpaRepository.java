/*
 * Copyright 2016-2023 the original author or authors.
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
package io.github.openfeign.querydsl.jpa.spring.repository.support;

import static org.springframework.ldap.query.LdapQueryBuilder.*;

import com.querydsl.core.Tuple;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.util.Assert;

/**
 * Base repository implementation for LDAP.
 *
 * @author Mattias Hellborg Arthursson
 * @author Mark Paluch
 * @author Jens Schauder
 */
public class SimpleQuerydslJpaRepository<T, ID> implements QuerydslJpaRepository<T, ID> {

  private static final String OBJECTCLASS_ATTRIBUTE = "objectclass";

  private final Class<T> entityType;

  private final JPAQueryFactory jpaQueryFactory;

  /**
   * Creates a new {@link SimpleQuerydslJpaRepository}.
   *
   * @param ldapOperations must not be {@literal null}.
   * @param odm must not be {@literal null}.
   * @param entityType must not be {@literal null}.
   * @param entityManager
   */
  public SimpleQuerydslJpaRepository(Class<T> entityType, EntityManager entityManager) {

    Assert.notNull(entityType, "Entity type must not be null");
    Assert.notNull(entityManager, "entityManager must not be null");

    this.entityType = entityType;
    this.jpaQueryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public DeleteClause<?> delete(EntityPath<T> path) {
    return jpaQueryFactory.delete(path);
  }

  @Override
  public JPQLQuery<T> select(Expression<T> expr) {
    return jpaQueryFactory.select(expr);
  }

  @Override
  public JPQLQuery<Tuple> select(Expression<?>... exprs) {
    return jpaQueryFactory.select(exprs);
  }

  @Override
  public JPQLQuery<T> selectDistinct(Expression<T> expr) {
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
  public JPQLQuery<T> selectFrom(EntityPath<T> from) {
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
