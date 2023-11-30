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
package org.springframework.data.ldap.repository.config;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.ldap.config.DummyEntity;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.ldap.repository.support.SimpleLdapRepository;
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

		CustomizedDummyRepository repository = applicationContext.getBean(CustomizedDummyRepository.class);
		assertThat(repository.returnOne()).isEqualTo(1);
	}

	@Configuration
	@ImportResource("classpath:/infrastructure.xml")
	@EnableLdapRepositories(considerNestedRepositories = true, repositoryBaseClass = CustomizedLdapRepositoryImpl.class)
	static class Config {}

	interface CustomizedDummyRepository extends CustomizedLdapRepository<DummyEntity> {}

	@NoRepositoryBean
	interface CustomizedLdapRepository<T> extends LdapRepository<T> {

		int returnOne();
	}

	static class CustomizedLdapRepositoryImpl<T> extends SimpleLdapRepository<T> implements CustomizedLdapRepository<T> {

		public CustomizedLdapRepositoryImpl(LdapOperations ldapOperations, ObjectDirectoryMapper odm, Class<T> clazz) {
			super(ldapOperations, odm, clazz);
		}

		@Override
		public int returnOne() {
			return 1;
		}
	}
}
