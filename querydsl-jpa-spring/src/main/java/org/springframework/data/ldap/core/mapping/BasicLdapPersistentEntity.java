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
package org.springframework.data.ldap.core.mapping;

import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.util.TypeInformation;

/**
 * {@link LdapPersistentEntity} implementation.
 *
 * @author Mark Paluch
 * @since 2.0.4
 */
public class BasicLdapPersistentEntity<T> extends BasicPersistentEntity<T, LdapPersistentProperty>
		implements LdapPersistentEntity<T> {

	/**
	 * Creates a new {@link BasicLdapPersistentEntity}.
	 *
	 * @param information must not be {@literal null}.
	 */
	public BasicLdapPersistentEntity(TypeInformation<T> information) {
		super(information);
	}
}
