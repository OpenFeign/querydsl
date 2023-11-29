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
package com.querydsl.core.types;

import static org.junit.Assert.assertEquals;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Collections;
import org.junit.Test;

public class DeepPopulationTest {

  public static class Entity1 {

    private Entity2 entity2;

    public Entity2 getEntity2() {
      return entity2;
    }

    public void setEntity2(Entity2 entity2) {
      this.entity2 = entity2;
    }
  }

  public static class Entity2 {

    private String name, id;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  @Test
  public void deep_population_via_qBean() {
    StringPath name = Expressions.stringPath("name");
    StringPath id = Expressions.stringPath("id");
    QBean<Entity2> entity2Bean = new QBean<Entity2>(Entity2.class, name, id);
    QBean<Entity1> entity1Bean =
        new QBean<Entity1>(Entity1.class, Collections.singletonMap("entity2", entity2Bean));

    Entity1 entity1 = FactoryExpressionUtils.wrap(entity1Bean).newInstance("nameX", "idX");
    assertEquals("nameX", entity1.getEntity2().getName());
    assertEquals("idX", entity1.getEntity2().getId());
  }

  @Test
  public void deep_population_via_qTuple() {
    StringPath name = Expressions.stringPath("name");
    StringPath id = Expressions.stringPath("id");
    QBean<Entity2> entity2Bean = new QBean<Entity2>(Entity2.class, name, id);
    QTuple tupleExpr = new QTuple(entity2Bean);

    Tuple tuple = FactoryExpressionUtils.wrap(tupleExpr).newInstance("nameX", "idX");
    assertEquals("nameX", tuple.get(entity2Bean).getName());
    assertEquals("idX", tuple.get(entity2Bean).getId());
  }
}
