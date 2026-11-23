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

import fluentq.jpa.domain.Cat;
import fluentq.jpa.domain.QCat;
import fluentq.jpa.hibernate.HibernateQuery;
import fluentq.jpa.testutil.HibernateTestExtension;
import org.hibernate.Session;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Disabled
@ExtendWith(HibernateTestExtension.class)
public class UniqueResultsTest implements HibernateTest {

  private Session session;

  @Test
  public void test() {
    session.persist(new Cat("Bob1", 1));
    session.persist(new Cat("Bob2", 2));
    session.persist(new Cat("Bob3", 3));

    assertThat(
            query()
                .from(QCat.cat)
                .orderBy(QCat.cat.name.asc())
                .offset(0)
                .limit(1)
                .select(QCat.cat.id)
                .fetchOne())
        .isEqualTo(Integer.valueOf(1));
    assertThat(
            query()
                .from(QCat.cat)
                .orderBy(QCat.cat.name.asc())
                .offset(1)
                .limit(1)
                .select(QCat.cat.id)
                .fetchOne())
        .isEqualTo(Integer.valueOf(2));
    assertThat(
            query()
                .from(QCat.cat)
                .orderBy(QCat.cat.name.asc())
                .offset(2)
                .limit(1)
                .select(QCat.cat.id)
                .fetchOne())
        .isEqualTo(Integer.valueOf(3));

    assertThat(query().from(QCat.cat).select(QCat.cat.count()).fetchOne())
        .isEqualTo(Long.valueOf(3));
  }

  private HibernateQuery<?> query() {
    return new HibernateQuery<Void>(session);
  }

  @Override
  public void setSession(Session session) {
    this.session = session;
  }
}
