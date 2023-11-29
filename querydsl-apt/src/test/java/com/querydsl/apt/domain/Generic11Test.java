package com.querydsl.apt.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import org.junit.Test;

public class Generic11Test {

  // 1
  public interface WhatEver {}

  @Entity
  public static class A<T extends WhatEver> {}

  @Entity
  public static class B extends A {} // note the missing type parameter

  // 2
  @MappedSuperclass
  public abstract static class WhatEver2 {}

  @Entity
  public static class A2<T extends WhatEver2> {}

  @Entity
  public static class B2 extends A2 {} // note the missing type parameter

  @Test
  public void test() {}
}
