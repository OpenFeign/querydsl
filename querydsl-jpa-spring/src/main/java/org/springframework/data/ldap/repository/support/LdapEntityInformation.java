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

package org.springframework.data.ldap.repository.support;

import javax.naming.Name;

import org.springframework.data.repository.core.support.AbstractEntityInformation;
import org.springframework.lang.Nullable;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;

/**
 * ODM-based {@link org.springframework.data.repository.core.EntityInformation} for LDAP entities.
 *
 * @author Mark Paluch
 * @since 2.0
 * @see org.springframework.ldap.odm.core.ObjectDirectoryMapper
 * @see org.springframework.ldap.odm.annotations.Entry
 */
class LdapEntityInformation<T> extends AbstractEntityInformation<T, Name> {

	private final ObjectDirectoryMapper odm;

	public LdapEntityInformation(Class<T> domainClass, ObjectDirectoryMapper odm) {
		super(domainClass);
		this.odm = odm;
	}

	@Nullable
	@Override
	public Name getId(T entity) {
		return odm.getId(entity);
	}

	@Override
	public Class<Name> getIdType() {
		return Name.class;
	}
}
