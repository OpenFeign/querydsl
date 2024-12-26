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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.hibernate.HibernateQuery;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Before;
import org.junit.Test;

public class JPQLQueryTest {

  private QCat cat = QCat.cat;

  private HibernateQuery<?> query = new HibernateQuery<Void>();

  @Before
  public void setUp() {
    query.from(cat);
  }

  @Test(expected = IllegalArgumentException.class)
  public void innerJoinPEntityOfPPEntityOfP() {
    query.innerJoin(cat.mate, cat.mate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void innerJoinPathOfQextendsCollectionOfPPathOfP() {
    query.innerJoin(cat.kittens, cat.mate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void joinPEntityOfPPEntityOfP() {
    query.join(cat.mate, cat.mate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void joinPathOfQextendsCollectionOfPPathOfP() {
    query.join(cat.kittens, cat.mate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void leftJoinPEntityOfPPEntityOfP() {
    query.leftJoin(cat.mate, cat.mate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void leftJoinPathOfQextendsCollectionOfPPathOfP() {
    query.leftJoin(cat.kittens, cat.mate);
  }

  @Test
  public void toString_() {
    assertThat(new HibernateQuery<Void>().toString()).isEmpty();
    assertThat(new JPAQuery<Void>().toString()).isEmpty();
    assertThat(new HibernateQuery<Void>().select(cat)).hasToString("select cat");
    assertThat(new JPAQuery<Void>().select(cat)).hasToString("select cat");
    assertThat(new HibernateQuery<Void>().from(cat).toString())
        .isEqualTo("select cat\nfrom Cat cat");
    assertThat(new JPAQuery<Void>().from(cat)).hasToString("select cat\nfrom Cat cat");
  }
}
