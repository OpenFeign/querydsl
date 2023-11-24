package io.github.openfeign.querydsl.jpa.spring.repository.config;

import java.lang.annotation.Annotation;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

/**
 * {@link ImportBeanDefinitionRegistrar} to setup Querydsl repositories via {@link
 * EnableQuerydslRepositories}.
 *
 * @author Marvin Froeder
 */
public class QuerydslRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

  @Override
  protected Class<? extends Annotation> getAnnotation() {
    return EnableQuerydslRepositories.class;
  }

  @Override
  protected RepositoryConfigurationExtension getExtension() {
    return new QuerydslRepositoryConfigurationExtension();
  }
}
