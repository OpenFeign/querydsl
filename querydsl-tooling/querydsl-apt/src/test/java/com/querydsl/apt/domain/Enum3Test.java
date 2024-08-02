package com.querydsl.apt.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import org.junit.Test;

public class Enum3Test {

  public interface BaseInterface extends Serializable {
    String getFoo();

    String getBar();
  }

  public enum EnumImplementation implements SpecificInterface {
    FOO,
    BAR;

    @Override
    public EnumImplementation getValue() {
      return this;
    }

    @Override
    public String getFoo() {
      return null;
    }

    @Override
    public String getBar() {
      return name();
    }
  }

  public interface SpecificInterface extends BaseInterface {
    EnumImplementation getValue();
  }

  @Entity
  public static class Entity1 {

    @Enumerated(jakarta.persistence.EnumType.STRING)
    private EnumImplementation value;

    public SpecificInterface getValue() {
      return value;
    }
  }

  @Entity
  public static class Entity2 {

    private EnumImplementation value;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    public SpecificInterface getValue() {
      return value;
    }
  }

  @Entity
  public static class Entity3 {

    private EnumImplementation value;

    public SpecificInterface getValue() {
      return value;
    }
  }

  @Test
  public void test() {}
}
