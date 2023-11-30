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

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.ldap.repository.support.LdapRepositoryFactory;
import org.springframework.data.repository.cdi.CdiRepositoryBean;
import org.springframework.data.repository.config.CustomRepositoryImplementationDetector;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.util.Assert;

/**
 * {@link CdiRepositoryBean} to create LDAP repository instances.
 *
 * @author Mark Paluch
 * @since 2.1
 */
public class LdapRepositoryBean<T> extends CdiRepositoryBean<T> {

	private final Bean<LdapOperations> operations;

	/**
	 * Creates a new {@link LdapRepositoryBean}.
	 *
	 * @param operations must not be {@literal null}.
	 * @param qualifiers must not be {@literal null}.
	 * @param repositoryType must not be {@literal null}.
	 * @param beanManager must not be {@literal null}.
	 * @param detector detector for the custom {@link org.springframework.data.repository.Repository} implementations
	 *          {@link CustomRepositoryImplementationDetector}, can be {@link Optional#empty()}.
	 */
	LdapRepositoryBean(Bean<LdapOperations> operations, Set<Annotation> qualifiers, Class<T> repositoryType,
			BeanManager beanManager, Optional<CustomRepositoryImplementationDetector> detector) {

		super(qualifiers, repositoryType, beanManager, detector);

		Assert.notNull(operations, "LdapOperations bean must not be null");
		this.operations = operations;
	}

	@Override
	protected T create(CreationalContext<T> creationalContext, Class<T> repositoryType) {

		LdapOperations ldapOperations = getDependencyInstance(operations, LdapOperations.class);

		return create(() -> new LdapRepositoryFactory(ldapOperations), repositoryType);
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return operations.getScope();
	}
}
