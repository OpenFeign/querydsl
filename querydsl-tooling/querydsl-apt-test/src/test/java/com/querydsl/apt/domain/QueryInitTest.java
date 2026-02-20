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
import com.querydsl.core.annotations.QueryInit;
import org.junit.Test;

public class QueryInitTest {

  private static final QQueryInitTest_PEntity e1 = QQueryInitTest_PEntity.pEntity;

  private static final QQueryInitTest_PEntity2 e2 = QQueryInitTest_PEntity2.pEntity2;

  @QueryEntity
  public static class PEntity {

    @QueryInit("e3.e4")
    public PEntity2 e2;

    @QueryInit({"e3.*", "e33.e4", "e333"})
    public PEntity2 e22;

    @QueryInit("*")
    public PEntity2 e222;

    public PEntity2 type;

    public PEntity2 inits;
  }

  @QueryEntity
  public static class PEntity2Super {

    public PEntity3 e333;

    @QueryInit("e4")
    public PEntity3 e3333;
  }

  @QueryEntity
  public static class PEntity2 extends PEntity2Super {

    public PEntity3 e3;

    public PEntity3 e33;
  }

  @QueryEntity
  public static class PEntity3 {

    public PEntity4 e4;

    public PEntity4 e44;
  }

  @QueryEntity
  public static class PEntity4 {

    public PEntity e1;

    public PEntity e11;
  }

  @Test
  public void basic_inits() {
    // e2
    assertThat(e1.e2).isNotNull();
    assertThat(e1.e2.e3.e4).isNotNull();
    assertThat(e1.e2.e33).isNull();
    assertThat(e1.e2.e3.e44).isNull();

    // e22
    assertThat(e1.e22.e33.e4).isNotNull();
    assertThat(e1.e22.e33.e44).isNull();
    assertThat(e1.e22.e333).isNotNull();
  }

  @Test
  public void deep_super_inits() {
    assertThat(e1.e22._super.e333).isNotNull();
  }

  @Test
  public void root_super_inits() {
    assertThat(e2.e3333.e4).isNotNull();
    assertThat(e2._super.e3333.e4).isNotNull();
  }
}
