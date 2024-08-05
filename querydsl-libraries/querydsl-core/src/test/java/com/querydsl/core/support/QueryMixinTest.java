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
import static org.assertj.core.api.Assertions.assertThat;

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

    assertThat(mixin.getMetadata().getJoins()).hasSize(1);
    var je = mixin.getMetadata().getJoins().getFirst();
    assertThat(je.getTarget()).isEqualTo(entity);
    assertThat(je.getCondition())
        .isEqualTo(Expressions.allOf(entity.version.isNull(), entity.version.isNotNull()));
  }

  @Test
  public void getJoins_no_condition() {
    mixin.innerJoin(entity);

    assertThat(mixin.getMetadata().getJoins()).hasSize(1);
    var je = mixin.getMetadata().getJoins().getFirst();
    assertThat(je.getTarget()).isEqualTo(entity);
    assertThat(je.getCondition()).isNull();
  }

  @Test
  public void innerJoin() {
    var e = Alias.alias(DummyEntity.class);
    var e2 = Alias.alias(DummyEntity.class, "e2");
    var e3 = Alias.alias(DummyEntity.class, "e3");
    var e4 = Alias.alias(DummyEntity.class, "e4");

    // inner join
    mixin.innerJoin($(e));
    mixin.innerJoin($(e.getOther()), $(e2));
    mixin.innerJoin($(e.getList()), $(e3));
    mixin.innerJoin($(e.getList()));
    mixin.innerJoin($(e.getMap()), $(e4));
    mixin.innerJoin($(e.getMap()));

    assertThat(mixin.getMetadata().getJoins()).hasSize(6);
  }

  @Test
  public void join() {
    var e = Alias.alias(DummyEntity.class);
    var e2 = Alias.alias(DummyEntity.class, "e2");
    var e3 = Alias.alias(DummyEntity.class, "e3");
    var e4 = Alias.alias(DummyEntity.class, "e4");

    // inner join
    mixin.innerJoin($(e));
    mixin.innerJoin($(e.getOther()), $(e2));
    mixin.innerJoin($(e.getList()), $(e3));
    mixin.innerJoin($(e.getList()));
    mixin.innerJoin($(e.getMap()), $(e4));
    mixin.innerJoin($(e.getMap()));

    assertThat(mixin.getMetadata().getJoins()).hasSize(6);
  }

  @Test
  public void joins() {
    var e = Alias.alias(DummyEntity.class);
    var e2 = Alias.alias(DummyEntity.class, "e2");

    mixin.join($(e));
    mixin.on($(e).isNotNull());
    mixin.join($(e.getOther()), $(e2));
    mixin.on($(e).isNotNull());

    assertThat(mixin.getMetadata().getJoins()).hasSize(2);
  }

  @Test
  public void leftJoin() {
    var e = Alias.alias(DummyEntity.class);
    var e2 = Alias.alias(DummyEntity.class, "e2");
    var e3 = Alias.alias(DummyEntity.class, "e3");
    var e4 = Alias.alias(DummyEntity.class, "e4");

    // left join
    mixin.leftJoin($(e));
    mixin.leftJoin($(e.getOther()), $(e2));
    mixin.leftJoin($(e.getList()), $(e3));
    mixin.leftJoin($(e.getList()));
    mixin.leftJoin($(e.getMap()), $(e4));
    mixin.leftJoin($(e.getMap()));

    assertThat(mixin.getMetadata().getJoins()).hasSize(6);
  }

  @Test
  public void rightJoin() {
    var e = Alias.alias(DummyEntity.class);
    var e2 = Alias.alias(DummyEntity.class, "e2");
    var e3 = Alias.alias(DummyEntity.class, "e3");
    var e4 = Alias.alias(DummyEntity.class, "e4");

    // right join
    mixin.rightJoin($(e));
    mixin.rightJoin($(e.getOther()), $(e2));
    mixin.rightJoin($(e.getList()), $(e3));
    mixin.rightJoin($(e.getList()));
    mixin.rightJoin($(e.getMap()), $(e4));
    mixin.rightJoin($(e.getMap()));

    assertThat(mixin.getMetadata().getJoins()).hasSize(6);
  }

  @Test
  public void fullJoin() {
    var e = Alias.alias(DummyEntity.class);
    var e2 = Alias.alias(DummyEntity.class, "e2");
    var e3 = Alias.alias(DummyEntity.class, "e3");
    var e4 = Alias.alias(DummyEntity.class, "e4");

    // full join
    mixin.fullJoin($(e));
    mixin.fullJoin($(e.getOther()), $(e2));
    mixin.fullJoin($(e.getList()), $(e3));
    mixin.fullJoin($(e.getList()));
    mixin.fullJoin($(e.getMap()), $(e4));
    mixin.fullJoin($(e.getMap()));

    assertThat(mixin.getMetadata().getJoins()).hasSize(6);
  }
}
