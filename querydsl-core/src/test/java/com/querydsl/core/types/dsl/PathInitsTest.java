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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PathInitsTest {

  @Test
  public void defaultInits() {
    assertFalse(PathInits.DEFAULT.isInitialized(""));
  }

  @Test
  public void isInitialized() {
    PathInits inits = new PathInits(".2").get("");
    assertFalse(inits.isInitialized("1"));
    assertTrue(inits.isInitialized("2"));
  }

  @Test
  public void wildcard() {
    assertTrue(new PathInits("*").isInitialized(""));
  }

  @Test
  public void wildcard2() {
    PathInits inits = new PathInits(".*").get("");
    assertTrue(inits.isInitialized("1"));
    assertTrue(inits.isInitialized("2"));
  }

  @Test
  public void deep_wildcard() {
    PathInits inits = new PathInits("*.*").get("");
    assertTrue(inits.isInitialized("1"));
    assertTrue(inits.isInitialized("2"));
  }
}
