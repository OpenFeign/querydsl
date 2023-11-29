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
package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public abstract class AbstractTest {

  private Class<?> cl;

  private com.querydsl.core.types.Expression<?> standardVariable;

  protected <T extends com.querydsl.core.types.Expression<?>> void start(
      Class<T> cl, T standardVariable) {
    this.cl = cl;
    this.standardVariable = standardVariable;
  }

  protected void match(Class<?> expectedType, String name)
      throws SecurityException, NoSuchFieldException {
    assertThat(expectedType.isAssignableFrom(cl.getField(name).getType()))
        .as(cl.getSimpleName() + "." + name + " failed")
        .isTrue();
  }

  protected void matchType(Class<?> expectedType, String name)
      throws NoSuchFieldException, IllegalAccessException {
    Class<?> type =
        ((com.querydsl.core.types.Expression) cl.getField(name).get(standardVariable)).getType();
    assertThat(expectedType.isAssignableFrom(type))
        .as(cl.getSimpleName() + "." + name + " failed")
        .isTrue();
  }

  protected void assertPresent(String name) {
    try {
      cl.getField(name);
    } catch (NoSuchFieldException e) {
      fail("", "Expected present field : " + cl.getSimpleName() + "." + name);
    }
  }

  protected void assertMissing(String name) {
    try {
      cl.getField(name);
      fail("", "Expected missing field : " + cl.getSimpleName() + "." + name);
    } catch (NoSuchFieldException e) {
      // expected
    }
  }
}
