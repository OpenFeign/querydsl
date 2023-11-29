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

import org.junit.Test;

public class PathBuilderFactoryTest {

  @Test
  public void create() {
    PathBuilderFactory factory = new PathBuilderFactory("");
    PathBuilder<Object> pathBuilder = factory.create(Object.class);
    assertEquals("object", pathBuilder.toString());
    assertEquals(Object.class, pathBuilder.getType());

    pathBuilder.get("prop", Object.class);
    pathBuilder.get("prop", String.class);
    pathBuilder.get("prop", Object.class);
  }

  @Test
  public void create_withSuffix() {
    PathBuilderFactory factory = new PathBuilderFactory("_");
    PathBuilder<Object> pathBuilder = factory.create(Object.class);
    assertEquals("object_", pathBuilder.toString());
    assertEquals(Object.class, pathBuilder.getType());

    pathBuilder.get("prop", Object.class);
    pathBuilder.get("prop", String.class);
    pathBuilder.get("prop", Object.class);
  }
}
