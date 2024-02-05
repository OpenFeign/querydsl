/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.Test;

public class ConstructorTest {

  @Test
  public void test() {
    Parameter firstName =
        new Parameter("firstName", new ClassType(TypeCategory.STRING, String.class));
    Parameter lastName =
        new Parameter("lastName", new ClassType(TypeCategory.STRING, String.class));
    Constructor c1 = new Constructor(Arrays.asList(firstName, lastName));
    Constructor c2 = new Constructor(Arrays.asList(firstName, lastName));
    assertThat(c1).isEqualTo(c1);
    assertThat(c2).isEqualTo(c1);
    assertThat(c2.hashCode()).isEqualTo(c1.hashCode());
  }
}
