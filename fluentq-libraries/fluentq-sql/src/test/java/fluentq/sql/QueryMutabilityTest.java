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
package fluentq.sql;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.QueryMutability;
import fluentq.sql.domain.QSurvey;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fluentq.core.testutil.Derby")
public class QueryMutabilityTest {

  private static final QSurvey survey = new QSurvey("survey");

  private Connection connection;

  @BeforeEach
  public void setUp() throws Exception {
    Connections.initDerby();
    connection = Connections.getConnection();
  }

  @AfterEach
  public void tearDown() throws SQLException {
    Connections.close();
  }

  @Test
  public void test()
      throws IOException,
          SecurityException,
          IllegalArgumentException,
          NoSuchMethodException,
          IllegalAccessException,
          InvocationTargetException {
    SQLQuery<?> query = new SQLQuery<Void>(connection, DerbyTemplates.DEFAULT);
    query.from(survey);
    query.addListener(new TestLoggingListener());
    new QueryMutability(query).test(survey.id, survey.name);
  }

  @Test
  public void clone_() {
    SQLQuery<?> query = new SQLQuery<Void>(DerbyTemplates.DEFAULT).from(survey);
    SQLQuery<?> query2 = query.clone(connection);
    assertThat(query2.getMetadata().getJoins()).isEqualTo(query.getMetadata().getJoins());
    assertThat(query2.getMetadata().getWhere()).isEqualTo(query.getMetadata().getWhere());
    query2.select(survey.id).fetch();
  }
}
