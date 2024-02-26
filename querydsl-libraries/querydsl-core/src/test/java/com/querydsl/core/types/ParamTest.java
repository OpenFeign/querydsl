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

import com.querydsl.core.types.dsl.Param;
import org.junit.Test;

public class ParamTest {

  Param<String> param11 = new Param<String>(String.class, "param1");
  Param<String> param12 = new Param<String>(String.class, "param1");
  Param<String> param2 = new Param<String>(String.class, "param2");
  Param<Object> param3 = new Param<Object>(Object.class, "param1");
  Param<String> param4 = new Param<String>(String.class);

  @Test
  public void identity() {
    assertThat(param12).isEqualTo(param11);
    assertThat(param11.equals(param2)).isFalse();
    assertThat(param11.equals(param3)).isFalse();
    assertThat(param11.equals(param4)).isFalse();
  }

  @Test
  public void anon() {
    assertThat(param4.getName()).isNotNull();
  }

  @Test
  public void getNotSetMessage() {
    assertThat(param11.getNotSetMessage()).isEqualTo("The parameter param1 needs to be set");
    assertThat(param4.getNotSetMessage())
        .isEqualTo("A parameter of type java.lang.String was not set");
  }
}
