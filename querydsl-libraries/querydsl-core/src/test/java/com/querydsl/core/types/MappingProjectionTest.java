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

import com.mysema.commons.lang.Pair;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Test;

public class MappingProjectionTest {

  StringPath str1 = Expressions.stringPath("str1");
  StringPath str2 = Expressions.stringPath("str2");

  @SuppressWarnings("serial")
  @Test
  public void two_args() {
    MappingProjection<Pair<String, String>> mapping =
        new MappingProjection<>(Pair.class, str1, str2) {
          @Override
          protected Pair<String, String> map(Tuple row) {
            return Pair.of(row.get(str1), row.get(str2));
          }
        };

    var pair = mapping.newInstance("1", "2");
    assertThat(pair.getFirst()).isEqualTo("1");
    assertThat(pair.getSecond()).isEqualTo("2");
  }

  @SuppressWarnings("serial")
  @Test
  public void single_arg() {
    MappingProjection<String> mapping =
        new MappingProjection<>(String.class, str1) {
          @Override
          protected String map(Tuple row) {
            return row.get(str1);
          }
        };

    assertThat(mapping.newInstance("1")).isEqualTo("1");
  }

  @Test
  public void distinct_expressions() {
    MappingProjection<Pair<String, String>> mapping =
        new MappingProjection<>(Pair.class, str1, str1) {
          @Override
          protected Pair<String, String> map(Tuple row) {
            return Pair.of(row.get(str1), row.get(str1));
          }
        };

    assertThat(mapping.getArgs()).hasSize(1);
    assertThat(mapping.newInstance("1")).isEqualTo(Pair.of("1", "1"));
  }
}
