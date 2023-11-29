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
package com.querydsl.apt.domain.p6;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TypeTest {

  @Test
  public void test() {
    QType1 type1 = QType1.type1;
    QType2 type2 = QType2.type2;
    assertThat(type1.property.getType()).isEqualTo(type2.getType());
    assertThat(type1.property.getClass()).isEqualTo(type2.getClass());
  }
}
