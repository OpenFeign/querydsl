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
package com.querydsl.jpa;

import static com.querydsl.jpa.Constants.*;

import org.junit.Test;

public class AggregationTest extends AbstractQueryTest {

  @Test
  public void max() {
    assertToString("max(cat.bodyWeight)", cat.bodyWeight.max());
  }

  @Test
  public void min() {
    assertToString("min(cat.bodyWeight)", cat.bodyWeight.min());
  }

  @Test
  public void avg() {
    assertToString("avg(cat.bodyWeight)", cat.bodyWeight.avg());
  }

  @Test
  public void count() {
    assertToString("count(cat)", cat.count());
  }

  @Test
  public void countDistinct() {
    assertToString("count(distinct cat)", cat.countDistinct());
  }
}
