/*
 * Copyright 2018-2023 the original author or authors.
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
package io.github.openfeign.querydsl.jpa.spring.repository.config;

import static org.junit.Assert.*;

import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository;
import java.util.Collection;
import javax.naming.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.ldap.odm.annotations.Entry;

/**
 * Unit tests for {@link QuerydslJpaRepositoryConfigurationExtension}.
 *
 * @author Mark Paluch
 */
class QuerydslJpaRepositoryConfigurationExtensionUnitTests {

  private StandardAnnotationMetadata metadata = new StandardAnnotationMetadata(Config.class, true);
  private ResourceLoader loader = new PathMatchingResourcePatternResolver();
  private Environment environment = new StandardEnvironment();
  private BeanDefinitionRegistry registry = new DefaultListableBeanFactory();

  private RepositoryConfigurationSource configurationSource =
      new AnnotationRepositoryConfigurationSource(
          metadata, EnableLdapRepositories.class, loader, environment, registry);

  @Test // DATALDAP-60
  void isStrictMatchIfDomainTypeIsAnnotatedWithEntry() {

    QuerydslJpaRepositoryConfigurationExtension extension =
        new QuerydslJpaRepositoryConfigurationExtension();
    assertHasRepo(
        SampleRepository.class,
        extension.getRepositoryConfigurations(configurationSource, loader, true));
  }

  @Test // DATALDAP-60
  void isStrictMatchIfRepositoryExtendsStoreSpecificBase() {

    QuerydslJpaRepositoryConfigurationExtension extension =
        new QuerydslJpaRepositoryConfigurationExtension();
    assertHasRepo(
        StoreRepository.class,
        extension.getRepositoryConfigurations(configurationSource, loader, true));
  }

  @Test // DATALDAP-60
  void isNotStrictMatchIfDomainTypeIsNotAnnotatedWithEntry() {

    QuerydslJpaRepositoryConfigurationExtension extension =
        new QuerydslJpaRepositoryConfigurationExtension();
    assertDoesNotHaveRepo(
        UnannotatedRepository.class,
        extension.getRepositoryConfigurations(configurationSource, loader, true));
  }

  private static void assertHasRepo(
      Class<?> repositoryInterface,
      Collection<RepositoryConfiguration<RepositoryConfigurationSource>> configs) {

    for (RepositoryConfiguration<?> config : configs) {
      if (config.getRepositoryInterface().equals(repositoryInterface.getName())) {
        return;
      }
    }

    fail(
        "Expected to find config for repository interface "
            .concat(repositoryInterface.getName())
            .concat(" but got ")
            .concat(configs.toString()));
  }

  private static void assertDoesNotHaveRepo(
      Class<?> repositoryInterface,
      Collection<RepositoryConfiguration<RepositoryConfigurationSource>> configs) {

    for (RepositoryConfiguration<?> config : configs) {
      if (config.getRepositoryInterface().equals(repositoryInterface.getName())) {
        fail(
            "Expected not to find config for repository interface "
                .concat(repositoryInterface.getName()));
      }
    }
  }

  @EnableLdapRepositories(considerNestedRepositories = true)
  private static class Config {}

  @Entry(objectClasses = "person")
  static class Sample {}

  interface SampleRepository extends Repository<Sample, Long> {}

  interface UnannotatedRepository extends Repository<Object, Long> {}

  interface StoreRepository extends QuerydslJpaRepository<Object, Name> {}
}
