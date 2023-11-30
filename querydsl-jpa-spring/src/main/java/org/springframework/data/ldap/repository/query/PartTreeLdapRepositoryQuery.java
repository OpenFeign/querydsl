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

import java.util.Collections;
import java.util.List;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ReturnedType;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;
import org.springframework.ldap.query.LdapQuery;

/**
 * {@link RepositoryQuery} implementation for LDAP.
 *
 * @author Mattias Hellborg Arthursson
 * @author Mark Paluch
 */
public class PartTreeLdapRepositoryQuery extends AbstractLdapRepositoryQuery {

	private final PartTree partTree;
	private final ObjectDirectoryMapper objectDirectoryMapper;

	/**
	 * Creates a new {@link PartTreeLdapRepositoryQuery}.
	 *
	 * @param queryMethod must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param ldapOperations must not be {@literal null}.
	 * @param mappingContext must not be {@literal null}.
	 * @param instantiators must not be {@literal null}.
	 */
	public PartTreeLdapRepositoryQuery(LdapQueryMethod queryMethod, Class<?> entityType, LdapOperations ldapOperations,
			MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext,
			EntityInstantiators instantiators) {

		super(queryMethod, entityType, ldapOperations, mappingContext, instantiators);

		partTree = new PartTree(queryMethod.getName(), entityType);
		objectDirectoryMapper = ldapOperations.getObjectDirectoryMapper();
	}

	@Override
	protected LdapQuery createQuery(LdapParameterAccessor parameters) {

		List<String> inputProperties = Collections.emptyList();
		ReturnedType returnedType = getQueryMethod().getResultProcessor().withDynamicProjection(parameters)
				.getReturnedType();

		if (returnedType.needsCustomConstruction()) {
			inputProperties = returnedType.getInputProperties();
		}

		org.springframework.data.ldap.repository.query.LdapQueryCreator queryCreator = new LdapQueryCreator(partTree,
				getEntityClass(), objectDirectoryMapper, parameters, inputProperties);
		return queryCreator.createQuery();
	}
}
