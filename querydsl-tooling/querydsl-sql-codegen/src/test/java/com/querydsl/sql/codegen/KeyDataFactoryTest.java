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
package com.querydsl.sql.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.AbstractJDBCTest;
import java.sql.SQLException;
import org.junit.Test;

public class KeyDataFactoryTest extends AbstractJDBCTest {

  @Test
  public void test() throws SQLException {
    statement.execute("drop table employee if exists");
    statement.execute("drop table survey if exists");
    statement.execute("drop table date_test if exists");
    statement.execute("drop table date_time_test if exists");

    statement.execute(
        """
        create table survey (id int, name varchar(30), \
        CONSTRAINT PK_survey PRIMARY KEY (id, name))\
        """);

    statement.execute(
        """
        create table employee(\
        id INT, \
        superior_id int, \
        superior_id2 int, \
        survey_id int, \
        survey_name varchar(30), \
        CONSTRAINT PK_employee PRIMARY KEY (id), \
        CONSTRAINT FK_survey FOREIGN KEY (survey_id, survey_name) REFERENCES survey(id,name), \
        CONSTRAINT FK_superior2 FOREIGN KEY (superior_id) REFERENCES employee(id), \
        CONSTRAINT FK_superior1 FOREIGN KEY (superior_id2) REFERENCES employee(id))\
        """);

    var keyDataFactory = new KeyDataFactory(new DefaultNamingStrategy(), "Q", "", "test", false);

    var md = connection.getMetaData();

    // EMPLOYEE

    // primary key
    var primaryKeys = keyDataFactory.getPrimaryKeys(md, null, null, "EMPLOYEE");
    assertThat(primaryKeys).isNotEmpty();
    // inverse foreign keys sorted in abc
    var exportedKeys = keyDataFactory.getExportedKeys(md, null, null, "EMPLOYEE");
    assertThat(exportedKeys).hasSize(2);
    var exportedKeysIterator = exportedKeys.keySet().iterator();
    assertThat(exportedKeysIterator.next()).isEqualTo("FK_SUPERIOR1");
    assertThat(exportedKeysIterator.next()).isEqualTo("FK_SUPERIOR2");
    // foreign keys sorted in abc
    var importedKeys = keyDataFactory.getImportedKeys(md, null, null, "EMPLOYEE");
    assertThat(importedKeys).hasSize(3);
    var importedKeysIterator = importedKeys.keySet().iterator();
    assertThat(importedKeysIterator.next()).isEqualTo("FK_SUPERIOR1");
    assertThat(importedKeysIterator.next()).isEqualTo("FK_SUPERIOR2");
    assertThat(importedKeysIterator.next()).isEqualTo("FK_SURVEY");

    // SURVEY

    // primary key
    primaryKeys = keyDataFactory.getPrimaryKeys(md, null, null, "SURVEY");
    assertThat(primaryKeys).isNotEmpty();
    // inverse foreign keys
    exportedKeys = keyDataFactory.getExportedKeys(md, null, null, "SURVEY");
    assertThat(exportedKeys).isNotEmpty();
    assertThat(exportedKeys).containsKey("FK_SURVEY");
    // foreign keys
    importedKeys = keyDataFactory.getImportedKeys(md, null, null, "SURVEY");
    assertThat(importedKeys).isEmpty();
  }
}
