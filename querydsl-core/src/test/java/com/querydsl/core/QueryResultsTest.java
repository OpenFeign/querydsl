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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class QueryResultsTest {

  private List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

  private QueryResults<Integer> results = new QueryResults<Integer>(list, 10L, 0L, 20);

  @Test
  public void getResults() {
    assertEquals(list, results.getResults());
  }

  @Test
  public void getTotal() {
    assertEquals(20L, results.getTotal());
  }

  @Test
  public void isEmpty() {
    assertFalse(results.isEmpty());
  }

  @Test
  public void getLimit() {
    assertEquals(10L, results.getLimit());
  }

  @Test
  public void getOffset() {
    assertEquals(0L, results.getOffset());
  }

  @Test
  public void emptyResults() {
    QueryResults<Object> empty = QueryResults.emptyResults();
    assertTrue(empty.isEmpty());
    assertEquals(Long.MAX_VALUE, empty.getLimit());
    assertEquals(0L, empty.getOffset());
    assertEquals(0L, empty.getTotal());
    assertEquals(Collections.emptyList(), empty.getResults());
  }
}
