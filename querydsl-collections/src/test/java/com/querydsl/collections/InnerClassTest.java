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

import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import org.junit.Test;

public class InnerClassTest {

  public static class Example {

    public String getId() {
      return null;
    }
  }

  @Test
  public void query() {
    Example example = alias(Example.class);
    assertFalse(
        CollQueryFactory.<Example>from(
                $(example), Collections.<Example>singletonList(new Example()))
            .where($(example.getId()).isNull())
            .fetch()
            .isEmpty());
    assertTrue(
        CollQueryFactory.<Example>from(
                $(example), Collections.<Example>singletonList(new Example()))
            .where($(example.getId()).isNotNull())
            .fetch()
            .isEmpty());
  }
}
