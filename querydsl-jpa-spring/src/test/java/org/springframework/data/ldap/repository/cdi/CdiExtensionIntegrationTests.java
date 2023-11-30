/*
 * Copyright 2017-2023 the original author or authors.
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
package org.springframework.data.ldap.repository.cdi;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import java.util.Collections;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.ldap.config.DummyEntity;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;

/**
 * Integration tests for {@link LdapRepositoryExtension}.
 *
 * @author Mark Paluch
 */
class CdiExtensionIntegrationTests {

  private static SeContainer container;

  @BeforeAll
  static void setUp() {

    container =
        SeContainerInitializer.newInstance() //
            .disableDiscovery() //
            .addPackages(CdiExtensionIntegrationTests.class) //
            .initialize();
  }

  @AfterAll
  static void tearDown() {
    container.close();
  }

  @Test // DATALDAP-5
  void bootstrapsRepositoryCorrectly() {

    RepositoryClient client = container.select(RepositoryClient.class).get();
    LdapTemplate ldapTemplateMock = client.getLdapTemplate();

    DummyEntity entity = new DummyEntity();
    when(ldapTemplateMock.findAll(DummyEntity.class)).thenReturn(Collections.singletonList(entity));

    SampleRepository repository = client.getSampleRepository();

    assertThat(repository).isNotNull();

    repository.findAll(LdapQueryBuilder.query().filter("*"));
  }

  @Test // DATALDAP-5
  void returnOneFromCustomImpl() {

    RepositoryClient repositoryConsumer = container.select(RepositoryClient.class).get();
    assertThat(repositoryConsumer.getSampleRepository().returnOne()).isEqualTo(1);
  }
}
