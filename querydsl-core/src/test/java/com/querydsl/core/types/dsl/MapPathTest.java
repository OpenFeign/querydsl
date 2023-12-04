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
import org.junit.Test;

public class MapPathTest {

  private MapPath<String, String, StringPath> mapPath =
      new MapPath<String, String, StringPath>(String.class, String.class, StringPath.class, "p");

  @Test
  public void get() {
    assertThat(mapPath.get("X")).isNotNull();
    assertThat(mapPath.get(ConstantImpl.create("X"))).isNotNull();
  }

  @Test
  public void getKeyType() {
    assertThat(mapPath.getKeyType()).isEqualTo(String.class);
  }

  @Test
  public void getValueType() {
    assertThat(mapPath.getValueType()).isEqualTo(String.class);
  }

  @Test
  public void getParameter() {
    assertThat(mapPath.getParameter(0)).isEqualTo(String.class);
    assertThat(mapPath.getParameter(1)).isEqualTo(String.class);
  }
}
