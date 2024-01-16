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

import com.querydsl.core.Target;
import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.Color;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.sql.SAnimal_;
import com.querydsl.jpa.hibernate.sql.HibernateSQLQuery;
import com.querydsl.jpa.testutil.HibernateTestRunner;
import com.querydsl.sql.SQLTemplates;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

@RunWith(HibernateTestRunner.class)
public class HibernateSQLBase extends AbstractSQLTest implements HibernateTest {

  @Rule @ClassRule public static TestRule targetRule = new TargetRule();

  private final SQLTemplates templates = Mode.getSQLTemplates();

  private final SAnimal_ cat = new SAnimal_("cat");

  private Session session;

  @Override
  protected HibernateSQLQuery<?> query() {
    return new HibernateSQLQuery<Void>(session, templates);
  }

  @Override
  public void setSession(Session session) {
    this.session = session;
  }

  @Before
  public void setUp() {
    if (query().from(cat).fetchCount() == 0) {
      session.save(new Cat("Beck", 1, Color.BLACK));
      session.save(new Cat("Kate", 2, Color.BLACK));
      session.save(new Cat("Kitty", 3, Color.BLACK));
      session.save(new Cat("Bobby", 4, Color.BLACK));
      session.save(new Cat("Harold", 5, Color.BLACK));
      session.save(new Cat("Tim", 6, Color.BLACK));
      session.flush();
    }
  }

  @Test
  public void entityQueries_createQuery() {
    SAnimal_ cat = new SAnimal_("cat");
    QCat catEntity = QCat.cat;

    Query query = query().from(cat).select(catEntity).createQuery();
    assertThat(query.list()).hasSize(6);
  }

  @Test
  @ExcludeIn(Target.MYSQL)
  public void entityQueries_createQuery2() {
    SAnimal_ cat = new SAnimal_("CAT");
    QCat catEntity = QCat.cat;

    Query query = query().from(cat).select(catEntity).createQuery();
    assertThat(query.list()).hasSize(6);
  }
}
