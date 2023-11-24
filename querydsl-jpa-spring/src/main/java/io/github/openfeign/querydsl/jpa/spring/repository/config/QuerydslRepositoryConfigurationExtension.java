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
package io.github.openfeign.querydsl.jpa.spring.repository.config;

import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository;
import io.github.openfeign.querydsl.jpa.spring.repository.support.QuerydslRepositoryFactoryBean;
import java.util.Collection;
import java.util.Collections;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.data.repository.config.AotRepositoryContext;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryRegistrationAotProcessor;
import org.springframework.data.repository.core.RepositoryMetadata;

/**
 * {@link RepositoryConfigurationExtension} for Querydsl.
 *
 * @author Marvin Froeder
 */
public class QuerydslRepositoryConfigurationExtension
    extends RepositoryConfigurationExtensionSupport {

  @Override
  public String getModuleName() {
    return "Querydsl";
  }

  @Override
  protected String getModulePrefix() {
    return "Querydsl";
  }

  @Override
  public String getRepositoryFactoryBeanClassName() {
    return QuerydslRepositoryFactoryBean.class.getName();
  }

  @Override
  protected Collection<Class<?>> getIdentifyingTypes() {
    return Collections.singleton(QuerydslJpaRepository.class);
  }

  @Override
  protected boolean useRepositoryConfiguration(RepositoryMetadata metadata) {
    return !metadata.isReactiveRepository();
  }

  @Override
  public Class<? extends BeanRegistrationAotProcessor> getRepositoryAotProcessor() {
    return QuerydslRepositoryRegistrationAotProcessor.class;
  }

  public static class QuerydslRepositoryRegistrationAotProcessor
      extends RepositoryRegistrationAotProcessor {

    protected void contribute(
        AotRepositoryContext repositoryContext, GenerationContext generationContext) {
      // don't register domain types nor annotations.
    }
  }
}
