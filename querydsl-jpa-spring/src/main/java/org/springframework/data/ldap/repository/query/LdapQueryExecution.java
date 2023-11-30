/*
 * Copyright 2021-2023 the original author or authors.
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

import org.springframework.core.convert.converter.Converter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.convert.DtoInstantiatingConverter;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.util.ClassUtils;

/**
 * Set of classes to contain query execution strategies. Depending (mostly) on the return type of a
 * {@link org.springframework.data.repository.query.QueryMethod} a {@link AbstractLdapRepositoryQuery} can be executed
 * in various flavors.
 *
 * @author Mark Paluch
 * @since 2.6
 */
@FunctionalInterface
interface LdapQueryExecution {

	Object execute(LdapQuery query);

	/**
	 * {@link LdapQueryExecution} returning a single object.
	 *
	 * @author Mark Paluch
	 */
	final class FindOneExecution implements LdapQueryExecution {

		private final LdapOperations operations;
		private final Class<?> entityType;

		FindOneExecution(LdapOperations operations, Class<?> entityType) {
			this.operations = operations;
			this.entityType = entityType;
		}

		@Override
		public Object execute(LdapQuery query) {
			try {
				return operations.findOne(query, entityType);
			} catch (EmptyResultDataAccessException e) {
				return null;
			}
		}
	}

	/**
	 * {@link LdapQueryExecution} returning a list of objects.
	 *
	 * @author Mark Paluch
	 */
	final class CollectionExecution implements LdapQueryExecution {

		private final LdapOperations operations;
		private final Class<?> entityType;

		CollectionExecution(LdapOperations operations, Class<?> entityType) {
			this.operations = operations;
			this.entityType = entityType;
		}

		@Override
		public Object execute(LdapQuery query) {
			return operations.find(query, entityType);
		}
	}

	/**
	 * {@link LdapQueryExecution} for a Stream.
	 *
	 * @author Mark Paluch
	 */
	final class StreamExecution implements LdapQueryExecution {

		private final LdapOperations operations;
		private final Class<?> entityType;
		private final Converter<Object, Object> resultProcessing;

		StreamExecution(LdapOperations operations, Class<?> entityType, Converter<Object, Object> resultProcessing) {
			this.operations = operations;
			this.entityType = entityType;
			this.resultProcessing = resultProcessing;
		}

		@Override
		public Object execute(LdapQuery query) {
			return operations.find(query, entityType).stream().map(resultProcessing::convert);
		}
	}

	/**
	 * An {@link LdapQueryExecution} that wraps the results of the given delegate with the given result processing.
	 */
	final class ResultProcessingExecution implements LdapQueryExecution {

		private final LdapQueryExecution delegate;
		private final Converter<Object, Object> converter;

		public ResultProcessingExecution(LdapQueryExecution delegate, Converter<Object, Object> converter) {
			this.delegate = delegate;
			this.converter = converter;
		}

		@Override
		public Object execute(LdapQuery query) {
			return converter.convert(delegate.execute(query));
		}
	}

	/**
	 * A {@link Converter} to post-process all source objects using the given {@link ResultProcessor}.
	 *
	 * @author Mark Paluch
	 */
	final class ResultProcessingConverter implements Converter<Object, Object> {

		private final ResultProcessor processor;
		private final MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext;
		private final EntityInstantiators instantiators;

		public ResultProcessingConverter(ResultProcessor processor,
				MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext,
				EntityInstantiators instantiators) {
			this.processor = processor;
			this.mappingContext = mappingContext;
			this.instantiators = instantiators;
		}

		@Override
		public Object convert(Object source) {

			ReturnedType returnedType = processor.getReturnedType();

			if (ClassUtils.isPrimitiveOrWrapper(returnedType.getReturnedType())) {
				return source;
			}

			if (source != null && returnedType.isInstance(source)) {
				return source;
			}
			Converter<Object, Object> converter = new DtoInstantiatingConverter(returnedType.getReturnedType(),
					mappingContext, instantiators);

			return processor.processResult(source, converter);
		}
	}
}
