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
import static org.junit.Assert.assertNotNull;

import jakarta.persistence.*;
import org.junit.Test;

public class EmbeddedTest {

  @Entity
  public static class EntityClass extends AbstractEntity<SubEntityCode> {}

  @MappedSuperclass
  public abstract static class AbstractEntity<C extends EntityCode> {

    @Embedded
    @Column(name = "code", nullable = false, unique = true)
    C code;
  }

  @MappedSuperclass
  public static class EntityCode {

    @Column(name = "code", unique = true)
    String code;
  }

  @Embeddable
  public static class SubEntityCode extends EntityCode {

    String property;
  }

  @Test
  public void entityClass() {
    assertNotNull(QEmbeddedTest_EntityClass.entityClass.code.property);
    assertEquals(SubEntityCode.class, QEmbeddedTest_EntityClass.entityClass.code.getType());
  }
}
