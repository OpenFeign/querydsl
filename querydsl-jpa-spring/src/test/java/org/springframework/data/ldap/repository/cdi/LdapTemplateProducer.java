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

import static org.mockito.Mockito.*;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;

/**
 * Simple component exposing a {@link LdapTemplate} instance as CDI bean.
 *
 * @author Mark Paluch
 */
class LdapTemplateProducer {

	@Produces
	@Singleton
	public LdapTemplate createLdapTemplate() {

		LdapTemplate ldapTemplateMock = mock(LdapTemplate.class);
		ObjectDirectoryMapper odmMock = mock(ObjectDirectoryMapper.class);

		when(ldapTemplateMock.getObjectDirectoryMapper()).thenReturn(odmMock);

		return ldapTemplateMock;
	}
}
