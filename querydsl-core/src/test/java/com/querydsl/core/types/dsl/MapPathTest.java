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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.querydsl.core.types.ConstantImpl;
import org.junit.Test;

public class MapPathTest {

  private MapPath<String, String, StringPath> mapPath =
      new MapPath<String, String, StringPath>(String.class, String.class, StringPath.class, "p");

  @Test
  public void get() {
    assertNotNull(mapPath.get("X"));
    assertNotNull(mapPath.get(ConstantImpl.create("X")));
  }

  @Test
  public void getKeyType() {
    assertEquals(String.class, mapPath.getKeyType());
  }

  @Test
  public void getValueType() {
    assertEquals(String.class, mapPath.getValueType());
  }

  @Test
  public void getParameter() {
    assertEquals(String.class, mapPath.getParameter(0));
    assertEquals(String.class, mapPath.getParameter(1));
  }
}
