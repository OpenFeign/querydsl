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
package org.springframework.data.ldap.repository.query;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.ldap.core.mapping.LdapMappingContext;
import org.springframework.data.ldap.repository.support.BaseUnitTestPerson;
import org.springframework.data.ldap.repository.support.UnitTestPerson;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @author Mattias Hellborg Arthursson
 * @author Eddu Melendez
 * @author Mark Paluch
 */
@SpringJUnitConfig
@ContextConfiguration
class PartTreeLdapRepositoryQueryTests {

	@Autowired private LdapTemplate ldapTemplate;

	private Class<?> entityClass = UnitTestPerson.class;
	private Class<?> targetClass = UnitTestPersonRepository.class;
	private DefaultRepositoryMetadata repositoryMetadata = new DefaultRepositoryMetadata(targetClass);
	private ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

	@Test
	void testFindByFullName() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullName", String.class), "(cn=John Doe)", "John Doe");
	}

	// LDAP-314
	@Test
	void testFindByFullNameWithBase() throws NoSuchMethodException {

		entityClass = BaseUnitTestPerson.class;
		targetClass = BaseTestPersonRepository.class;
		repositoryMetadata = new DefaultRepositoryMetadata(targetClass);

		assertFilterAndBaseForMethod(targetClass.getMethod("findByFullName", String.class), "(cn=John Doe)", "ou=someOu",
				"John Doe");
	}

	@Test
	void testFindByFullNameLike() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameLike", String.class), "(cn=*John*)", "*John*");
	}

	@Test
	void testFindByFullNameStartsWith() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameStartsWith", String.class), "(cn=John*)", "John");
	}

	@Test
	void testFindByFullNameEndsWith() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameEndsWith", String.class), "(cn=*John)", "John");
	}

	@Test
	void testFindByFullNameContains() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameContains", String.class), "(cn=*John*)", "John");
	}

	@Test
	void testFindByFullNameGreaterThanEqual() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameGreaterThanEqual", String.class), "(cn>=John)", "John");
	}

	@Test
	void testFindByFullNameLessThanEqual() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameLessThanEqual", String.class), "(cn<=John)", "John");
	}

	@Test
	void testFindByFullNameIsNotNull() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameIsNotNull"), "(cn=*)");
	}

	@Test
	void testFindByFullNameIsNull() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameIsNull"), "(!(cn=*))");
	}

	@Test
	void testFindByFullNameNot() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameNot", String.class), "(!(cn=John Doe))", "John Doe");
	}

	@Test
	void testFindByFullNameNotLike() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameNotLike", String.class), "(!(cn=*John*))", "*John*");
	}

	@Test
	void testFindByFullNameAndLastName() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameAndLastName", String.class, String.class),
				"(&(cn=John Doe)(sn=Doe))", "John Doe", "Doe");
	}

	@Test
	void testFindByFullNameAndLastNameNot() throws NoSuchMethodException {
		assertFilterForMethod(targetClass.getMethod("findByFullNameAndLastNameNot", String.class, String.class),
				"(&(cn=John Doe)(!(sn=Doe)))", "John Doe", "Doe");
	}

	private void assertFilterForMethod(Method targetMethod, String expectedFilter, Object... expectedParams) {
		assertFilterAndBaseForMethod(targetMethod, expectedFilter, "", expectedParams);
	}

	private void assertFilterAndBaseForMethod(Method targetMethod, String expectedFilter, String expectedBase,
			Object... expectedParams) {

		LdapQueryMethod queryMethod = new LdapQueryMethod(targetMethod, repositoryMetadata, factory);
		PartTreeLdapRepositoryQuery tested = new PartTreeLdapRepositoryQuery(queryMethod, entityClass, ldapTemplate,
				new LdapMappingContext(), new EntityInstantiators());

		LdapQuery query = tested.createQuery(new LdapParametersParameterAccessor(queryMethod, expectedParams));
		String base = query.base().toString();
		assertThat(base).isEqualTo(expectedBase);
		assertThat(query.filter().encode()).isEqualTo(expectedFilter);
	}
}
