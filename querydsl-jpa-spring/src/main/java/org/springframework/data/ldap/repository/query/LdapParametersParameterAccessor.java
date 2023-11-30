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

import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;

/**
 * LDAP-specific {@link ParametersParameterAccessor}.
 *
 * @author Mark Paluch
 * @since 2.6
 */
class LdapParametersParameterAccessor extends ParametersParameterAccessor implements LdapParameterAccessor {

	/**
	 * Creates a new {@link LdapParametersParameterAccessor}.
	 *
	 * @param method must not be {@literal null}.
	 * @param values must not be {@literal null}.
	 */
	public LdapParametersParameterAccessor(QueryMethod method, Object[] values) {
		super(method.getParameters(), values);
	}

	@Override
	public Object[] getBindableParameterValues() {

		Parameters<?, ?> bindableParameters = getParameters().getBindableParameters();
		int count = bindableParameters.getNumberOfParameters();

		if (count == 0) {
			return new Object[0];
		}

		Object[] values = new Object[count];

		for (int i = 0; i < count; i++) {
			values[i] = getBindableValue(i);
		}

		return values;
	}

}
