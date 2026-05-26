package fluentq.apt.domain;

import fluentq.core.annotations.QueryEntity;
import fluentq.core.annotations.QuerySupertype;
import org.junit.Test;

public class RawTest {

  @QuerySupertype
  public static class SuperClass<T extends Comparable<T>> {

    public String property;
  }

  @SuppressWarnings("rawtypes")
  @QueryEntity
  public static class Entity extends SuperClass {

    public String property2;
  }

  @Test
  public void test() {}
}
