package com.querydsl.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.annotation.Generated;
import java.lang.annotation.Annotation;
import org.junit.Test;

public class GeneratedAnnotationResolverTest {

  private static final String defaultGenerated = Generated.class.getName();

  @Test
  public void resolveCustom() {
    var customClass = "some.random.Class";
    Class<? extends Annotation> resolvedAnnotationClass =
        GeneratedAnnotationResolver.resolve(customClass);
    assertThat(resolvedAnnotationClass).isNotNull();
  }

  @Test
  public void resolveNull() {
    Class<? extends Annotation> resolvedAnnotationClass = GeneratedAnnotationResolver.resolve(null);
    assertThat(resolvedAnnotationClass).isNotNull();
  }

  @Test
  public void resolveDefault() {
    Class<? extends Annotation> resolvedAnnotationClass =
        GeneratedAnnotationResolver.resolveDefault();
    assertThat(resolvedAnnotationClass).isNotNull();
  }
}
