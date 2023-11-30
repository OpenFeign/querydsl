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

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.UnsatisfiedResolutionException;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.ProcessBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.repository.cdi.CdiRepositoryBean;
import org.springframework.data.repository.cdi.CdiRepositoryExtensionSupport;
import org.springframework.ldap.core.LdapOperations;

/**
 * CDI extension to export LDAP repositories.
 *
 * @author Mark Paluch
 * @since 2.1
 */
public class LdapRepositoryExtension extends CdiRepositoryExtensionSupport {

	private static final Log LOG = LogFactory.getLog(LdapRepositoryExtension.class);

	private final Map<Set<Annotation>, Bean<LdapOperations>> ldapOperations = new HashMap<>();

	public LdapRepositoryExtension() {
		LOG.info("Activating CDI extension for Spring Data LDAP repositories");
	}

	@SuppressWarnings("unchecked")
	<X> void processBean(@Observes ProcessBean<X> processBean) {

		Bean<X> bean = processBean.getBean();

		for (Type type : bean.getTypes()) {
			if (type instanceof Class<?> && LdapOperations.class.isAssignableFrom((Class<?>) type)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(
							String.format("Discovered %s with qualifiers %s", LdapOperations.class.getName(), bean.getQualifiers()));
				}

				// Store the EntityManager bean using its qualifiers.
				ldapOperations.put(new HashSet<>(bean.getQualifiers()), (Bean<LdapOperations>) bean);
			}
		}
	}

	void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {

		for (Entry<Class<?>, Set<Annotation>> entry : getRepositoryTypes()) {

			Class<?> repositoryType = entry.getKey();
			Set<Annotation> qualifiers = entry.getValue();

			// Create the bean representing the repository.
			CdiRepositoryBean<?> repositoryBean = createRepositoryBean(repositoryType, qualifiers, beanManager);

			if (LOG.isInfoEnabled()) {
				LOG.info(String.format("Registering bean for %s with qualifiers %s", repositoryType.getName(), qualifiers));
			}

			// Register the bean to the container.
			registerBean(repositoryBean);
			afterBeanDiscovery.addBean(repositoryBean);
		}
	}

	/**
	 * Creates a {@link CdiRepositoryBean} for the repository of the given type.
	 *
	 * @param <T> the type of the repository.
	 * @param repositoryType the class representing the repository.
	 * @param qualifiers the qualifiers to be applied to the bean.
	 * @param beanManager the BeanManager instance.
	 * @return the repository bean.
	 */
	private <T> CdiRepositoryBean<T> createRepositoryBean(Class<T> repositoryType, Set<Annotation> qualifiers,
			BeanManager beanManager) {

		// Determine the LdapOperations bean which matches the qualifiers of the repository.
		Bean<LdapOperations> LdapOperations = this.ldapOperations.get(qualifiers);

		if (LdapOperations == null) {
			throw new UnsatisfiedResolutionException(String.format("Unable to resolve a bean for '%s' with qualifiers %s",
					LdapOperations.class.getName(), qualifiers));
		}

		// Construct and return the repository bean.
		return new LdapRepositoryBean<>(LdapOperations, qualifiers, repositoryType, beanManager,
				Optional.of(getCustomImplementationDetector()));
	}
}
