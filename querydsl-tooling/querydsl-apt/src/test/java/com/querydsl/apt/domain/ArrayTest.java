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
import org.junit.Test;

public class ArrayTest {

  @QueryEntity
  public static class ArrayTestEntity {

    ArrayTestEntity[] entityArray;

    int[] primitiveArray;

    String[] stringArray;
  }

  @Test
  public void test() {
    var entity = QArrayTest_ArrayTestEntity.arrayTestEntity;
    assertThat(entity.entityArray.getType()).isEqualTo(ArrayTestEntity[].class);
    assertThat(entity.entityArray.get(0).getType()).isEqualTo(ArrayTestEntity.class);
  }
}
