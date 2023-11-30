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

import java.util.List;

import javax.naming.Name;

import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.odm.annotations.Transient;

/**
 * @author Mattias Hellborg Arthursson
 */
@Entry(objectClasses = { "inetOrgPerson", "organizationalPerson", "person", "top" })

public class UnitTestPerson {
	@Id private Name dn;

	@Attribute(name = "cn")
	@DnAttribute("cn") private String fullName;

	@Attribute(name = "sn") private String lastName;

	@Attribute(name = "description") private List<String> description;

	@Transient
	@DnAttribute("c") private String country;

	@Transient
	@DnAttribute("ou") private String company;

	// This should be automatically found
	private String telephoneNumber;

	public UnitTestPerson() {}

	public UnitTestPerson(Name dn, String fullName, String lastName, List<String> description, String country,
			String company, String telephoneNumber) {
		this.dn = dn;
		this.fullName = fullName;
		this.lastName = lastName;
		this.description = description;
		this.country = country;
		this.company = company;
		this.telephoneNumber = telephoneNumber;
	}
}
