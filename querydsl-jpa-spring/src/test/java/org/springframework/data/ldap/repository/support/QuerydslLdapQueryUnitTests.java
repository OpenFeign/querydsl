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
package org.springframework.data.ldap.repository.support;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.filter.AbsoluteTrueFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.odm.core.impl.DefaultObjectDirectoryMapper;
import org.springframework.ldap.query.LdapQuery;

/**
 * Unit tests for {@link QuerydslLdapQuery}.
 *
 * @author Mark Paluch
 */
@MockitoSettings
class QuerydslLdapQueryUnitTests {

	@Mock LdapOperations ldapOperations;

	@BeforeEach
	void before() {
		when(ldapOperations.getObjectDirectoryMapper()).thenReturn(new DefaultObjectDirectoryMapper());
	}

	@Test // DATALDAP-65
	void shouldCreateFilter() {

		QuerydslLdapQuery<UnitTestPerson> query = new QuerydslLdapQuery<>(ldapOperations, UnitTestPerson.class);

		LdapQuery ldapQuery = query.where(QPerson.person.fullName.eq("foo")).buildQuery();

		assertThat(ldapQuery.filter()).isInstanceOf(EqualsFilter.class);
	}

	@Test // DATALDAP-65
	void shouldCreateEmptyFilter() {

		QuerydslLdapQuery<UnitTestPerson> query = new QuerydslLdapQuery<>(ldapOperations, UnitTestPerson.class);

		LdapQuery ldapQuery = query.buildQuery();

		assertThat(ldapQuery.filter()).isInstanceOf(AbsoluteTrueFilter.class);
	}

	@Test // DATALDAP-65
	void shouldCreateEmptyFilterFromWhereNull() {

		QuerydslLdapQuery<UnitTestPerson> query = new QuerydslLdapQuery<>(ldapOperations, QPerson.person);

		LdapQuery ldapQuery = query.where(null).buildQuery();

		assertThat(ldapQuery.filter()).isInstanceOf(AbsoluteTrueFilter.class);
	}
}
