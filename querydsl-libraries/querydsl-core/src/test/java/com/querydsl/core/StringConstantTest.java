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
import org.junit.jupiter.api.Test;

class StringConstantTest {

  @Test
  void test() {
    assertThat(expr("ab").append("c")).hasToString("abc");
    assertThat(expr("bc").prepend("a")).hasToString("abc");
    assertThat(expr("ABC").lower()).hasToString("abc");
    assertThat(expr("abc").upper()).hasToString("ABC");
    assertThat(expr("abc").substring(0, 2)).hasToString("ab");
  }

  @Test
  void test2() {
    assertThat(expr("ab").append(expr("c"))).hasToString("abc");
    assertThat(expr("bc").prepend(expr("a"))).hasToString("abc");
    assertThat(expr("ABC").lower()).hasToString("abc");
    assertThat(expr("abc").upper()).hasToString("ABC");
    assertThat(expr("abc").substring(0, 2)).hasToString("ab");
  }

  private StringExpression expr(String str) {
    return StringConstant.create(str);
  }
}
