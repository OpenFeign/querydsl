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

import com.querydsl.core.domain.query.QAnimal;
import com.querydsl.jpa.hibernate.HibernateQueryFactory;
import java.util.function.Supplier;
import org.easymock.EasyMock;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

public class HibernateQueryFactoryTest {

  private HibernateQueryFactory queryFactory;

  @Before
  public void setUp() {
    Supplier<Session> provider = () -> EasyMock.<Session>createNiceMock(Session.class);
    queryFactory = new HibernateQueryFactory(JPQLTemplates.DEFAULT, provider);
  }

  @Test
  public void query() {
    assertThat(queryFactory.query()).isNotNull();
  }

  @Test
  public void from() {
    assertThat(queryFactory.from(QAnimal.animal)).isNotNull();
  }

  @Test
  public void delete() {
    assertThat(queryFactory.delete(QAnimal.animal)).isNotNull();
  }

  @Test
  public void update() {
    assertThat(queryFactory.update(QAnimal.animal)).isNotNull();
  }

  @Test
  public void insert() {
    assertThat(queryFactory.insert(QAnimal.animal)).isNotNull();
  }
}
