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

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.junit.Test;

public class HierarchyTest {

  @Entity
  public static class A {

    B b;

    A(B b) {
      this.b = b;
    }

    B getB() {
      return b;
    }
  }

  @Entity
  public static class A2 extends A {

    // XXX: uncomment @Comment to break generation - QA2.a2.b() will then
    // return B instead of B2
    @Column int foo;

    A2(B2 b2) {
      super(b2);
    }

    @Override
    @QueryType(PropertyType.ENTITY)
    B2 getB() {
      return (B2) super.getB();
    }
  }

  @Entity
  public static class B {}

  @Entity
  public static class B2 extends B {}

  @Test
  public void test() {
    QHierarchyTest_B2 qb2 = QHierarchyTest_A2.a2.b;
    assertNotNull(qb2);
  }
}
