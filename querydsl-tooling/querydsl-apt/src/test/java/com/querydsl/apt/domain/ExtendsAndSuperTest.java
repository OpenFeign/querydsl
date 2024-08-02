/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class ExtendsAndSuperTest {

  @QueryEntity
  public static class ExtendsAndSuper<A> {
    // col
    Collection<? extends A> extendsCol;
    Collection<? extends CharSequence> extendsCol2;
    Collection<? super A> superCol;
    Collection<? super String> superCol2;

    // list
    List<? extends A> extendsList;
    List<? extends CharSequence> extendsList2;
    List<? super A> superList;
    List<? super String> superList2;

    // set
    Set<? extends A> extendsSet;
    Set<? extends CharSequence> extendsSet2;
    Set<? super A> superSet;
    Set<? super String> superSet2;

    // map
    Map<String, ? super A> superMap;
    Map<? super A, String> superMap2;
    Map<String, ? extends A> extendsMap;
    Map<? extends A, String> extendsMap2;
  }

  @Test
  public void validate() {
    var var = QExtendsAndSuperTest_ExtendsAndSuper.extendsAndSuper;
    assertThat(var.extendsCol.getElementType()).isEqualTo(Object.class);
    assertThat(var.extendsCol2.getElementType()).isEqualTo(CharSequence.class);

    assertThat(var.superCol.getElementType()).isEqualTo(Object.class);
    assertThat(var.superCol2.getElementType()).isEqualTo(Object.class);
  }

  @Test
  public void test() {
    var var = QExtendsAndSuperTest_ExtendsAndSuper.extendsAndSuper;
    var entity = new ExtendsAndSuper<>();
    assertThat(var.eq(entity)).isNotNull();
    assertThat(var.extendsMap.containsKey("")).isNotNull();
    assertThat(var.extendsMap2.containsValue("")).isNotNull();
  }
}
