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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.beans.factory.FactoryBean} to create {@link QuerydslRepository}
 * instances.
 *
 * @author Alex Shvid
 * @author John Blum
 * @author Oliver Gierke
 * @author Mark Paluch
 */
public class QuerydslRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
    extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> {

  private @Nullable EntityManager entityManager;
  private EntityPathResolver entityPathResolver;
  private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;
  private JpaQueryMethodFactory queryMethodFactory;

  /**
   * Creates a new {@link JpaRepositoryFactoryBean} for the given repository interface.
   *
   * @param repositoryInterface must not be {@literal null}.
   */
  public QuerydslRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
    super(repositoryInterface);
  }

  /**
   * The {@link EntityManager} to be used.
   *
   * @param entityManager the entityManager to set
   */
  @PersistenceContext
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void setMappingContext(MappingContext<?, ?> mappingContext) {
    super.setMappingContext(mappingContext);
  }

  /**
   * Configures the {@link EntityPathResolver} to be used. Will expect a canonical bean to be
   * present but fallback to {@link SimpleEntityPathResolver#INSTANCE} in case none is available.
   *
   * @param resolver must not be {@literal null}.
   */
  @Autowired
  public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
    this.entityPathResolver = resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
  }

  /**
   * Configures the {@link JpaQueryMethodFactory} to be used. Will expect a canonical bean to be
   * present but will fallback to {@link
   * org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory} in case none is
   * available.
   *
   * @param factory may be {@literal null}.
   */
  @Autowired
  public void setQueryMethodFactory(@Nullable JpaQueryMethodFactory factory) {

    if (factory != null) {
      this.queryMethodFactory = factory;
    }
  }

  @Override
  protected RepositoryFactorySupport doCreateRepositoryFactory() {

    Assert.state(entityManager != null, "EntityManager must not be null");

    return createRepositoryFactory(entityManager);
  }

  /** Returns a {@link RepositoryFactorySupport}. */
  protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

    QuerydslRepositoryFactory jpaRepositoryFactory = new QuerydslRepositoryFactory(entityManager);
    //	jpaRepositoryFactory.setEntityPathResolver(entityPathResolver);
    //	jpaRepositoryFactory.setEscapeCharacter(escapeCharacter);
    //
    //	if (queryMethodFactory != null) {
    //		jpaRepositoryFactory.setQueryMethodFactory(queryMethodFactory);
    //	}

    return jpaRepositoryFactory;
  }

  @Override
  public void afterPropertiesSet() {

    Assert.state(entityManager != null, "EntityManager must not be null");

    super.afterPropertiesSet();
  }

  public void setEscapeCharacter(char escapeCharacter) {

    this.escapeCharacter = EscapeCharacter.of(escapeCharacter);
  }
}
