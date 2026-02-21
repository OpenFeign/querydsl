package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PathBuilderValidatorTest {

  public static class Customer {
    String name;
    Collection<Integer> collection;
    Map<String, Integer> map;
  }

  public static class ExtendedCustomer extends Customer {}

  public static class Project {
    public String getName() {
      return "";
    }

    public Collection<Integer> getCollection() {
      return null;
    }

    public Map<String, Integer> getMap() {
      return null;
    }
  }

  public static class ExtendedProject extends Project {
    public boolean isStarted() {
      return true;
    }
  }

  @Test
  void defaults() {
    assertThat(PathBuilderValidator.DEFAULT.validate(Customer.class, "name", String.class))
        .isEqualTo(String.class);
    assertThat(PathBuilderValidator.DEFAULT.validate(ExtendedCustomer.class, "name", String.class))
        .isEqualTo(String.class);
    assertThat(PathBuilderValidator.DEFAULT.validate(Project.class, "name", String.class))
        .isEqualTo(String.class);
    assertThat(PathBuilderValidator.DEFAULT.validate(ExtendedProject.class, "name", String.class))
        .isEqualTo(String.class);
  }

  @Test
  void fields() {
    assertThat(PathBuilderValidator.FIELDS.validate(Customer.class, "name", String.class))
        .isEqualTo(String.class);
    assertThat(PathBuilderValidator.FIELDS.validate(ExtendedCustomer.class, "name", String.class))
        .isEqualTo(String.class);
    assertThat(PathBuilderValidator.FIELDS.validate(Customer.class, "collection", Collection.class))
        .isEqualTo(Integer.class);
    assertThat(PathBuilderValidator.FIELDS.validate(Customer.class, "map", Map.class))
        .isEqualTo(Integer.class);
    assertThat(PathBuilderValidator.FIELDS.validate(Project.class, "name", String.class)).isNull();
    assertThat(PathBuilderValidator.FIELDS.validate(ExtendedProject.class, "name", String.class))
        .isNull();
  }

  @Test
  void properties() {
    assertThat(PathBuilderValidator.PROPERTIES.validate(Customer.class, "name", String.class))
        .isNull();
    assertThat(
            PathBuilderValidator.PROPERTIES.validate(ExtendedCustomer.class, "name", String.class))
        .isNull();
    assertThat(PathBuilderValidator.PROPERTIES.validate(Project.class, "name", String.class))
        .isEqualTo(String.class);
    assertThat(
            PathBuilderValidator.PROPERTIES.validate(ExtendedProject.class, "name", String.class))
        .isEqualTo(String.class);
    assertThat(
            PathBuilderValidator.PROPERTIES.validate(
                ExtendedProject.class, "started", Boolean.class))
        .isEqualTo(Boolean.class);
    assertThat(
            PathBuilderValidator.PROPERTIES.validate(Project.class, "collection", Collection.class))
        .isEqualTo(Integer.class);
    assertThat(PathBuilderValidator.PROPERTIES.validate(Project.class, "map", Map.class))
        .isEqualTo(Integer.class);
  }
}
