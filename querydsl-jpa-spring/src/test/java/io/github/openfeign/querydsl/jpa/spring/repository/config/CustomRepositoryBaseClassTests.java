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
package io.github.openfeign.querydsl.jpa.spring.repository.config;

import static org.assertj.core.api.Assertions.*;

import io.github.openfeign.querydsl.jpa.spring.config.DummyEntity;
import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository;
import io.github.openfeign.querydsl.jpa.spring.repository.support.SimpleQuerydslJpaRepository;
import javax.naming.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * Unit tests for {@link EnableLdapRepositories#repositoryBaseClass()}.
 *
 * @author Mark Paluch
 */
@SpringJUnitConfig(classes = CustomRepositoryBaseClassTests.Config.class)
class CustomRepositoryBaseClassTests {

  @Autowired ApplicationContext applicationContext;

  @Test // DATALDAP-2
  void shouldImplementCustomizedRepository() {

    CustomizedDummyRepository repository =
        applicationContext.getBean(CustomizedDummyRepository.class);
    assertThat(repository.returnOne()).isEqualTo(1);
  }

  @Configuration
  @ImportResource("classpath:/infrastructure.xml")
  @EnableLdapRepositories(
      considerNestedRepositories = true,
      repositoryBaseClass = CustomizedLdapRepositoryImpl.class)
  static class Config {}

  interface CustomizedDummyRepository extends CustomizedLdapRepository<DummyEntity> {}

  @NoRepositoryBean
  interface CustomizedLdapRepository<T> extends QuerydslJpaRepository<T, Name> {

    int returnOne();
  }

  static class CustomizedLdapRepositoryImpl<T> extends SimpleQuerydslJpaRepository<T, Name>
      implements CustomizedLdapRepository<T> {

    public CustomizedLdapRepositoryImpl(
        LdapOperations ldapOperations, ObjectDirectoryMapper odm, Class<T> clazz) {
      super(ldapOperations, odm, clazz);
    }

    @Override
    public int returnOne() {
      return 1;
    }
  }
}
