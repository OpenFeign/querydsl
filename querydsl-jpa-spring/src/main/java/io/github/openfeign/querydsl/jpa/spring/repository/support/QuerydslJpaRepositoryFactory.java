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

import static org.springframework.data.querydsl.QuerydslUtils.*;
import static org.springframework.data.repository.core.support.RepositoryComposition.*;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Factory to create {@link
 * io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository} instances.
 *
 * @author Mattias Hellborg Arthursson
 * @author Eddu Melendez
 * @author Mark Paluch
 * @author Jens Schauder
 */
public class QuerydslJpaRepositoryFactory extends RepositoryFactorySupport {

  private final EntityManager entityManager;
  private final EntityInstantiators instantiators = new EntityInstantiators();

  /**
   * Creates a new {@link QuerydslJpaRepositoryFactory}.
   *
   * @param ldapOperations must not be {@literal null}.
   */
  public QuerydslJpaRepositoryFactory(EntityManager entityManager) {

    Assert.notNull(entityManager, "entityManager must not be null");

    this.entityManager = entityManager;
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
    return new LdapEntityInformation(domainClass);
  }

  @Override
  protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
    return SimpleQuerydslJpaRepository.class;
  }

  @Override
  protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
    return RepositoryFragments.empty();
  }

  @Override
  protected Object getTargetRepository(RepositoryInformation information) {
    return getTargetRepositoryViaReflection(
        information, information.getDomainType(), entityManager);
  }

  @Override
  protected Optional<QueryLookupStrategy> getQueryLookupStrategy(
      @Nullable Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
    return Optional.empty();
  }
}
