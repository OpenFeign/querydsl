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

import java.util.Collections;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;

import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Transient;

import com.querydsl.apt.AbstractQuerydslProcessor;
import com.querydsl.apt.Configuration;
import com.querydsl.apt.DefaultConfiguration;
import com.querydsl.core.annotations.QueryEntities;

/**
 * QueryDSL Annotation Processor to generate QueryDSL classes for entity classes annotated with {@link Entry}.
 *
 * @author Mattias Hellborg Arthursson
 * @author Eddu Melendez
 * @author Mark Paluch
 */
@SupportedAnnotationTypes("org.springframework.ldap.odm.annotations.*")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class LdapAnnotationProcessor extends AbstractQuerydslProcessor {

	@Override
	protected Configuration createConfiguration(RoundEnvironment roundEnv) {
		processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Running " + getClass().getSimpleName());

		DefaultConfiguration configuration = new DefaultLdapAnnotationProcessorConfiguration(processingEnv, roundEnv,
				Collections.emptySet(), QueryEntities.class, Entry.class, null, null, null, Transient.class);
		configuration.setUseFields(true);
		configuration.setUseGetters(false);

		return configuration;
	}
}
