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
package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ConstantImplTest {

  @Test
  public void create() {
    assertThat(ConstantImpl.create(true)).isNotNull();
    assertThat(ConstantImpl.create((byte) 1)).isNotNull();
    assertThat(ConstantImpl.create(ConstantImplTest.class)).isNotNull();
    assertThat(ConstantImpl.create(1)).isNotNull();
    assertThat(ConstantImpl.create(1L)).isNotNull();
    assertThat(ConstantImpl.create((short) 1)).isNotNull();
    assertThat(ConstantImpl.create("x")).isNotNull();
    //        assertNotNull(ConstantImpl.create("x",true));
  }
}
