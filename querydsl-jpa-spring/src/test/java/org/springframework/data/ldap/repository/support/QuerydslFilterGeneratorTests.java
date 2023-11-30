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
package org.springframework.data.ldap.repository.support;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;
import org.springframework.ldap.odm.core.impl.DefaultObjectDirectoryMapper;

import com.querydsl.core.types.Expression;

/**
 * @author Mattias Hellborg Arthursson
 * @author Eddu Melendez
 */
class QuerydslFilterGeneratorTests {

	private ObjectDirectoryMapper odm = new DefaultObjectDirectoryMapper();
	private LdapSerializer tested = new LdapSerializer(odm, UnitTestPerson.class);
	private QPerson person = QPerson.person;

	@Test
	void testEqualsFilter() {

		Expression<?> expression = person.fullName.eq("John Doe");
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(cn=John Doe)");
	}

	@Test
	void testAndFilter() {

		Expression<?> expression = person.fullName.eq("John Doe").and(person.lastName.eq("Doe"));
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(&(cn=John Doe)(sn=Doe))");
	}

	@Test
	void testOrFilter() {

		Expression<?> expression = person.fullName.eq("John Doe").or(person.lastName.eq("Doe"));
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(|(cn=John Doe)(sn=Doe))");
	}

	@Test
	void testOr() {

		Expression<?> expression = person.fullName.eq("John Doe")
				.and(person.lastName.eq("Doe").or(person.lastName.eq("Die")));

		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(&(cn=John Doe)(|(sn=Doe)(sn=Die)))");
	}

	@Test
	void testNot() {

		Expression<?> expression = person.fullName.eq("John Doe").not();
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(!(cn=John Doe))");
	}

	@Test
	void testIsLike() {

		Expression<?> expression = person.fullName.like("kalle*");
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(cn=kalle*)");
	}

	@Test
	void testStartsWith() {

		Expression<?> expression = person.fullName.startsWith("kalle");
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(cn=kalle*)");
	}

	@Test
	void testEndsWith() {

		Expression<?> expression = person.fullName.endsWith("kalle");
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(cn=*kalle)");
	}

	@Test
	void testContains() {

		Expression<?> expression = person.fullName.contains("kalle");
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(cn=*kalle*)");
	}

	@Test
	void testNotNull() {

		Expression<?> expression = person.fullName.isNotNull();
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(cn=*)");
	}

	@Test
	void testNull() {

		Expression<?> expression = person.fullName.isNull();
		Filter result = tested.handle(expression);

		assertThat(result).hasToString("(!(cn=*))");
	}
}
