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

import com.querydsl.core.annotations.QueryEntity;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class GenericSignatureTest {

  @QueryEntity
  @SuppressWarnings("unchecked")
  public static class Entity<T extends Entity<T>> {

    // collection
    Collection<Entity> rawCollection;

    Collection<Entity<T>> genericCollection;

    Collection<T> genericCollection2;

    // list
    List<Entity> rawList;

    List<Entity<T>> genericList;

    List<T> genericList2;

    // set
    Set<Entity> rawSet;

    Set<Entity<T>> genericSet;

    Set<T> genericSet2;

    // map
    Map<String, Entity> rawMap;

    Map<String, Entity<T>> genericMap;

    Map<String, T> genericMap2;
  }

  @Test
  public void test() {
    QGenericSignatureTest_Entity entity = QGenericSignatureTest_Entity.entity;
    // collection
    assertThat(entity.rawCollection.getParameter(0)).isEqualTo(Entity.class);
    assertThat(entity.genericCollection.getParameter(0)).isEqualTo(Entity.class);
    assertThat(entity.genericCollection2.getParameter(0)).isEqualTo(Entity.class);

    // list
    assertThat(entity.rawList.getParameter(0)).isEqualTo(Entity.class);
    assertThat(entity.genericList.getParameter(0)).isEqualTo(Entity.class);
    assertThat(entity.genericList2.getParameter(0)).isEqualTo(Entity.class);

    // set
    assertThat(entity.rawSet.getParameter(0)).isEqualTo(Entity.class);
    assertThat(entity.genericSet.getParameter(0)).isEqualTo(Entity.class);
    assertThat(entity.genericSet2.getParameter(0)).isEqualTo(Entity.class);

    // map
    assertThat(entity.rawMap.getParameter(1)).isEqualTo(Entity.class);
    assertThat(entity.genericMap.getParameter(1)).isEqualTo(Entity.class);
    assertThat(entity.genericMap2.getParameter(1)).isEqualTo(Entity.class);
  }
}
