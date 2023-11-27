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

import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Entity;
import org.junit.Test;

public class KeywordsTest {

  @Entity
  public static class Order {}

  @Entity
  public static class From {

    String from1;
  }

  @Entity
  public static class NonKeyword {}

  @PersistenceCapable
  public static class Distinct {

    String distinct;
  }

  @Test
  public void test() {
    assertThat(QKeywordsTest_Order.order.toString()).isEqualTo("order1");
    assertThat(QKeywordsTest_From.from.toString()).isEqualTo("from1");
    assertThat(QKeywordsTest_NonKeyword.nonKeyword.toString()).isEqualTo("nonKeyword");
    assertThat(QKeywordsTest_Distinct.distinct1.toString()).isEqualTo("distinct1");
  }
}
