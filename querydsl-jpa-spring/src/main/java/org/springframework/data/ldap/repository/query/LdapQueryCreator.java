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

import java.util.Iterator;
import java.util.List;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.core.ObjectDirectoryMapper;
import org.springframework.ldap.query.ConditionCriteria;
import org.springframework.ldap.query.ContainerCriteria;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.util.Assert;

/**
 * Creator of dynamic queries based on method names.
 *
 * @author Mattias Hellborg Arthursson
 * @author Mark Paluch
 */
class LdapQueryCreator extends AbstractQueryCreator<LdapQuery, ContainerCriteria> {

	private final Class<?> entityType;
	private final ObjectDirectoryMapper mapper;
	private final List<String> inputProperties;

	/**
	 * Constructs a new {@link LdapQueryCreator}.
	 *
	 * @param tree must not be {@literal null}.
	 * @param entityType must not be {@literal null}.
	 * @param mapper must not be {@literal null}.
	 * @param values must not be {@literal null}.
	 * @param inputProperties must not be {@literal null}.
	 */
	LdapQueryCreator(PartTree tree, Class<?> entityType, ObjectDirectoryMapper mapper,
			LdapParameterAccessor parameterAccessor, List<String> inputProperties) {

		super(tree, parameterAccessor);

		Assert.notNull(entityType, "Entity type must not be null");
		Assert.notNull(mapper, "ObjectDirectoryMapper must not be null");

		this.entityType = entityType;
		this.mapper = mapper;
		this.inputProperties = inputProperties;
	}

	@Override
	protected ContainerCriteria create(Part part, Iterator<Object> iterator) {

		Entry entry = AnnotatedElementUtils.findMergedAnnotation(entityType, Entry.class);

		LdapQueryBuilder query = query();

		if (entry != null) {
			query = query.base(entry.base());
		}

		if (!inputProperties.isEmpty()) {
			query.attributes(inputProperties.toArray(new String[0]));
		}

		ConditionCriteria criteria = query.where(getAttribute(part));

		return appendCondition(part, iterator, criteria);
	}

	private ContainerCriteria appendCondition(Part part, Iterator<Object> iterator, ConditionCriteria criteria) {

		Part.Type type = part.getType();

		String value = null;
		if (iterator.hasNext()) {
			Object next = iterator.next();
			value = next != null ? next.toString() : null;
		}
		switch (type) {
			case NEGATING_SIMPLE_PROPERTY:
				return criteria.not().is(value);
			case SIMPLE_PROPERTY:
				return criteria.is(value);
			case STARTING_WITH:
				return criteria.like(value + "*");
			case ENDING_WITH:
				return criteria.like("*" + value);
			case CONTAINING:
				return criteria.like("*" + value + "*");
			case LIKE:
				return criteria.like(value);
			case NOT_LIKE:
				return criteria.not().like(value);
			case GREATER_THAN_EQUAL:
				return criteria.gte(value);
			case LESS_THAN_EQUAL:
				return criteria.lte(value);
			case IS_NOT_NULL:
				return criteria.isPresent();
			case IS_NULL:
				return criteria.not().isPresent();
			default:
				throw new IllegalArgumentException(String.format("%s queries are not supported for LDAP repositories", type));
		}

	}

	private String getAttribute(Part part) {
		PropertyPath path = part.getProperty();
		if (path.hasNext()) {
			throw new IllegalArgumentException("Nested properties are not supported");
		}

		return mapper.attributeFor(entityType, path.getSegment());
	}

	@Override
	protected ContainerCriteria and(Part part, ContainerCriteria base, Iterator<Object> iterator) {
		ConditionCriteria criteria = base.and(getAttribute(part));

		return appendCondition(part, iterator, criteria);
	}

	@Override
	protected ContainerCriteria or(ContainerCriteria base, ContainerCriteria criteria) {
		return base.or(criteria);
	}

	@Override
	protected LdapQuery complete(ContainerCriteria criteria, Sort sort) {
		return criteria;
	}
}
