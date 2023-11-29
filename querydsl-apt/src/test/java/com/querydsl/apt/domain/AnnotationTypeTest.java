package com.querydsl.apt.domain;

import jakarta.persistence.*;
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
