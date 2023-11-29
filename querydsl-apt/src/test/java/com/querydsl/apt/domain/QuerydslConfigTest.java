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

import static org.junit.Assert.assertEquals;

import com.querydsl.core.annotations.Config;
import com.querydsl.core.annotations.QueryEntity;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class QuerydslConfigTest {

  @Config(entityAccessors = true)
  @QueryEntity
  public static class Superclass {

    Entity prop3;
  }

  @Config(entityAccessors = true, listAccessors = true, mapAccessors = true)
  @QueryEntity
  public static class Entity extends Superclass {

    Entity prop1;

    Entity prop2;

    List<Entity> entityList;

    Map<String, Entity> entityMap;
  }

  @Test
  public void long_path() {
    assertEquals(
        "entity.prop1.prop2.prop1",
        QQuerydslConfigTest_Entity.entity.prop1().prop2().prop1().toString());
  }
}
