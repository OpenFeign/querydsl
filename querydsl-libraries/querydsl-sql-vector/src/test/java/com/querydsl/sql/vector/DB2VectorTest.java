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
package com.querydsl.sql.vector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Tag;

@Tag("com.querydsl.core.testutil.DB2")
class DB2VectorTest extends AbstractSQLVectorTest {

  @Override
  protected Connection connect() throws SQLException {
    return DriverManager.getConnection("jdbc:db2://localhost:50000/sample", "db2inst1", "a3sd!fDj");
  }

  @Override
  protected DB2VectorTemplates createTemplates() {
    return new DB2VectorTemplates();
  }

  @Override
  protected void recreateVectorDocumentTable(Connection connection) throws SQLException {
    try (var statement = connection.createStatement()) {
      statement.execute(
          """
          begin
            declare continue handler for sqlstate '42704' begin end;
            execute immediate 'drop table vector_document';
          end\
          """);
      statement.execute(
          "create table vector_document (id bigint not null primary key, title varchar(100),"
              + " embedding vector(3, float32))");
    }
  }
}
