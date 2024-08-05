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

import org.junit.Ignore;
import org.junit.Test;

public class CollQueryFunctionsTest {

  @Test
  public void coalesce() {
    assertThat(CollQueryFunctions.coalesce("1", null)).isEqualTo("1");
    assertThat(CollQueryFunctions.coalesce(null, "1", "2")).isEqualTo("1");
    assertThat(CollQueryFunctions.coalesce((Integer) null, null)).isNull();
  }

  @Test
  @Ignore
  public void likeSpeed() {
    // 3015
    final var iterations = 1000000;
    var start = System.currentTimeMillis();
    for (var i = 0; i < iterations; i++) {
      CollQueryFunctions.like("abcDOG", "%DOG");
      CollQueryFunctions.like("DOGabc", "DOG%");
      CollQueryFunctions.like("abcDOGabc", "%DOG%");
    }
    var duration = System.currentTimeMillis() - start;
    System.err.println(duration);
  }

  @Test
  public void like() {
    assertThat(CollQueryFunctions.like("abcDOG", "%DOG")).isTrue();
    assertThat(CollQueryFunctions.like("DOGabc", "DOG%")).isTrue();
    assertThat(CollQueryFunctions.like("abcDOGabc", "%DOG%")).isTrue();
  }

  @Test
  public void like_with_special_chars() {
    assertThat(CollQueryFunctions.like("$DOG", "$DOG")).isTrue();
    assertThat(CollQueryFunctions.like("$DOGabc", "$DOG%")).isTrue();
  }
}
