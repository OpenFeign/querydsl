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
package org.springframework.data.ldap.repository.support;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.naming.ldap.LdapName;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.ldap.core.mapping.LdapMappingContext;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.odm.core.impl.DefaultObjectDirectoryMapper;
import org.springframework.ldap.query.LdapQuery;

/**
 * Unit tests for {@link QuerydslLdapPredicateExecutor}.
 *
 * @author Mark Paluch
 */
@MockitoSettings
class QuerydslLdapPredicateExecutorUnitTests {

	@Mock LdapOperations ldapOperations;

	UnitTestPerson walter, hank;

	QuerydslLdapPredicateExecutor<UnitTestPerson> repository;

	@BeforeEach
	void before() throws Exception {
		when(ldapOperations.getObjectDirectoryMapper()).thenReturn(new DefaultObjectDirectoryMapper());
		repository = new QuerydslLdapPredicateExecutor<>(UnitTestPerson.class, new SpelAwareProxyProjectionFactory(),
				ldapOperations, new LdapMappingContext());

		walter = new UnitTestPerson(new LdapName("cn=walter"), "Walter", "White", Collections.emptyList(), "US",
				"Heisenberg", "000");

		hank = new UnitTestPerson(new LdapName("cn=hank"), "Hank", "Schrader", Collections.emptyList(), "US", "DEA", "000");

	}

	@Test // GH-269
	void findByShouldReturnFirst() {

		when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class))).thenReturn(Arrays.asList(walter, hank),
				Collections.emptyList());

		UnitTestPerson first = repository.findBy(QPerson.person.fullName.eq("Walter"),
				FluentQuery.FetchableFluentQuery::firstValue);

		assertThat(first).isEqualTo(walter);

		first = repository.findBy(QPerson.person.fullName.eq("Walter"), Function.identity()).firstValue();

		assertThat(first).isNull();
	}

	@Test // GH-269
	void findByShouldReturnOne() {

		when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class))).thenReturn(Arrays.asList(walter, hank),
				Collections.singletonList(walter));

		assertThatExceptionOfType(IncorrectResultSizeDataAccessException.class).isThrownBy(
				() -> repository.findBy(QPerson.person.fullName.eq("Walter"), FluentQuery.FetchableFluentQuery::one));

		UnitTestPerson one = repository.findBy(QPerson.person.fullName.eq("Walter"),
				FluentQuery.FetchableFluentQuery::oneValue);

		assertThat(one).isEqualTo(walter);
	}

	@Test // GH-269
	void findByShouldReturnFirstWithProjection() {

		when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class))).thenReturn(Arrays.asList(walter));

		PersonProjection interfaceProjection = repository.findBy(QPerson.person.fullName.eq("Walter"),
				it -> it.as(PersonProjection.class).firstValue());
		assertThat(interfaceProjection.getLastName()).isEqualTo("White");

		PersonDto dto = repository.findBy(QPerson.person.fullName.eq("Walter"), it -> it.as(PersonDto.class).firstValue());
		assertThat(dto.lastName()).isEqualTo("White");

		ArgumentCaptor<LdapQuery> captor = ArgumentCaptor.forClass(LdapQuery.class);

		verify(ldapOperations, times(2)).find(captor.capture(), any());

		List<LdapQuery> queries = captor.getAllValues();

		assertThat(queries.get(0).attributes()).containsOnly("lastName");
		assertThat(queries.get(1).attributes()).isNullOrEmpty();
	}

	@Test // GH-269
	void findByShouldReturnOneWithProjection() {

		when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class)))
				.thenReturn(Collections.singletonList(walter));

		PersonProjection interfaceProjection = repository.findBy(QPerson.person.fullName.eq("Walter"),
				it -> it.as(PersonProjection.class).oneValue());
		assertThat(interfaceProjection.getLastName()).isEqualTo("White");

		PersonDto dto = repository.findBy(QPerson.person.fullName.eq("Walter"), it -> it.as(PersonDto.class).oneValue());
		assertThat(dto.lastName()).isEqualTo("White");

		ArgumentCaptor<LdapQuery> captor = ArgumentCaptor.forClass(LdapQuery.class);

		verify(ldapOperations, times(2)).find(captor.capture(), any());

		List<LdapQuery> queries = captor.getAllValues();

		assertThat(queries.get(0).attributes()).containsOnly("lastName");
		assertThat(queries.get(1).attributes()).isNullOrEmpty();
	}

	@Test // GH-269
	void findByShouldReturnAll() {

		when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class))).thenReturn(Arrays.asList(walter, hank));

		List<UnitTestPerson> all = repository.findBy(QPerson.person.fullName.eq("Walter"),
				FluentQuery.FetchableFluentQuery::all);

		assertThat(all).contains(walter);
	}

	@Test // GH-269
	void findByShouldReturnAllWithProjection() {

		when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class))).thenReturn(Arrays.asList(walter, hank));

		Stream<PersonProjection> all = repository.findBy(QPerson.person.fullName.eq("Walter"),
				q -> q.as(PersonProjection.class).stream());

		assertThat(all).hasOnlyElementsOfType(PersonProjection.class);
	}

	@Test // GH-269
	void findByShouldReturnFirstPage() {

		when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class)))
				.thenReturn(Collections.singletonList(walter));
		when(ldapOperations.search(any(LdapQuery.class), any(ContextMapper.class))).thenReturn(Arrays.asList(true, true));

		Page<PersonProjection> page = repository.findBy(QPerson.person.fullName.eq("Walter"),
				it -> it.as(PersonProjection.class).page(PageRequest.of(0, 1, Sort.unsorted())));
		assertThat(page.getContent().get(0).getLastName()).isEqualTo("White");
		assertThat(page.getTotalPages()).isEqualTo(2);

		ArgumentCaptor<LdapQuery> captor = ArgumentCaptor.forClass(LdapQuery.class);

		verify(ldapOperations).find(captor.capture(), any());

		LdapQuery query = captor.getValue();

		assertThat(query.countLimit()).isEqualTo(1);
	}

	@Test // GH-269
	void shouldRejectNextPage() {

		assertThatExceptionOfType(UnsupportedOperationException.class)
				.isThrownBy(() -> repository.findBy(QPerson.person.fullName.eq("Walter"),
						it -> it.as(PersonProjection.class).page(PageRequest.of(1, 1, Sort.unsorted()))));
	}

	@Test // GH-269
	void shouldRejectSortedPage() {

		assertThatExceptionOfType(UnsupportedOperationException.class)
				.isThrownBy(() -> repository.findBy(QPerson.person.fullName.eq("Walter"),
						it -> it.as(PersonProjection.class).page(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "foo")))));
	}

	@Test // GH-269
	void findByShouldReturnStream() {

		when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class))).thenReturn(Arrays.asList(walter, hank));

		List<UnitTestPerson> all = repository.findBy(QPerson.person.fullName.eq("Walter"),
				FluentQuery.FetchableFluentQuery::all);

		assertThat(all).contains(walter);
	}

	@Test // GH-269
	void findByShouldReturnStreamWithProjection() {

		when(ldapOperations.find(any(LdapQuery.class), eq(UnitTestPerson.class))).thenReturn(Arrays.asList(walter, hank));

		Stream<PersonProjection> all = repository.findBy(QPerson.person.fullName.eq("Walter"),
				q -> q.as(PersonProjection.class).stream());

		assertThat(all).hasOnlyElementsOfType(PersonProjection.class);
	}

	@Test // GH-269
	void findByShouldReturnCount() {

		when(ldapOperations.search(any(LdapQuery.class), any(ContextMapper.class))).thenReturn(Arrays.asList(true, true));

		long count = repository.findBy(QPerson.person.fullName.eq("Walter"), FluentQuery.FetchableFluentQuery::count);

		assertThat(count).isEqualTo(2);
	}

	@Test // GH-269
	void findByShouldReturnExists() {

		when(ldapOperations.search(any(LdapQuery.class), any(ContextMapper.class))).thenReturn(Arrays.asList(true, true),
				Collections.emptyList());

		boolean exists = repository.findBy(QPerson.person.fullName.eq("Walter"), FluentQuery.FetchableFluentQuery::exists);
		assertThat(exists).isTrue();

		exists = repository.findBy(QPerson.person.fullName.eq("Walter"), FluentQuery.FetchableFluentQuery::exists);
		assertThat(exists).isFalse();
	}

	interface PersonProjection {
		String getLastName();
	}

	record PersonDto(String lastName) {

	}
}
