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
package com.querydsl.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class NullSafeComparableComparatorTest {

  private final NullSafeComparableComparator<String> comparator =
      new NullSafeComparableComparator<>();

  @Test
  public void null_before_object() {
    assertThat(comparator.compare(null, "X") < 0).isTrue();
  }

  @Test
  public void object_after_null() {
    assertThat(comparator.compare("X", null) > 0).isTrue();
  }

  @Test
  public void object_eq_object() {
    assertThat(comparator.compare("X", "X")).isEqualTo(0);
  }

  @Test
  public void object_lt_object() {
    assertThat(comparator.compare("X", "Y") < 0).isTrue();
  }

  @Test
  public void object_gt_object() {
    assertThat(comparator.compare("Z", "Y") > 0).isTrue();
  }
}
