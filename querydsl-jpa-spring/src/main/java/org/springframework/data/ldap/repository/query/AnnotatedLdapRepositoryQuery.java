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

import static org.springframework.ldap.query.LdapQueryBuilder.*;

import org.springframework.data.ldap.repository.Query;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.util.Assert;

/**
 * Handles queries for repository methods annotated with {@link org.springframework.data.ldap.repository.Query}.
 *
 * @author Mattias Hellborg Arthursson
 * @author Mark Paluch
 */
public class AnnotatedLdapRepositoryQuery extends AbstractLdapRepositoryQuery {

	private final Query queryAnnotation;

	/**
	 * Construct a new instance.
	 *
	 * @param queryMethod the QueryMethod.
	 * @param entityType the managed class.
	 * @param ldapOperations the LdapOperations instance to use.
	 * @param mappingContext must not be {@literal null}.
	 * @param instantiators must not be {@literal null}.
	 */
	public AnnotatedLdapRepositoryQuery(LdapQueryMethod queryMethod, Class<?> entityType, LdapOperations ldapOperations,
			MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext,
			EntityInstantiators instantiators) {

		super(queryMethod, entityType, ldapOperations, mappingContext, instantiators);

		Assert.notNull(queryMethod.getQueryAnnotation(), "Annotation must be present");
		Assert.hasLength(queryMethod.getQueryAnnotation().value(), "Query filter must be specified");

		queryAnnotation = queryMethod.getRequiredQueryAnnotation();
	}

	@Override
	protected LdapQuery createQuery(LdapParameterAccessor parameters) {

		return query().base(queryAnnotation.base()) //
				.searchScope(queryAnnotation.searchScope()) //
				.countLimit(queryAnnotation.countLimit()) //
				.timeLimit(queryAnnotation.timeLimit()) //
				.filter(queryAnnotation.value(), parameters.getBindableParameterValues());
	}
}
