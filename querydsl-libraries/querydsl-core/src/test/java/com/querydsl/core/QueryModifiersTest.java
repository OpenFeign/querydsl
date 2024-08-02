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
import java.util.List;
import org.junit.Test;

public class QueryModifiersTest {

  @Test
  public void limit() {
    var modifiers = QueryModifiers.limit(12L);
    assertThat(modifiers.getLimit()).isEqualTo(Long.valueOf(12));
    assertThat(modifiers.getOffset()).isNull();
    assertThat(modifiers.isRestricting()).isTrue();
  }

  @Test
  public void offset() {
    var modifiers = QueryModifiers.offset(12L);
    assertThat(modifiers.getOffset()).isEqualTo(Long.valueOf(12));
    assertThat(modifiers.getLimit()).isNull();
    assertThat(modifiers.isRestricting()).isTrue();
  }

  @Test
  public void both() {
    var modifiers = new QueryModifiers(1L, 2L);
    assertThat(modifiers.getLimit()).isEqualTo(Long.valueOf(1));
    assertThat(modifiers.getOffset()).isEqualTo(Long.valueOf(2));
    assertThat(modifiers.isRestricting()).isTrue();
  }

  @Test
  public void empty() {
    var modifiers = new QueryModifiers(null, null);
    assertThat(modifiers.getLimit()).isNull();
    assertThat(modifiers.getOffset()).isNull();
    assertThat(modifiers.isRestricting()).isFalse();
  }

  @Test
  public void hashCode_() {
    var modifiers1 = new QueryModifiers(null, null);
    var modifiers2 = new QueryModifiers(1L, null);
    var modifiers3 = new QueryModifiers(null, 1L);

    assertThat(QueryModifiers.EMPTY.hashCode()).isEqualTo(modifiers1.hashCode());
    assertThat(QueryModifiers.limit(1L).hashCode()).isEqualTo(modifiers2.hashCode());
    assertThat(QueryModifiers.offset(1L).hashCode()).isEqualTo(modifiers3.hashCode());
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalLimit() {
    QueryModifiers.limit(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegalOffset() {
    QueryModifiers.offset(-1);
  }

  @Test
  public void subList() {
    List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
    assertThat(QueryModifiers.offset(2).subList(ints)).isEqualTo(Arrays.asList(3, 4, 5));
    assertThat(QueryModifiers.limit(3).subList(ints)).isEqualTo(Arrays.asList(1, 2, 3));
    assertThat(new QueryModifiers(3L, 1L).subList(ints)).isEqualTo(Arrays.asList(2, 3, 4));
  }
}
