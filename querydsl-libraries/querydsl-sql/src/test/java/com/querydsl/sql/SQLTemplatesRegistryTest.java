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
package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

class SQLTemplatesRegistryTest {

  private final SQLTemplatesRegistry registry = new SQLTemplatesRegistry();

  @Test
  void mariaDBDriver() throws SQLException {
    assertThat(registry.getTemplates(metadata("MariaDB", "11.8.9-MariaDB-ubu2404")))
        .isInstanceOf(MariaDBTemplates.class);
  }

  @Test
  void mariaDBBehindMySQLDriver() throws SQLException {
    assertThat(registry.getTemplates(metadata("MySQL", "11.8.9-MariaDB-ubu2404")))
        .isInstanceOf(MariaDBTemplates.class);
  }

  @Test
  void mySQL() throws SQLException {
    assertThat(registry.getTemplates(metadata("MySQL", "8.4.6")))
        .isExactlyInstanceOf(MySQLTemplates.class);
  }

  private static DatabaseMetaData metadata(String productName, String productVersion)
      throws SQLException {
    DatabaseMetaData metadata = EasyMock.createNiceMock(DatabaseMetaData.class);
    EasyMock.expect(metadata.getDatabaseProductName()).andReturn(productName).anyTimes();
    EasyMock.expect(metadata.getDatabaseProductVersion()).andReturn(productVersion).anyTimes();
    EasyMock.replay(metadata);
    return metadata;
  }
}
