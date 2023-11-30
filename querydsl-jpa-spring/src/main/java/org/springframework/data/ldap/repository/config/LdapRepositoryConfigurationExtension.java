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
package org.springframework.data.ldap.repository.config;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.ldap.core.mapping.LdapMappingContext;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.ldap.repository.support.LdapRepositoryFactoryBean;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * {@link RepositoryConfigurationExtension} for LDAP.
 *
 * @author Mattias Hellborg Arthursson
 * @author Mark Paluch
 */
public class LdapRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

	private static final String ATT_LDAP_TEMPLATE_REF = "ldap-template-ref";
	private static final String MAPPING_CONTEXT_BEAN_NAME = "ldapMappingContext";

	@Override
	public String getModuleName() {
		return "LDAP";
	}

	@Override
	protected String getModulePrefix() {
		return "ldap";
	}

	public String getRepositoryFactoryBeanClassName() {
		return LdapRepositoryFactoryBean.class.getName();
	}

	@Override
	protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
		return Collections.singleton(Entry.class);
	}

	@Override
	protected Collection<Class<?>> getIdentifyingTypes() {
		return Collections.singleton(LdapRepository.class);
	}

	@Override
	public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {

		Element element = config.getElement();
		String ldapTemplateRef = element.getAttribute(ATT_LDAP_TEMPLATE_REF);

		if (!StringUtils.hasText(ldapTemplateRef)) {
			ldapTemplateRef = "ldapTemplate";
		}

		builder.addPropertyReference("ldapOperations", ldapTemplateRef);
		builder.addPropertyReference("mappingContext", MAPPING_CONTEXT_BEAN_NAME);
	}

	@Override
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {

		AnnotationAttributes attributes = config.getAttributes();

		builder.addPropertyReference("ldapOperations", attributes.getString("ldapTemplateRef"));
		builder.addPropertyReference("mappingContext", MAPPING_CONTEXT_BEAN_NAME);
	}

	@Override
	public void registerBeansForRoot(BeanDefinitionRegistry registry, RepositoryConfigurationSource configurationSource) {

		if (!registry.containsBeanDefinition(MAPPING_CONTEXT_BEAN_NAME)) {

			RootBeanDefinition definition = new RootBeanDefinition(LdapMappingContext.class);
			definition.setRole(AbstractBeanDefinition.ROLE_INFRASTRUCTURE);
			definition.setSource(configurationSource.getSource());

			registry.registerBeanDefinition(MAPPING_CONTEXT_BEAN_NAME, definition);
		}
	}

	@Override
	protected boolean useRepositoryConfiguration(RepositoryMetadata metadata) {
		return !metadata.isReactiveRepository();
	}
}
