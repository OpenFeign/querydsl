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
import java.util.function.Consumer;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.filter.AbsoluteTrueFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.util.Assert;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.FilteredClause;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;

/**
 * Spring LDAP specific {@link FilteredClause} implementation.
 *
 * @author Mattias Hellborg Arthursson
 * @author Eddu Melendez
 * @author Mark Paluch
 */
public class QuerydslLdapQuery<K> implements FilteredClause<QuerydslLdapQuery<K>> {

	private final LdapOperations ldapOperations;
	private final Class<K> entityType;
	private final LdapSerializer filterGenerator;
	private final Consumer<LdapQueryBuilder> queryCustomizer;

	private QueryMixin<QuerydslLdapQuery<K>> queryMixin = new QueryMixin<>(this, new DefaultQueryMetadata().noValidate());

	/**
	 * Creates a new {@link QuerydslLdapQuery}.
	 *
	 * @param ldapOperations must not be {@literal null}.
	 * @param entityPath must not be {@literal null}.
	 */
	public QuerydslLdapQuery(LdapOperations ldapOperations, EntityPath<K> entityPath) {
		this(ldapOperations, (Class<K>) entityPath.getType());
	}

	/**
	 * Creates a new {@link QuerydslLdapQuery}.
	 *
	 * @param ldapOperations must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 */
	public QuerydslLdapQuery(LdapOperations ldapOperations, Class<K> entityType) {
		this(ldapOperations, entityType, it -> {

		});
	}

	/**
	 * Creates a new {@link QuerydslLdapQuery}.
	 *
	 * @param ldapOperations must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param queryCustomizer must not be {@literal null}.
	 * @since 2.6
	 */
	public QuerydslLdapQuery(LdapOperations ldapOperations, Class<K> entityType,
			Consumer<LdapQueryBuilder> queryCustomizer) {

		Assert.notNull(ldapOperations, "LdapOperations must not be null");
		Assert.notNull(entityType, "Type must not be null");
		Assert.notNull(queryCustomizer, "Query customizer must not be null");

		this.ldapOperations = ldapOperations;
		this.entityType = entityType;
		this.queryCustomizer = queryCustomizer;
		this.filterGenerator = new LdapSerializer(ldapOperations.getObjectDirectoryMapper(), this.entityType);
	}

	@Override
	public QuerydslLdapQuery<K> where(Predicate... o) {

		if (o == null) {
			return this;
		}

		return queryMixin.where(o);
	}

	@SuppressWarnings("unchecked")
	public List<K> list() {

		LdapQuery ldapQuery = buildQuery();
		if (ldapQuery.filter() instanceof AbsoluteTrueFilter) {
			return ldapOperations.findAll(entityType);
		}

		return ldapOperations.find(ldapQuery, entityType);
	}

	<T> List<T> search(ContextMapper<T> mapper) {

		LdapQuery ldapQuery = buildQuery();

		return ldapOperations.search(ldapQuery, mapper);
	}

	public K uniqueResult() {
		return ldapOperations.findOne(buildQuery(), entityType);
	}

	LdapQuery buildQuery() {

		Predicate where = queryMixin.getMetadata().getWhere();

		LdapQueryBuilder builder = query();
		queryCustomizer.accept(builder);

		return where != null ? builder.filter(filterGenerator.handle(where)) : builder.filter(new AbsoluteTrueFilter());
	}
}
