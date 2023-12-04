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
import com.querydsl.core.annotations.QueryInit;
import org.junit.Test;

public class AnimalTest {

  @QueryEntity
  public static class Animal {

    public String name;
  }

  @QueryEntity
  public static class Cat extends Animal {

    @QueryInit("name")
    public Cat mate;
  }

  @Test
  public void properties_are_copied_from_super() {
    assertThat(QAnimalTest_Cat.cat.name == QAnimalTest_Cat.cat._super.name)
        .as("direct copy of StringPath field failed")
        .isTrue();
  }
}
