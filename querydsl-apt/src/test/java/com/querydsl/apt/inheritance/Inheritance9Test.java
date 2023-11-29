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
package com.querydsl.apt.inheritance;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.domain.SuperSupertype;
import org.junit.Test;

/**
 * related to https://bugs.launchpad.net/querydsl/+bug/538148
 *
 * @author tiwe
 */
public class Inheritance9Test {

  @QueryEntity
  public static class Supertype extends SuperSupertype {}

  @QueryEntity
  public static class Entity1 extends Supertype {}

  @QueryEntity
  public static class Entity2 extends Supertype {}

  @Test
  public void test() {
    assertThat(QInheritance9Test_Entity1.entity1.id).isNotNull();
    assertThat(QInheritance9Test_Entity1.entity1.version).isNotNull();
    assertThat(QInheritance9Test_Entity2.entity2.id).isNotNull();
    assertThat(QInheritance9Test_Entity2.entity2.version).isNotNull();
  }
}
