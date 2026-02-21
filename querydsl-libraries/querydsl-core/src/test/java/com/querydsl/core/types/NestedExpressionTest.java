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

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class NestedExpressionTest {

  StringPath str1 = Expressions.stringPath("str1");
  StringPath str2 = Expressions.stringPath("str2");
  StringPath str3 = Expressions.stringPath("str3");
  StringPath str4 = Expressions.stringPath("str3");

  Concatenation concat1 = new Concatenation(new Concatenation(str1, str2), str3);
  Concatenation concat2 =
      new Concatenation(new Concatenation(str1, new Concatenation(str2, str3)), str4);

  @Test
  void wrapped_projection_has_right_arguments() {
    FactoryExpression<String> wrapped = FactoryExpressionUtils.wrap(concat1);
    assertThat(wrapped.getArgs()).containsExactlyElementsOf(Arrays.asList(str1, str2, str3));
  }

  @Test
  void wrapped_projection_compresses_projection() {
    FactoryExpression<String> wrapped = FactoryExpressionUtils.wrap(concat1);
    assertThat(wrapped.newInstance("1", "2", "3")).isEqualTo("123");
  }

  @Test
  void deeply_wrapped_projection_has_right_arguments() {
    FactoryExpression<String> wrapped = FactoryExpressionUtils.wrap(concat2);
    assertThat(wrapped.getArgs()).containsExactlyElementsOf(Arrays.asList(str1, str2, str3, str4));
  }

  @Test
  void deeply_wrapped_projection_compresses_projection() {
    FactoryExpression<String> wrapped = FactoryExpressionUtils.wrap(concat2);
    assertThat(wrapped.newInstance("1", "2", "3", "4")).isEqualTo("1234");
  }
}
