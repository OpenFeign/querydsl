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
package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.PathMetadataFactory;
import org.junit.jupiter.api.Test;

class ListPathTest {

  private ListPath<String, StringPath> stringPath =
      new ListPath<>(String.class, StringPath.class, PathMetadataFactory.forVariable("stringPath"));

  @Test
  void toString_() {
    assertThat(stringPath).hasToString("stringPath");
    assertThat(stringPath.any()).hasToString("any(stringPath)");
    assertThat(stringPath.get(0).equalsIgnoreCase("X")).hasToString("eqIc(stringPath.get(0),X)");
    assertThat(stringPath.any().equalsIgnoreCase("X")).hasToString("eqIc(any(stringPath),X)");
    assertThat(stringPath.get(ConstantImpl.create(0))).hasToString("stringPath.get(0)");
  }

  @Test
  void getElementType() {
    assertThat(stringPath.getElementType()).isEqualTo(String.class);
  }

  @Test
  void getParameter() {
    assertThat(stringPath.getParameter(0)).isEqualTo(String.class);
  }
}
