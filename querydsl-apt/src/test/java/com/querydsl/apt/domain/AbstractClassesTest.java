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

import com.querydsl.core.types.dsl.NumberPath;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings({"rawtypes", "serial", "unchecked"})
public class AbstractClassesTest {

  public interface Archetype<PK extends Serializable, DO extends Serializable>
      extends Serializable, Comparable<DO> {}

  @MappedSuperclass
  public abstract static class BaseArchetype<PK extends Serializable, DO extends Serializable>
      implements Archetype<PK, DO> {

    @Id @GeneratedValue PK id;
    String name;
    String description;

    public BaseArchetype() {}

    public int compareTo(BaseArchetype o) {
      return 0;
    }

    public boolean equals(Object o) {
      return o == this;
    }
  }

  @Entity
  public static class Grant<P extends Party, S extends Party> extends BaseArchetype<P, S> {

    public int compareTo(S o) {
      return 0;
    }

    public boolean equals(Object o) {
      return o == this;
    }
  }

  @Entity
  public static class Party extends BaseArchetype<Long, Party> {

    public Party() {}

    public int compareTo(Party o) {
      return 0;
    }

    public boolean equals(Object o) {
      return o == this;
    }
  }

  @Test
  public void grant_id_type() {
    Assert.assertEquals(
        QAbstractClassesTest_Party.class, QAbstractClassesTest_Grant.grant.id.getClass());
    assertEquals(Party.class, QAbstractClassesTest_Grant.grant.id.getType());
  }

  @Test
  public void party_id_type() {
    assertEquals(NumberPath.class, QAbstractClassesTest_Party.party.id.getClass());
    assertEquals(Long.class, QAbstractClassesTest_Party.party.id.getType());
  }
}
