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

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class DistinctTest extends AbstractQueryTest {

  private NumberPath<Integer> intVar1 = Expressions.numberPath(Integer.class, "var1");
  private NumberPath<Integer> intVar2 = Expressions.numberPath(Integer.class, "var2");
  private List<Integer> list1 = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
  private List<Integer> list2 = Arrays.asList(2, 2, 3, 3, 3, 4, 4, 4, 4, 4);

  @Test
  public void singleSource() {
    assertThat(CollQueryFactory.from(intVar1, list1).fetch()).isEqualTo(list1);
    assertThat(CollQueryFactory.from(intVar1, list1).distinct().fetch())
        .isEqualTo(Arrays.asList(1, 2, 3, 4));
    assertThat(CollQueryFactory.from(intVar2, list2).distinct().fetch())
        .isEqualTo(Arrays.asList(2, 3, 4));

    assertThat(CollQueryFactory.from(intVar2, list2).distinct().fetch())
        .isEqualTo(Arrays.asList(2, 3, 4));
  }

  @Test
  public void bothSources() {
    assertThat(
            CollQueryFactory.from(intVar1, list1)
                .from(intVar2, list2)
                .select(intVar1, intVar2)
                .fetch())
        .hasSize(100);
    assertThat(
            CollQueryFactory.from(intVar1, list1)
                .from(intVar2, list2)
                .distinct()
                .select(intVar1, intVar2)
                .fetch())
        .hasSize(12);

    assertThat(
            CollQueryFactory.from(intVar1, list1)
                .from(intVar2, list2)
                .distinct()
                .select(intVar1, intVar2)
                .fetch())
        .hasSize(12);
  }

  @Test
  public void countDistinct() {
    assertThat(CollQueryFactory.from(intVar1, list1).fetchCount()).isEqualTo(10);
    assertThat(CollQueryFactory.from(intVar1, list1).distinct().fetchCount()).isEqualTo(4);
    assertThat(CollQueryFactory.from(intVar2, list2).distinct().fetchCount()).isEqualTo(3);

    assertThat(CollQueryFactory.from(intVar2, list2).distinct().fetchCount()).isEqualTo(3);
  }

  @Test
  public void null_() {
    assertThat(CollQueryFactory.<Integer>from(intVar1, Arrays.asList(null, 1)).distinct().fetch())
        .isEqualTo(Arrays.asList(null, 1));
  }
}
