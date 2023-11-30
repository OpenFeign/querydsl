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

import static org.springframework.data.querydsl.QuerydslUtils.*;
import static org.springframework.data.repository.core.support.RepositoryComposition.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.ldap.core.mapping.LdapMappingContext;
import org.springframework.data.ldap.repository.query.AnnotatedLdapRepositoryQuery;
import org.springframework.data.ldap.repository.query.LdapQueryMethod;
import org.springframework.data.ldap.repository.query.PartTreeLdapRepositoryQuery;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.Nullable;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.util.Assert;

/**
 * Factory to create {@link org.springframework.data.ldap.repository.LdapRepository} instances.
 *
 * @author Mattias Hellborg Arthursson
 * @author Eddu Melendez
 * @author Mark Paluch
 * @author Jens Schauder
 */
public class LdapRepositoryFactory extends RepositoryFactorySupport {

	private final LdapQueryLookupStrategy queryLookupStrategy;
	private final LdapOperations ldapOperations;
	private final MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext;
	private final EntityInstantiators instantiators = new EntityInstantiators();

	/**
	 * Creates a new {@link LdapRepositoryFactory}.
	 *
	 * @param ldapOperations must not be {@literal null}.
	 */
	public LdapRepositoryFactory(LdapOperations ldapOperations) {

		Assert.notNull(ldapOperations, "LdapOperations must not be null");

		this.ldapOperations = ldapOperations;
		this.mappingContext = new LdapMappingContext();
		this.queryLookupStrategy = new LdapQueryLookupStrategy(ldapOperations, instantiators, mappingContext);
	}

	/**
	 * Creates a new {@link LdapRepositoryFactory}.
	 *
	 * @param ldapOperations must not be {@literal null}.
	 * @param mappingContext must not be {@literal null}.
	 */
	LdapRepositoryFactory(LdapOperations ldapOperations,
			MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext) {

		Assert.notNull(ldapOperations, "LdapOperations must not be null");
		Assert.notNull(mappingContext, "LdapMappingContext must not be null");

		this.queryLookupStrategy = new LdapQueryLookupStrategy(ldapOperations, instantiators, mappingContext);
		this.ldapOperations = ldapOperations;
		this.mappingContext = mappingContext;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		return new LdapEntityInformation(domainClass, ldapOperations.getObjectDirectoryMapper());
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		return SimpleLdapRepository.class;
	}

	@Override
	protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
		return getRepositoryFragments(metadata, this.ldapOperations);
	}

	/**
	 * Creates {@link RepositoryFragments} based on {@link RepositoryMetadata} to add LDAP-specific extensions. Typically,
	 * adds a {@link QuerydslLdapPredicateExecutor} if the repository interface uses Querydsl.
	 * <p>
	 * Can be overridden by subclasses to customize {@link RepositoryFragments}.
	 *
	 * @param metadata repository metadata.
	 * @param operations the LDAP operations manager.
	 * @return
	 * @since 2.6
	 */
	protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata, LdapOperations operations) {

		boolean isQueryDslRepository = QUERY_DSL_PRESENT
				&& QuerydslPredicateExecutor.class.isAssignableFrom(metadata.getRepositoryInterface());

		if (isQueryDslRepository) {

			if (metadata.isReactiveRepository()) {
				throw new InvalidDataAccessApiUsageException(
						"Cannot combine Querydsl and reactive repository support in a single interface");
			}

			return RepositoryFragments.just(new QuerydslLdapPredicateExecutor<>(
					getEntityInformation(metadata.getDomainType()), getProjectionFactory(), operations, mappingContext));
		}

		return RepositoryFragments.empty();
	}

	@Override
	protected Object getTargetRepository(RepositoryInformation information) {

		boolean acceptsMappingContext = acceptsMappingContext(information);

		if (acceptsMappingContext) {
			return getTargetRepositoryViaReflection(information, ldapOperations, mappingContext,
					ldapOperations.getObjectDirectoryMapper(), information.getDomainType());
		}

		return getTargetRepositoryViaReflection(information, ldapOperations,
				ldapOperations.getObjectDirectoryMapper(),
				information.getDomainType());
	}

	@Override
	protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable Key key,
			QueryMethodEvaluationContextProvider evaluationContextProvider) {
		return Optional.of(queryLookupStrategy);
	}

	/**
	 * Allow creation of repository base classes that do not accept a {@link LdapMappingContext} that was introduced with
	 * version 2.6.
	 *
	 * @param information
	 * @return
	 */
	private static boolean acceptsMappingContext(RepositoryInformation information) {

		Class<?> repositoryBaseClass = information.getRepositoryBaseClass();

		Constructor<?>[] declaredConstructors = repositoryBaseClass.getDeclaredConstructors();

		boolean acceptsMappingContext = false;

		for (Constructor<?> declaredConstructor : declaredConstructors) {
			Class<?>[] parameterTypes = declaredConstructor.getParameterTypes();

			if (parameterTypes.length == 4 && parameterTypes[1].isAssignableFrom(LdapMappingContext.class)) {
				acceptsMappingContext = true;
			}
		}

		return acceptsMappingContext;
	}

	private static final class LdapQueryLookupStrategy implements QueryLookupStrategy {

		private final LdapOperations ldapOperations;
		private final EntityInstantiators instantiators;
		private final MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext;

		public LdapQueryLookupStrategy(LdapOperations ldapOperations, EntityInstantiators instantiators,
				MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext) {

			this.ldapOperations = ldapOperations;
			this.instantiators = instantiators;
			this.mappingContext = mappingContext;
		}

		@Override
		public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory,
				NamedQueries namedQueries) {

			LdapQueryMethod queryMethod = new LdapQueryMethod(method, metadata, factory);
			Class<?> domainType = metadata.getDomainType();

			if (queryMethod.hasQueryAnnotation()) {
				return new AnnotatedLdapRepositoryQuery(queryMethod, domainType, ldapOperations, mappingContext, instantiators);
			} else {
				return new PartTreeLdapRepositoryQuery(queryMethod, domainType, ldapOperations, mappingContext, instantiators);
			}
		}
	}
}
