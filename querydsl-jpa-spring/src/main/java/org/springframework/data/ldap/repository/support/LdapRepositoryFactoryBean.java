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

import javax.naming.Name;

import org.springframework.data.ldap.core.mapping.LdapMappingContext;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.Nullable;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.beans.factory.FactoryBean} to create
 * {@link org.springframework.data.ldap.repository.LdapRepository} instances.
 *
 * @author Mattias Hellborg Arthursson
 * @author Oliver Gierke
 * @author Mark Paluch
 */
public class LdapRepositoryFactoryBean<T extends Repository<S, Name>, S>
		extends RepositoryFactoryBeanSupport<T, S, Name> {

	private @Nullable LdapOperations ldapOperations;
	private boolean mappingContextConfigured = false;
	private @Nullable MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext;

	/**
	 * Creates a new {@link LdapRepositoryFactoryBean} for the given repository interface.
	 *
	 * @param repositoryInterface must not be {@literal null}.
	 */
	public LdapRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
		super(repositoryInterface);
	}

	/**
	 * @param ldapOperations
	 */
	public void setLdapOperations(LdapOperations ldapOperations) {
		this.ldapOperations = ldapOperations;
	}

	@Override
	public void setMappingContext(MappingContext<?, ?> mappingContext) {

		super.setMappingContext(mappingContext);
		this.mappingContext = mappingContext;
		this.mappingContextConfigured = true;
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory() {

		Assert.state(ldapOperations != null, "LdapOperations must be set");

		return mappingContext != null ? new LdapRepositoryFactory(ldapOperations, mappingContext)
				: new LdapRepositoryFactory(ldapOperations);
	}

	@Override
	public void afterPropertiesSet() {

		Assert.notNull(ldapOperations, "LdapOperations must be set");

		super.afterPropertiesSet();

		if (!mappingContextConfigured) {
			setMappingContext(new LdapMappingContext());
		}
	}
}
