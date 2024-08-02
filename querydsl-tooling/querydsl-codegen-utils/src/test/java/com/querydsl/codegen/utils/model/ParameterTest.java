/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ParameterTest {

  @Test
  public void test() {
    var param1 = new Parameter("test", new ClassType(TypeCategory.STRING, String.class));
    var param2 = new Parameter("test2", new ClassType(TypeCategory.STRING, String.class));
    var param3 = new Parameter("test2", new ClassType(TypeCategory.NUMERIC, Integer.class));

    assertThat(param1.equals(param2)).isFalse();
    assertThat(param1.equals(param3)).isFalse();
    assertThat(param2.equals(param3)).isFalse();
  }
}
