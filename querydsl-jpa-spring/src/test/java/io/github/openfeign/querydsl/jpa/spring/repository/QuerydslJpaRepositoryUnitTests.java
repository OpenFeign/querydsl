/*
 * Copyright 2021-2023 the original author or authors.
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
package io.github.openfeign.querydsl.jpa.spring.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.github.openfeign.querydsl.jpa.spring.repository.support.QUnitTestPerson;
import io.github.openfeign.querydsl.jpa.spring.repository.support.QuerydslJpaRepositoryFactory;
import io.github.openfeign.querydsl.jpa.spring.repository.support.UnitTestPerson;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.odm.core.impl.DefaultObjectDirectoryMapper;
import org.springframework.ldap.query.LdapQuery;

/**
 * Unit tests for {@link QuerydslJpaRepository}.
 *
 * @author Mark Paluch
 */
@MockitoSettings
class QuerydslJpaRepositoryUnitTests {

  @Mock LdapOperations ldapOperations;

  UnitTestPerson walter, hank;

  PersonRepository repository;

  @BeforeEach
  void before() throws Exception {

    when(ldapOperations.getObjectDirectoryMapper()).thenReturn(new DefaultObjectDirectoryMapper());

    walter =
        new UnitTestPerson(new LdapName("cn=walter"), "Walter", "White", "US", "Heisenberg", "000");
    hank = new UnitTestPerson(new LdapName("cn=hank"), "Hank", "Schrader", "US", "DEA", "000");

    repository =
        new QuerydslJpaRepositoryFactory(ldapOperations).getRepository(PersonRepository.class);
  }

  @Test
  void shouldReturnInterfaceProjection() {

    when(ldapOperations.findOne(any(LdapQuery.class), eq(UnitTestPerson.class))).thenReturn(walter);

    PersonProjection walter = repository.findByLastName("White");

    assertThat(walter).isNotNull();
    assertThat(walter.getLastName()).isEqualTo("White");

    ArgumentCaptor<LdapQuery> captor = ArgumentCaptor.forClass(LdapQuery.class);

    verify(ldapOperations).findOne(captor.capture(), any());

    LdapQuery query = captor.getValue();
    assertThat(query.attributes()).containsOnly("lastName");
  }

  @Test
  void shouldReturnDynamicDtoProjection() {

    when(ldapOperations.findOne(any(LdapQuery.class), eq(UnitTestPerson.class))).thenReturn(walter);

    PersonDto walter = repository.findByLastName("White", PersonDto.class);

    assertThat(walter).isNotNull();
    assertThat(walter.lastName()).isEqualTo("White");

    ArgumentCaptor<LdapQuery> captor = ArgumentCaptor.forClass(LdapQuery.class);

    verify(ldapOperations).findOne(captor.capture(), any());

    LdapQuery query = captor.getValue();
    assertThat(query.attributes()).contains("lastName");
  }

  @Test
  void shouldReturnInterfaceProjectionAsStream() {

    when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class)))
        .thenReturn(Collections.singletonList(walter));

    Stream<UnitTestPerson> walter = repository.streamAllByLastName("White");

    List<UnitTestPerson> list = walter.collect(Collectors.toList());
    assertThat(list).hasSize(1).hasOnlyElementsOfType(PersonProjection.class);

    ArgumentCaptor<LdapQuery> captor = ArgumentCaptor.forClass(LdapQuery.class);

    verify(ldapOperations).find(captor.capture(), any());

    LdapQuery query = captor.getValue();
    assertThat(query.attributes()).containsOnly("lastName");
  }

  interface PersonRepository extends QuerydslJpaRepository<UnitTestPerson, Name> {

    default Stream<UnitTestPerson> streamAllByLastName(String lastName) {
      return selectFrom(QUnitTestPerson.unitTestPerson)
          .where(QUnitTestPerson.unitTestPerson.lastName.eq(lastName))
          .stream();
    }

    PersonProjection findByLastName(String lastname);

    <T> T findByLastName(String lastname, Class<T> projection);
  }

  interface PersonProjection {
    String getLastName();
  }

  record PersonDto(String lastName) {}
}
