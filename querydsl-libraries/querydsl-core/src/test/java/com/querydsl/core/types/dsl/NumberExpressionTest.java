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

import org.junit.Test;

public class NumberExpressionTest {

  private NumberPath<Integer> intPath = new NumberPath<>(Integer.class, "int");

  @Test
  public void between_start_given() {
    assertThat(intPath.between(1L, null)).isEqualTo(intPath.goe(1L));
  }

  @Test
  public void between_end_given() {
    assertThat(intPath.between(null, 3L)).isEqualTo(intPath.loe(3L));
  }
}
