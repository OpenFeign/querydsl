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

import static org.springframework.ldap.query.LdapQueryBuilder.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.naming.Name;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Persistable;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.util.Optionals;
import org.springframework.lang.Nullable;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.support.CountNameClassPairCallbackHandler;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.util.Assert;

/**
 * Base repository implementation for LDAP.
 *
 * @author Mattias Hellborg Arthursson
 * @author Mark Paluch
 * @author Jens Schauder
 */
public class SimpleLdapRepository<T> implements LdapRepository<T> {

	private static final String OBJECTCLASS_ATTRIBUTE = "objectclass";

	private final LdapOperations ldapOperations;
	private final ObjectDirectoryMapper odm;
	private final Class<T> entityType;

	/**
	 * Creates a new {@link SimpleLdapRepository}.
	 *
	 * @param ldapOperations must not be {@literal null}.
	 * @param odm must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 */
	public SimpleLdapRepository(LdapOperations ldapOperations, ObjectDirectoryMapper odm, Class<T> entityType) {

		Assert.notNull(ldapOperations, "LdapOperations must not be null");
		Assert.notNull(odm, "ObjectDirectoryMapper must not be null");
		Assert.notNull(entityType, "Entity type must not be null");

		this.ldapOperations = ldapOperations;
		this.odm = odm;
		this.entityType = entityType;
	}

	/**
	 * Creates a new {@link SimpleLdapRepository}.
	 *
	 * @param ldapOperations must not be {@literal null}.
	 * @param odm must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 */
	SimpleLdapRepository(LdapOperations ldapOperations,
			MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> context,
			ObjectDirectoryMapper odm, Class<T> entityType) {

		Assert.notNull(ldapOperations, "LdapOperations must not be null");
		Assert.notNull(context, "MappingContext must not be null");
		Assert.notNull(odm, "ObjectDirectoryMapper must not be null");
		Assert.notNull(entityType, "Entity type must not be null");

		this.ldapOperations = ldapOperations;
		this.odm = odm;
		this.entityType = entityType;
	}
 
	// -------------------------------------------------------------------------
	// Methods from LdapRepository
	// ------------------------------------------------------------------------

	@Override
	public Optional<T> findOne(LdapQuery ldapQuery) {

		Assert.notNull(ldapQuery, "LdapQuery must not be null");

		try {
			return Optional.ofNullable(ldapOperations.findOne(ldapQuery, entityType));
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Override
	public List<T> findAll(LdapQuery ldapQuery) {

		Assert.notNull(ldapQuery, "LdapQuery must not be null");
		return ldapOperations.find(ldapQuery, entityType);
	}


	private <S extends T> boolean isNew(S entity, @Nullable Name id) {

		if (entity instanceof Persistable) {
			Persistable<?> persistable = (Persistable<?>) entity;
			return persistable.isNew();
		} else {
			return id == null;
		}
	}


}
