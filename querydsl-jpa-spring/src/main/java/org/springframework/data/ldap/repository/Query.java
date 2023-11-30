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
package org.springframework.data.ldap.repository;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.ldap.query.SearchScope;

/**
 * Annotation for use in {@link org.springframework.data.ldap.repository.LdapRepository} declarations to create
 * automatic query methods based on statically defined queries.
 *
 * @author Mattias Hellborg Arthursson
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {

	/**
	 * Search base, to be used as input to {@link org.springframework.ldap.query.LdapQueryBuilder#base(javax.naming.Name)}
	 * .
	 *
	 * @return the search base, default is {@link org.springframework.ldap.support.LdapUtils#emptyLdapName()}
	 */
	String base() default "";

	/**
	 * The filter format string, to be used as input to
	 * {@link org.springframework.ldap.query.LdapQueryBuilder#filter(String, Object...)}.
	 *
	 * @return search filter, must be specified.
	 */
	String value() default "";

	/**
	 * Search scope, to be used as input to
	 * {@link org.springframework.ldap.query.LdapQueryBuilder#searchScope(org.springframework.ldap.query.SearchScope)}.
	 *
	 * @return the search scope.
	 */
	SearchScope searchScope() default SearchScope.SUBTREE;

	/**
	 * Time limit, to be used as input to {@link org.springframework.ldap.query.LdapQueryBuilder#timeLimit(int)}.
	 *
	 * @return the time limit.
	 */
	int timeLimit() default 0;

	/**
	 * Count limit, to be used as input to {@link org.springframework.ldap.query.LdapQueryBuilder#countLimit(int)}.
	 *
	 * @return the count limit.
	 */
	int countLimit() default 0;
}
