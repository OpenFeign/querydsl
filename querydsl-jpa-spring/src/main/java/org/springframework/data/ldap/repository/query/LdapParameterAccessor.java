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

import org.springframework.data.repository.query.ParameterAccessor;

/**
 * LDAP-specific {@link ParameterAccessor} exposing all bindable parameters.
 *
 * @author Mark Paluch
 * @since 2.6
 */
interface LdapParameterAccessor extends ParameterAccessor {

	/**
	 * Returns the bindable parameter values of the underlying query method.
	 *
	 * @return
	 */
	Object[] getBindableParameterValues();
}
