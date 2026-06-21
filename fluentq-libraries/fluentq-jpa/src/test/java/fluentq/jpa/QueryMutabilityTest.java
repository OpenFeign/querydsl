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

import fluentq.core.QueryMutability;
import fluentq.jpa.domain.sql.SAnimal_;
import fluentq.jpa.hibernate.sql.HibernateSQLQuery;
import fluentq.sql.DerbyTemplates;
import fluentq.sql.SQLTemplates;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.hibernate.Session;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class QueryMutabilityTest {

  private static final SQLTemplates derbyTemplates = new DerbyTemplates();

  private Session session;

  protected HibernateSQLQuery<?> query() {
    return new HibernateSQLQuery<Void>(session, derbyTemplates);
  }

  public void setSession(Session session) {
    this.session = session;
  }

  @Test
  @Disabled
  public void queryMutability()
      throws SecurityException,
          IllegalArgumentException,
          NoSuchMethodException,
          IllegalAccessException,
          InvocationTargetException,
          IOException {
    var cat = new SAnimal_("cat");
    HibernateSQLQuery<?> query = query().from(cat);
    new QueryMutability(query).test(cat.id, cat.name);
  }

  @Test
  public void clone_() {
    var cat = new SAnimal_("cat");
    HibernateSQLQuery<?> query = query().from(cat).where(cat.name.isNotNull());
    HibernateSQLQuery<?> query2 = query.clone(session);
    assertThat(query2.getMetadata().getJoins()).isEqualTo(query.getMetadata().getJoins());
    assertThat(query2.getMetadata().getWhere()).isEqualTo(query.getMetadata().getWhere());
    // query2.fetch(cat.id);
  }
}
