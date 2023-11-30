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

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.convert.DtoInstantiatingConverter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.EntityInstantiators;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.ProjectionInformation;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.util.Assert;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

/**
 * LDAP-specific {@link QuerydslPredicateExecutor}.
 *
 * @author Mark Paluch
 * @since 2.6
 */
public class QuerydslLdapPredicateExecutor<T> implements ListQuerydslPredicateExecutor<T> {

	private final EntityInformation<T, ?> entityInformation;
	private final ProjectionFactory projectionFactory;
	private final LdapOperations ldapOperations;
	private final MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext;
	private final EntityInstantiators entityInstantiators = new EntityInstantiators();

	/**
	 * Creates a new {@link QuerydslLdapPredicateExecutor}.
	 *
	 * @param entityType must not be {@literal null}.
	 * @param projectionFactory must not be {@literal null}.
	 * @param ldapOperations must not be {@literal null}.
	 * @param mappingContext must not be {@literal null}.
	 */
	public QuerydslLdapPredicateExecutor(Class<T> entityType, ProjectionFactory projectionFactory,
			LdapOperations ldapOperations,
			MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext) {

		Assert.notNull(entityType, "Entity type must not be null");
		Assert.notNull(projectionFactory, "ProjectionFactory must not be null");
		Assert.notNull(ldapOperations, "LdapOperations must not be null");
		Assert.notNull(mappingContext, "MappingContext must not be null");

		this.entityInformation = new LdapEntityInformation<>(entityType, ldapOperations.getObjectDirectoryMapper());
		this.ldapOperations = ldapOperations;
		this.projectionFactory = projectionFactory;
		this.mappingContext = mappingContext;
	}

	/**
	 * Creates a new {@link QuerydslLdapPredicateExecutor}.
	 *
	 * @param entityInformation must not be {@literal null}.
	 * @param projectionFactory must not be {@literal null}.
	 * @param ldapOperations must not be {@literal null}.
	 * @param mappingContext must not be {@literal null}.
	 */
	public QuerydslLdapPredicateExecutor(EntityInformation<T, ?> entityInformation, ProjectionFactory projectionFactory,
			LdapOperations ldapOperations,
			MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext) {

		Assert.notNull(entityInformation, "EntityInformation must not be null");
		Assert.notNull(projectionFactory, "ProjectionFactory must not be null");
		Assert.notNull(ldapOperations, "LdapOperations must not be null");
		Assert.notNull(mappingContext, "MappingContext must not be null");

		this.entityInformation = entityInformation;
		this.ldapOperations = ldapOperations;
		this.projectionFactory = projectionFactory;
		this.mappingContext = mappingContext;
	}

	@Override
	public Optional<T> findOne(Predicate predicate) {
		return findBy(predicate, Function.identity()).one();
	}

	@Override
	public List<T> findAll(Predicate predicate) {
		return queryFor(predicate).list();
	}

	@Override
	public long count(Predicate predicate) {
		return findBy(predicate, FluentQuery.FetchableFluentQuery::count);
	}

	public boolean exists(Predicate predicate) {
		return findBy(predicate, FluentQuery.FetchableFluentQuery::exists);
	}

	public List<T> findAll(Predicate predicate, Sort sort) {

		Assert.notNull(sort, "Pageable must not be null");

		if (sort.isUnsorted()) {
			return findAll(predicate);
		}

		throw new UnsupportedOperationException("Sorting is not supported");
	}

	public List<T> findAll(OrderSpecifier<?>... orders) {

		if (orders.length == 0) {
			return findAll();
		}

		throw new UnsupportedOperationException("Sorting is not supported");
	}

	@Override
	public List<T> findAll(Predicate predicate, OrderSpecifier<?>... orders) {

		if (orders.length == 0) {
			return findAll(predicate);
		}

		throw new UnsupportedOperationException("Sorting is not supported");
	}

	@Override
	public Page<T> findAll(Predicate predicate, Pageable pageable) {

		Assert.notNull(pageable, "Pageable must not be null");

		if (pageable.isUnpaged()) {
			return PageableExecutionUtils.getPage(findAll(predicate), pageable, () -> count(predicate));
		}

		if (pageable.getSort().isUnsorted() && pageable.getPageNumber() == 0) {

			return PageableExecutionUtils.getPage(queryFor(predicate, q -> q.countLimit(pageable.getPageSize())).list(),
					pageable, () -> count(predicate));
		}

		throw new UnsupportedOperationException("Pagination and Sorting is not supported");
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S extends T, R> R findBy(Predicate predicate,
			Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {

		Assert.notNull(queryFunction, "Query function must not be null");

		return queryFunction.apply(new FluentQuerydsl<>(predicate, (Class<S>) entityInformation.getJavaType()));
	}

	private QuerydslLdapQuery<T> queryFor(Predicate predicate) {
		return queryFor(predicate, it -> {

		});
	}

	private QuerydslLdapQuery<T> queryFor(Predicate predicate, Consumer<LdapQueryBuilder> queryBuilderConsumer) {

		Assert.notNull(predicate, "Predicate must not be null");

		return new QuerydslLdapQuery<>(ldapOperations, entityInformation.getJavaType(), queryBuilderConsumer)
				.where(predicate);
	}

	/**
	 * {@link FetchableFluentQuery} using {@link Example}.
	 *
	 * @author Mark Paluch
	 * @since 2.6
	 */
	class FluentQuerydsl<R> implements FluentQuery.FetchableFluentQuery<R> {

		private final Predicate predicate;
		private final Sort sort;
		private final Class<R> resultType;
		private final List<String> projection;

		FluentQuerydsl(Predicate predicate, Class<R> resultType) {
			this(predicate, Sort.unsorted(), resultType, Collections.emptyList());
		}

		FluentQuerydsl(Predicate predicate, Sort sort, Class<R> resultType, List<String> projection) {
			this.predicate = predicate;
			this.sort = sort;
			this.resultType = resultType;
			this.projection = projection;
		}

		@Override
		public FetchableFluentQuery<R> sortBy(Sort sort) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <R1> FetchableFluentQuery<R1> as(Class<R1> resultType) {

			Assert.notNull(projection, "Projection target type must not be null");

			return new FluentQuerydsl<>(predicate, sort, resultType, projection);
		}

		@Override
		public FetchableFluentQuery<R> project(Collection<String> properties) {

			Assert.notNull(properties, "Projection properties must not be null");

			return new FluentQuerydsl<>(predicate, sort, resultType, new ArrayList<>(properties));
		}

		@Nullable
		@Override
		public R oneValue() {

			List<T> results = findTop(2);

			if (results.isEmpty()) {
				return null;
			}

			if (results.size() > 1) {
				throw new IncorrectResultSizeDataAccessException(1);
			}

			T one = results.get(0);
			return getConversionFunction().apply(one);
		}

		@Nullable
		@Override
		public R firstValue() {

			List<T> results = findTop(2);

			if (results.isEmpty()) {
				return null;
			}

			T one = results.get(0);
			return getConversionFunction().apply(one);
		}

		@Override
		public List<R> all() {
			return stream().collect(Collectors.toList());
		}

		@Override
		public Page<R> page(Pageable pageable) {

			Assert.notNull(pageable, "Pageable must not be null");

			if (pageable.isUnpaged()) {
				return PageableExecutionUtils.getPage(all(), pageable, this::count);
			}

			if (pageable.getSort().isUnsorted() && pageable.getPageNumber() == 0) {

				Function<Object, R> conversionFunction = getConversionFunction();

				return PageableExecutionUtils.getPage(
						findTop(pageable.getPageSize()).stream().map(conversionFunction).collect(Collectors.toList()), pageable,
						this::count);
			}

			throw new UnsupportedOperationException("Pagination and Sorting is not supported");
		}

		@Override
		public Stream<R> stream() {

			Function<Object, R> conversionFunction = getConversionFunction();

			return search(null, QuerydslLdapQuery::list).stream().map(conversionFunction);
		}

		@Override
		public long count() {
			return search(null, q -> q.search(it -> true)).size();
		}

		@Override
		public boolean exists() {
			return !search(1, q -> q.search(it -> true)).isEmpty();
		}

		private List<T> findTop(int limit) {
			return search(limit, QuerydslLdapQuery::list);
		}

		private <S> S search(@Nullable Integer limit, Function<QuerydslLdapQuery<T>, S> searchFunction) {

			QuerydslLdapQuery<T> q = queryFor(predicate, query -> {

				List<String> projection = getProjection();

				if (!projection.isEmpty()) {
					query.attributes(projection.toArray(new String[0]));
				}

				if (limit != null) {
					query.countLimit(limit);
				}
			});

			return searchFunction.apply(q);
		}

		@SuppressWarnings("unchecked")
		private <P> Function<Object, P> getConversionFunction(Class<?> inputType, Class<P> targetType) {

			if (targetType.isAssignableFrom(inputType)) {
				return (Function<Object, P>) Function.identity();
			}

			if (targetType.isInterface()) {
				return o -> projectionFactory.createProjection(targetType, o);
			}

			DtoInstantiatingConverter converter = new DtoInstantiatingConverter(targetType, mappingContext,
					entityInstantiators);

			return o -> (P) converter.convert(o);
		}

		private Function<Object, R> getConversionFunction() {
			return getConversionFunction(entityInformation.getJavaType(), resultType);
		}

		private List<String> getProjection() {

			if (projection.isEmpty()) {

				if (resultType.isAssignableFrom(entityInformation.getJavaType())) {
					return projection;
				}

				if (resultType.isInterface()) {
					ProjectionInformation projectionInformation = projectionFactory.getProjectionInformation(resultType);

					if (projectionInformation.isClosed()) {
						return projectionInformation.getInputProperties().stream().map(FeatureDescriptor::getName)
								.collect(Collectors.toList());
					}
				}
			}

			return projection;
		}

	}

}
