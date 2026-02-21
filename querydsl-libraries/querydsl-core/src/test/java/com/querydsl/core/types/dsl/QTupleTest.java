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

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import org.junit.jupiter.api.Test;

class QTupleTest {

  private StringPath first = new StringPath("x");

  private NumberPath<Integer> second = new NumberPath<>(Integer.class, "y");

  private BooleanPath third = new BooleanPath("z");

  private QTuple tupleExpression = Projections.tuple(first, second, third);

  @Test
  void newInstanceObjectArray() {
    var tuple = tupleExpression.newInstance("1", 42, true);
    assertThat(tuple.size()).isEqualTo(3);
    assertThat(tuple.get(0, String.class)).isEqualTo("1");
    assertThat(tuple.get(1, Integer.class)).isEqualTo(Integer.valueOf(42));
    assertThat(tuple.get(2, Boolean.class)).isEqualTo(Boolean.TRUE);
    assertThat(tuple.get(first)).isEqualTo("1");
    assertThat(tuple.get(second)).isEqualTo(Integer.valueOf(42));
    assertThat(tuple.get(third)).isEqualTo(Boolean.TRUE);
  }
}
