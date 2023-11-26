package com.querydsl.apt.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class PathTest {

  @Test
  public void test() {
    assertEquals(Path.class, QPath.path.getType());
  }
}
