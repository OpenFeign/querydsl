package com.querydsl.apt.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.lang.annotation.Annotation;
import org.junit.Ignore;

@Ignore
public class AnnotationTypeTest {

  @MappedSuperclass
  public abstract static class BaseObject<T extends Annotation> {}

  @Entity
  public static class Person extends BaseObject<jakarta.persistence.Id> {
    @Id private Long id;
  }

  @Embeddable
  public static class Address extends BaseObject<jakarta.persistence.EmbeddedId> {
    @EmbeddedId private String street;
  }
}
