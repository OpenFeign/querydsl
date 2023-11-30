/*
 * Copyright 2018-2023 the original author or authors.
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
package org.springframework.data.ldap.core.mapping;

import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.AnnotationBasedPersistentProperty;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.Lazy;
import org.springframework.ldap.odm.annotations.Id;

/**
 * LDAP-specific {@link AnnotationBasedPersistentProperty}. By default, if a property is named {@code id} it's used as
 * Id property.
 *
 * @author Mark Paluch
 * @since 2.0.4
 */
public class LdapPersistentProperty extends AnnotationBasedPersistentProperty<LdapPersistentProperty> {

	private final Lazy<Boolean> isId = Lazy.of(() -> isAnnotationPresent(Id.class));

	/**
	 * Create a new {@link LdapPersistentProperty}.
	 *
	 * @param property must not be {@literal null}.
	 * @param owner must not be {@literal null}.
	 * @param simpleTypeHolder must not be {@literal null}.
	 */
	public LdapPersistentProperty(Property property, PersistentEntity<?, LdapPersistentProperty> owner,
			SimpleTypeHolder simpleTypeHolder) {
		super(property, owner, simpleTypeHolder);
	}

	@Override
	protected Association<LdapPersistentProperty> createAssociation() {
		return null;
	}

	@Override
	public boolean isIdProperty() {
		return isId.get();
	}
}
