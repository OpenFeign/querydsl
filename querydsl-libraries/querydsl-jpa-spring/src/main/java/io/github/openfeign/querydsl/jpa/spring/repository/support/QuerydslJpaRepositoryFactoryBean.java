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
package io.github.openfeign.querydsl.jpa.spring.repository.support;

import io.github.openfeign.querydsl.jpa.spring.repository.JPQLRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.history.RevisionRepository;

/** {@link FactoryBean} creating {@link JPQLRepository} instances. */
public class QuerydslJpaRepositoryFactoryBean<T extends JPQLRepository<S, ID>, S, ID>
    extends JpaRepositoryFactoryBean<T, S, ID> {

  /**
   * Creates a new {@link QuerydslJpaRepositoryFactoryBean} for the given repository interface.
   *
   * @param repositoryInterface must not be {@literal null}.
   */
  public QuerydslJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
    super(repositoryInterface);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean#createRepositoryFactory(jakarta.persistence.EntityManager)
   */
  @Override
  protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
    return new QuerydslRepositoryFactory<T, ID>(entityManager);
  }

  /** Repository factory creating {@link RevisionRepository} instances. */
  private static class QuerydslRepositoryFactory<T, ID> extends JpaRepositoryFactory {

    private final EntityManager entityManager;

    /**
     * Creates a new {@link QuerydslRepositoryFactory} using the given {@link EntityManager} and
     * revision entity class.
     *
     * @param entityManager must not be {@literal null}.
     */
    public QuerydslRepositoryFactory(EntityManager entityManager) {

      super(entityManager);

      this.entityManager = entityManager;
    }

    @Override
    protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {

      var fragmentImplementation =
          getTargetRepositoryViaReflection( //
              QuerydslJpaRepositoryImpl.class, //
              getEntityInformation(metadata.getDomainType()), //
              entityManager //
              );

      return RepositoryFragments //
          .just(fragmentImplementation) //
          .append(super.getRepositoryFragments(metadata));
    }
  }
}
