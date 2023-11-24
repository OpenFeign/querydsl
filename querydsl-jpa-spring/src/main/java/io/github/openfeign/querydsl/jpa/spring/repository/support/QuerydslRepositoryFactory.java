/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.openfeign.querydsl.jpa.spring.repository.support;

import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

/**
 * Factory to create {@link QuerydslRepository} instances.
 *
 * @author Alex Shvid
 * @author Matthew T. Adams
 * @author Thomas Darimont
 * @author Mark Paluch
 * @author John Blum
 */
public class QuerydslRepositoryFactory extends RepositoryFactorySupport {

  private static final SpelExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
  private EntityManager entityManager;

  /**
   * Create a new {@link QuerydslRepositoryFactory} with the given {@link QuerydslOperations}.
   *
   * @param operations must not be {@literal null}
   */
  public QuerydslRepositoryFactory(EntityManager entityManager) {

    Assert.notNull(entityManager, "entityManager must not be null");

    this.entityManager = entityManager;
  }

  @Override
  protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
    return QuerydslJpaRepositoryImpl.class;
  }

  @Override
  protected Object getTargetRepository(RepositoryInformation information) {
    return getTargetRepositoryViaReflection(information, entityManager);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T, ID> JpaEntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {

    return (JpaEntityInformation<T, ID>)
        JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
  }
}
