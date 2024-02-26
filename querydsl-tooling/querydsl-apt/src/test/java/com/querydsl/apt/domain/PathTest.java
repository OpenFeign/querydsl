package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PathTest {

  @Test
  public void test() {
    assertThat(QPath.path.getType()).isEqualTo(Path.class);
  }
}
