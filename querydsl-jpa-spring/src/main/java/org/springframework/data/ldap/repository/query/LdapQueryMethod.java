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

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.ldap.repository.Query;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.lang.Nullable;

/**
 * QueryMethod for Ldap Queries.
 *
 * @author Mattias Hellborg Arthursson
 * @author Eddu Melendez
 * @author Mark Paluch
 */
public class LdapQueryMethod extends QueryMethod {

	private final Method method;

	/**
	 * Creates a new LdapQueryMethod from the given parameters.
	 *
	 * @param method must not be {@literal null}
	 * @param metadata must not be {@literal null}
	 */
	public LdapQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {

		super(method, metadata, factory);

		this.method = method;
	}

	/**
	 * Check whether the target method is annotated with {@link org.springframework.data.ldap.repository.Query}.
	 *
	 * @return <code>true</code> if the target method is annotated with
	 *         {@link org.springframework.data.ldap.repository.Query}, <code>false</code> otherwise.
	 */
	public boolean hasQueryAnnotation() {
		return getQueryAnnotation() != null;
	}

	/**
	 * Get the {@link org.springframework.data.ldap.repository.Query} annotation of the target method (if any).
	 *
	 * @return the {@link org.springframework.data.ldap.repository.Query} annotation of the target method if present, or
	 *         <code>null</code> otherwise.
	 */
	@Nullable
	Query getQueryAnnotation() {
		return AnnotationUtils.getAnnotation(method, Query.class);
	}

	/**
	 * Get the required {@link org.springframework.data.ldap.repository.Query} annotation of the target method.
	 *
	 * @return the {@link org.springframework.data.ldap.repository.Query} annotation of the target method if present, or
	 *         {@link IllegalStateException} otherwise.
	 */
	Query getRequiredQueryAnnotation() {

		Query queryAnnotation = getQueryAnnotation();

		if (queryAnnotation != null) {
			return queryAnnotation;
		}

		throw new IllegalStateException("Required @Query annotation is not present");
	}
}
