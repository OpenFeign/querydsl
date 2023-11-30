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
import org.springframework.data.ldap.config.DummyLdapRepository;
import org.springframework.data.ldap.repository.config.AnnotationConfigTests.Config;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @author Mattias Hellborg Arthursson
 * @author Mark Paluch
 */
@SpringJUnitConfig(classes = Config.class)
class AnnotationConfigTests {

	@Autowired ApplicationContext context;

	@Test
	void testAnnotationConfig() {

		DummyLdapRepository repository = context.getBean(DummyLdapRepository.class);
		assertThat(repository).isNotNull();
	}

	@Configuration
	@ImportResource("classpath:/infrastructure.xml")
	@EnableLdapRepositories(basePackageClasses = DummyLdapRepository.class)
	static class Config {}
}
