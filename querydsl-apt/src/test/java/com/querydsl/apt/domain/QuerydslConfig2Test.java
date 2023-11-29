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

import static org.junit.Assert.assertNotNull;

import com.querydsl.core.annotations.Config;
import com.querydsl.core.annotations.QueryEntity;
import org.junit.Test;

public class QuerydslConfig2Test {

  @Config(entityAccessors = true)
  @QueryEntity
  public static class Entity extends Superclass {

    Entity prop1;
  }

  @Config(createDefaultVariable = false)
  @QueryEntity
  public static class Entity2 extends Superclass2 {

    Entity prop1;
  }

  @QueryEntity
  public static class Superclass {

    Entity prop2;
  }

  @Config(entityAccessors = true)
  @QueryEntity
  public static class Superclass2 {

    Entity prop2;
  }

  @Test
  public void test() {
    assertNotNull(QQuerydslConfig2Test_Entity.entity);
  }

  @Test(expected = NoSuchFieldException.class)
  public void create_default_variable() throws SecurityException, NoSuchFieldException {
    QQuerydslConfig2Test_Entity2.class.getField("entity2");
  }
}
