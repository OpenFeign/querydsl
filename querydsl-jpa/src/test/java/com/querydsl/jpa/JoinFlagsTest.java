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

import static com.querydsl.jpa.Constants.cat;
import static com.querydsl.jpa.Constants.cat1;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class JoinFlagsTest extends AbstractQueryTest {
  @Test
  public void fetch() {
    QueryHelper query = query().from(cat).innerJoin(cat.mate, cat1).fetchJoin();
    assertThat(query.toString())
        .isEqualTo("select cat\nfrom Cat cat\n  inner join fetch cat.mate as cat1");
  }

  @Test
  public void rightJoin() {
    QueryHelper query = query().from(cat).rightJoin(cat.mate, cat1);
    assertThat(query.toString())
        .isEqualTo("select cat\nfrom Cat cat\n  right join cat.mate as cat1");
  }
}
