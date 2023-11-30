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
package org.springframework.data.ldap.repository.query;

import static org.springframework.data.ldap.repository.query.LdapQueryExecution.*;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.ldap.repository.Query;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.util.Assert;

/**
 * Base class for {@link RepositoryQuery} implementations for LDAP.
 *
 * @author Mattias Hellborg Arthursson
 * @author Mark Paluch
 */
public abstract class AbstractLdapRepositoryQuery implements RepositoryQuery {

	private final LdapQueryMethod queryMethod;
	private final Class<?> entityType;
	private final LdapOperations ldapOperations;
	private final MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext;
	private final EntityInstantiators instantiators;

	/**
	 * Creates a new {@link AbstractLdapRepositoryQuery} instance given {@link LdapQuery}, {@link Class} and
	 * {@link LdapOperations}.
	 *
	 * @param queryMethod must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param ldapOperations must not be {@literal null}.
	 * @param mappingContext must not be {@literal null}.
	 * @param instantiators must not be {@literal null}.
	 */
	public AbstractLdapRepositoryQuery(LdapQueryMethod queryMethod, Class<?> entityType, LdapOperations ldapOperations,
			MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext,
			EntityInstantiators instantiators) {

		Assert.notNull(queryMethod, "LdapQueryMethod must not be null");
		Assert.notNull(entityType, "Entity type must not be null");
		Assert.notNull(ldapOperations, "LdapOperations must not be null");

		this.queryMethod = queryMethod;
		this.entityType = entityType;
		this.ldapOperations = ldapOperations;
		this.mappingContext = mappingContext;
		this.instantiators = instantiators;
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public final Object execute(Object[] parameters) {

		LdapParametersParameterAccessor parameterAccessor = new LdapParametersParameterAccessor(queryMethod, parameters);
		LdapQuery query = createQuery(parameterAccessor);

		ResultProcessor processor = queryMethod.getResultProcessor().withDynamicProjection(parameterAccessor);
		Class<?> typeToRead = processor.getReturnedType().getDomainType();

		ResultProcessingConverter converter = new ResultProcessingConverter(processor, mappingContext, instantiators);
		ResultProcessingExecution execution = new ResultProcessingExecution(
				getLdapQueryExecutionToWrap(typeToRead, converter), converter);

		return execution.execute(query);
	}

	private LdapQueryExecution getLdapQueryExecutionToWrap(Class<?> typeToRead,
			Converter<Object, Object> resultProcessing) {

		if (queryMethod.isCollectionQuery()) {
			return new CollectionExecution(ldapOperations, typeToRead);
		} else if (queryMethod.isStreamQuery()) {
			return new StreamExecution(ldapOperations, typeToRead, resultProcessing);
		} else {
			return new FindOneExecution(ldapOperations, typeToRead);
		}
	}

	/**
	 * Creates a {@link Query} instance using the given {@literal parameters}.
	 *
	 * @param parameters must not be {@literal null}.
	 * @return
	 */
	protected abstract LdapQuery createQuery(LdapParameterAccessor parameters);

	/**
	 * @return
	 */
	protected Class<?> getEntityClass() {
		return entityType;
	}

	@Override
	public final QueryMethod getQueryMethod() {
		return queryMethod;
	}
}
