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
package com.querydsl.core.support;

import static com.querydsl.core.alias.Alias.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.alias.Alias;
import com.querydsl.core.domain.QCommonPersistence;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class QueryMixinTest {

  private QueryMixin<?> mixin = new QueryMixin<Void>();

  private QCommonPersistence entity =
      new QCommonPersistence(PathMetadataFactory.forVariable("entity"));

  @Test
  public void where_null() {
    mixin.where((Predicate) null);
  }

  @Test
  public void getJoins_with_condition() {
    mixin.innerJoin(entity);
    mixin.on(entity.version.isNull(), entity.version.isNotNull());

    assertEquals(1, mixin.getMetadata().getJoins().size());
    JoinExpression je = mixin.getMetadata().getJoins().get(0);
    assertEquals(entity, je.getTarget());
    assertEquals(
        Expressions.allOf(entity.version.isNull(), entity.version.isNotNull()), je.getCondition());
  }

  @Test
  public void getJoins_no_condition() {
    mixin.innerJoin(entity);

    assertEquals(1, mixin.getMetadata().getJoins().size());
    JoinExpression je = mixin.getMetadata().getJoins().get(0);
    assertEquals(entity, je.getTarget());
    assertNull(je.getCondition());
  }

  @Test
  public void innerJoin() {
    DummyEntity e = Alias.alias(DummyEntity.class);
    DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
    DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
    DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");

    // inner join
    mixin.innerJoin($(e));
    mixin.innerJoin($(e.getOther()), $(e2));
    mixin.innerJoin($(e.getList()), $(e3));
    mixin.innerJoin($(e.getList()));
    mixin.innerJoin($(e.getMap()), $(e4));
    mixin.innerJoin($(e.getMap()));

    assertEquals(6, mixin.getMetadata().getJoins().size());
  }

  @Test
  public void join() {
    DummyEntity e = Alias.alias(DummyEntity.class);
    DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
    DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
    DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");

    // inner join
    mixin.innerJoin($(e));
    mixin.innerJoin($(e.getOther()), $(e2));
    mixin.innerJoin($(e.getList()), $(e3));
    mixin.innerJoin($(e.getList()));
    mixin.innerJoin($(e.getMap()), $(e4));
    mixin.innerJoin($(e.getMap()));

    assertEquals(6, mixin.getMetadata().getJoins().size());
  }

  @Test
  public void joins() {
    DummyEntity e = Alias.alias(DummyEntity.class);
    DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");

    mixin.join($(e));
    mixin.on($(e).isNotNull());
    mixin.join($(e.getOther()), $(e2));
    mixin.on($(e).isNotNull());

    assertEquals(2, mixin.getMetadata().getJoins().size());
  }

  @Test
  public void leftJoin() {
    DummyEntity e = Alias.alias(DummyEntity.class);
    DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
    DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
    DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");

    // left join
    mixin.leftJoin($(e));
    mixin.leftJoin($(e.getOther()), $(e2));
    mixin.leftJoin($(e.getList()), $(e3));
    mixin.leftJoin($(e.getList()));
    mixin.leftJoin($(e.getMap()), $(e4));
    mixin.leftJoin($(e.getMap()));

    assertEquals(6, mixin.getMetadata().getJoins().size());
  }

  @Test
  public void rightJoin() {
    DummyEntity e = Alias.alias(DummyEntity.class);
    DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
    DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
    DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");

    // right join
    mixin.rightJoin($(e));
    mixin.rightJoin($(e.getOther()), $(e2));
    mixin.rightJoin($(e.getList()), $(e3));
    mixin.rightJoin($(e.getList()));
    mixin.rightJoin($(e.getMap()), $(e4));
    mixin.rightJoin($(e.getMap()));

    assertEquals(6, mixin.getMetadata().getJoins().size());
  }

  @Test
  public void fullJoin() {
    DummyEntity e = Alias.alias(DummyEntity.class);
    DummyEntity e2 = Alias.alias(DummyEntity.class, "e2");
    DummyEntity e3 = Alias.alias(DummyEntity.class, "e3");
    DummyEntity e4 = Alias.alias(DummyEntity.class, "e4");

    // full join
    mixin.fullJoin($(e));
    mixin.fullJoin($(e.getOther()), $(e2));
    mixin.fullJoin($(e.getList()), $(e3));
    mixin.fullJoin($(e.getList()));
    mixin.fullJoin($(e.getMap()), $(e4));
    mixin.fullJoin($(e.getMap()));

    assertEquals(6, mixin.getMetadata().getJoins().size());
  }
}
