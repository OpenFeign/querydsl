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

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;

/**
 * QPerson is a Querydsl query type for Person
 */
public class QPerson extends EntityPathBase<UnitTestPerson> {

	private static final long serialVersionUID = -1526737794;

	public static final QPerson person = new QPerson("person");

	public final StringPath fullName = createString("fullName");

	public final ListPath<String, StringPath> description = this.<String, StringPath> createList("description",
			String.class, StringPath.class, PathInits.DIRECT2);

	public final StringPath lastName = createString("lastName");

	public QPerson(String variable) {
		super(UnitTestPerson.class, forVariable(variable));
	}

	public QPerson(Path<? extends UnitTestPerson> path) {
		super(path.getType(), path.getMetadata());
	}

	public QPerson(PathMetadata metadata) {
		super(UnitTestPerson.class, metadata);
	}

}
