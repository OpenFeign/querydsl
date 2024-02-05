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
import static org.junit.jupiter.api.Assertions.*;

import com.querydsl.core.QueryMutability;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.hibernate.HibernateQuery;
import com.querydsl.jpa.testutil.HibernateTestRunner;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.hibernate.Session;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(HibernateTestRunner.class)
public class HibernateQueryMutabilityTest implements HibernateTest {

  private Session session;

  protected HibernateQuery<?> query() {
    return new HibernateQuery<Void>(session);
  }

  @Override
  public void setSession(Session session) {
    this.session = session;
  }

  @Test
  public void test()
      throws SecurityException,
          IllegalArgumentException,
          NoSuchMethodException,
          IllegalAccessException,
          InvocationTargetException,
          IOException {
    QCat cat = QCat.cat;
    HibernateQuery<?> query = query().from(cat);
    new QueryMutability(query).test(cat, cat.name);
  }

  @Test
  public void clone_() {
    QCat cat = QCat.cat;
    HibernateQuery<?> query = query().from(cat).where(cat.name.isNotNull());
    HibernateQuery<?> query2 = query.clone(session);
    assertThat(query2.getMetadata().getJoins()).isEqualTo(query.getMetadata().getJoins());
    assertThat(query2.getMetadata().getWhere()).isEqualTo(query.getMetadata().getWhere());
    query2.select(cat).fetch();
  }
}
