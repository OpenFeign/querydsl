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

import com.mysema.commons.lang.IteratorAdapter;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class PagingTest extends AbstractQueryTest {

  private List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

  private NumberPath<Integer> var = Expressions.numberPath(Integer.class, "var");

  @Test
  public void test() {
    assertResultSize(9, 9, QueryModifiers.EMPTY);
    assertResultSize(9, 2, new QueryModifiers(2L, null));
    assertResultSize(9, 2, new QueryModifiers(2L, 0L));
    assertResultSize(9, 2, new QueryModifiers(2L, 3L));
    assertResultSize(9, 9, new QueryModifiers(20L, null));
    assertResultSize(9, 9, new QueryModifiers(20L, 0L));
    assertResultSize(9, 5, new QueryModifiers(20L, 4L));
    assertResultSize(9, 0, new QueryModifiers(10L, 9L));
  }

  private void assertResultSize(int total, int size, QueryModifiers modifiers) {
    // via fetch
    assertThat(createQuery(modifiers).select(var).fetch()).hasSize(size);

    // via results
    QueryResults<?> results = createQuery(modifiers).select(var).fetchResults();
    assertThat(results.getTotal()).isEqualTo(total);
    assertThat(results.getResults()).hasSize(size);

    // via fetchCount (ignore limit and offset)
    assertThat(createQuery(modifiers).fetchCount()).isEqualTo(total);

    // via iterator
    assertThat(IteratorAdapter.asList(createQuery(modifiers).select(var).iterate())).hasSize(size);
  }

  private CollQuery<?> createQuery(QueryModifiers modifiers) {
    CollQuery<?> query = new CollQuery<Void>().from(var, ints);
    if (modifiers != null) {
      query.restrict(modifiers);
    }
    return query;
  }
}
