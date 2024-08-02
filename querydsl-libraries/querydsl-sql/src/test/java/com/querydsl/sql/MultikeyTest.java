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
package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MultikeyTest {

  Multikey multiKey1 = new Multikey();
  Multikey multiKey2 = new Multikey();

  @Test
  public void hashCode_() {
    var hashCode = multiKey1.hashCode();
    multiKey1.setId(1);
    assertThat(multiKey1.hashCode()).isEqualTo(hashCode);

    multiKey1.setId2("2");
    multiKey1.setId3(3);

    multiKey2.setId(1);
    multiKey2.setId2("2");
    multiKey2.setId3(3);

    assertThat(multiKey2.hashCode()).isEqualTo(multiKey1.hashCode());
  }

  @Test
  public void equals() {
    multiKey1.setId(1);
    multiKey1.setId2("2");
    multiKey1.setId3(3);

    assertThat(multiKey1.equals(multiKey2)).isFalse();
    multiKey2.setId(1);
    assertThat(multiKey1.equals(multiKey2)).isFalse();

    multiKey2.setId2("2");
    multiKey2.setId3(3);

    assertThat(multiKey2).isEqualTo(multiKey1);
  }

  @Test
  public void toString_() {
    assertThat(multiKey1).hasToString("Multikey#null;null;null");

    multiKey1.setId(1);
    multiKey1.setId2("2");
    multiKey1.setId3(3);
    assertThat(multiKey1).hasToString("Multikey#1;2;3");
  }
}
