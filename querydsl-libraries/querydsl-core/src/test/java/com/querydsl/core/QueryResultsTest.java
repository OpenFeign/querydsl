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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class QueryResultsTest {

  private List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

  private QueryResults<Integer> results = new QueryResults<>(list, 10L, 0L, 20);

  @Test
  void getResults() {
    assertThat(results.getResults()).containsExactlyElementsOf(list);
  }

  @Test
  void getTotal() {
    assertThat(results.getTotal()).isEqualTo(20L);
  }

  @Test
  void isEmpty() {
    assertThat(results.isEmpty()).isFalse();
  }

  @Test
  void getLimit() {
    assertThat(results.getLimit()).isEqualTo(10L);
  }

  @Test
  void getOffset() {
    assertThat(results.getOffset()).isZero();
  }

  @Test
  void emptyResults() {
    QueryResults<Object> empty = QueryResults.emptyResults();
    assertThat(empty.isEmpty()).isTrue();
    assertThat(empty.getLimit()).isEqualTo(Long.MAX_VALUE);
    assertThat(empty.getOffset()).isZero();
    assertThat(empty.getTotal()).isZero();
    assertThat(empty.getResults()).containsExactlyElementsOf(Collections.emptyList());
  }
}
