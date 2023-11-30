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

import java.lang.annotation.Annotation;
import java.util.Collection;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.VariableElement;

import org.springframework.lang.Nullable;
import org.springframework.ldap.odm.annotations.Id;

import com.querydsl.apt.DefaultConfiguration;

/**
 * Configuration for {@link LdapAnnotationProcessor}.
 *
 * @author Mattias Hellborg Arthursson
 * @author Eddu Melendez
 * @author Mark Paluch
 */
class DefaultLdapAnnotationProcessorConfiguration extends DefaultConfiguration {

	public DefaultLdapAnnotationProcessorConfiguration(ProcessingEnvironment processingEnvironment,
			RoundEnvironment roundEnv, Collection<String> keywords, Class<? extends Annotation> entitiesAnn,
			Class<? extends Annotation> entityAnn, @Nullable Class<? extends Annotation> superTypeAnn,
			@Nullable Class<? extends Annotation> embeddableAnn, @Nullable Class<? extends Annotation> embeddedAnn,
			Class<? extends Annotation> skipAnn) {

		super(processingEnvironment, roundEnv, keywords, entitiesAnn, entityAnn, superTypeAnn, embeddableAnn, embeddedAnn,
				skipAnn);
	}

	@Override
	public boolean isBlockedField(VariableElement field) {
		return super.isBlockedField(field) || field.getAnnotation(Id.class) != null;
	}

	@Override
	public boolean isValidField(VariableElement field) {
		return super.isValidField(field) && field.getAnnotation(Id.class) == null;
	}
}
