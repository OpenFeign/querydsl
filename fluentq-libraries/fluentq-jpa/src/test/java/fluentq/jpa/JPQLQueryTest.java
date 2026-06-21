/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fluentq.jpa.domain.QCat;
import fluentq.jpa.hibernate.HibernateQuery;
import fluentq.jpa.impl.JPAQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JPQLQueryTest {

  private QCat cat = QCat.cat;

  private HibernateQuery<?> query = new HibernateQuery<Void>();

  @BeforeEach
  public void setUp() {
    query.from(cat);
  }

  @Test
  public void innerJoinPEntityOfPPEntityOfP() {
    assertThatThrownBy(
            () -> {
              query.innerJoin(cat.mate, cat.mate);
            })
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void innerJoinPathOfQextendsCollectionOfPPathOfP() {
    assertThatThrownBy(
            () -> {
              query.innerJoin(cat.kittens, cat.mate);
            })
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void joinPEntityOfPPEntityOfP() {
    assertThatThrownBy(
            () -> {
              query.join(cat.mate, cat.mate);
            })
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void joinPathOfQextendsCollectionOfPPathOfP() {
    assertThatThrownBy(
            () -> {
              query.join(cat.kittens, cat.mate);
            })
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void leftJoinPEntityOfPPEntityOfP() {
    assertThatThrownBy(
            () -> {
              query.leftJoin(cat.mate, cat.mate);
            })
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void leftJoinPathOfQextendsCollectionOfPPathOfP() {
    assertThatThrownBy(
            () -> {
              query.leftJoin(cat.kittens, cat.mate);
            })
        .isInstanceOf(IllegalArgumentException.class);
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
