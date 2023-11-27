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
package com.querydsl.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.StringExpression;
import org.junit.Test;

public class StringConstantTest {

  @Test
  public void test() {
    assertThat(expr("ab").append("c").toString()).isEqualTo("abc");
    assertThat(expr("bc").prepend("a").toString()).isEqualTo("abc");
    assertThat(expr("ABC").lower().toString()).isEqualTo("abc");
    assertThat(expr("abc").upper().toString()).isEqualTo("ABC");
    assertThat(expr("abc").substring(0, 2).toString()).isEqualTo("ab");
  }

  @Test
  public void test2() {
    assertThat(expr("ab").append(expr("c")).toString()).isEqualTo("abc");
    assertThat(expr("bc").prepend(expr("a")).toString()).isEqualTo("abc");
    assertThat(expr("ABC").lower().toString()).isEqualTo("abc");
    assertThat(expr("abc").upper().toString()).isEqualTo("ABC");
    assertThat(expr("abc").substring(0, 2).toString()).isEqualTo("ab");
  }

  private StringExpression expr(String str) {
    return StringConstant.create(str);
  }
}
