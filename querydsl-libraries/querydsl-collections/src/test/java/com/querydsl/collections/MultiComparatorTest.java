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
package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.utils.Evaluator;
import org.junit.Test;

public class MultiComparatorTest {

  private final Evaluator<Object[]> evaluator =
      new Evaluator<>() {
        @Override
        public Object[] evaluate(Object... args) {
          return args;
        }

        @Override
        public Class<? extends Object[]> getType() {
          return Object[].class;
        }
      };

  @Test
  public void test() {
    var comparator =
        new MultiComparator<>(evaluator, new boolean[] {true, true}, new boolean[] {true, true});
    assertThat(comparator.compare(new Object[] {"a", "b"}, new Object[] {"a", "c"}) < 0).isTrue();
    assertThat(comparator.compare(new Object[] {"b", "a"}, new Object[] {"a", "b"}) > 0).isTrue();
    assertThat(comparator.compare(new Object[] {"b", "b"}, new Object[] {"b", "b"}) == 0).isTrue();
  }
}
