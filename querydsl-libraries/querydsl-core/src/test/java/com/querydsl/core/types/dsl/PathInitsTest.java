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
package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PathInitsTest {

  @Test
  public void defaultInits() {
    assertThat(PathInits.DEFAULT.isInitialized("")).isFalse();
  }

  @Test
  public void isInitialized() {
    var inits = new PathInits(".2").get("");
    assertThat(inits.isInitialized("1")).isFalse();
    assertThat(inits.isInitialized("2")).isTrue();
  }

  @Test
  public void wildcard() {
    assertThat(new PathInits("*").isInitialized("")).isTrue();
  }

  @Test
  public void wildcard2() {
    var inits = new PathInits(".*").get("");
    assertThat(inits.isInitialized("1")).isTrue();
    assertThat(inits.isInitialized("2")).isTrue();
  }

  @Test
  public void deep_wildcard() {
    var inits = new PathInits("*.*").get("");
    assertThat(inits.isInitialized("1")).isTrue();
    assertThat(inits.isInitialized("2")).isTrue();
  }
}
