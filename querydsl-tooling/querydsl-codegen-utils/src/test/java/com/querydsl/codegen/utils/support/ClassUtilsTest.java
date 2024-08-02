/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class ClassUtilsTest {

  @Test
  public void GetName() {
    assertThat(ClassUtils.getName(int.class)).isEqualTo("int");
    assertThat(
            ClassUtils.getName(
                int.class, Collections.<String>emptySet(), Collections.<String>emptySet()))
        .isEqualTo("int");
    assertThat(ClassUtils.getName(Object.class)).isEqualTo("Object");
    assertThat(ClassUtils.getName(Object[].class)).isEqualTo("Object[]");
    assertThat(ClassUtils.getName(int.class)).isEqualTo("int");
    assertThat(ClassUtils.getName(int[].class)).isEqualTo("int[]");
    assertThat(ClassUtils.getName(void.class)).isEqualTo("void");
    assertThat(ClassUtils.getName(Locale.class)).isEqualTo("java.util.Locale");
    assertThat(ClassUtils.getName(Locale[].class)).isEqualTo("java.util.Locale[]");
  }

  @Test
  public void GetName_Packge() {
    assertThat(
            ClassUtils.getName(
                Locale.class, Collections.singleton("java.util"), Collections.<String>emptySet()))
        .isEqualTo("Locale");
    assertThat(
            ClassUtils.getName(
                Locale.class,
                Collections.singleton("java.util.gen"),
                Collections.<String>emptySet()))
        .isEqualTo("java.util.Locale");
  }

  @Test
  public void Normalize() {
    assertThat(ClassUtils.normalize(ArrayList.class)).isEqualTo(List.class);
    assertThat(ClassUtils.normalize(HashSet.class)).isEqualTo(Set.class);
    assertThat(ClassUtils.normalize(HashMap.class)).isEqualTo(Map.class);
    //        assertEquals(Collection.class, ClassUtils.normalize(Bag.class));
  }
}
