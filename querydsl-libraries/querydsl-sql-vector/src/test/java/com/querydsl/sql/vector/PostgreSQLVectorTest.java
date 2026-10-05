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

@Tag("com.querydsl.core.testutil.PostgreSQL")
class PostgreSQLVectorTest extends AbstractSQLVectorTest {

  static Connection pgvector() throws SQLException {
    return DriverManager.getConnection(
        "jdbc:postgresql://localhost:5433/querydsl", "querydsl", "querydsl");
  }

  static void createDocumentTable(Connection connection) throws SQLException {
    try (var stmt = connection.createStatement()) {
      stmt.execute("create extension if not exists vector");
      stmt.execute("drop table if exists vector_document");
      stmt.execute(
          "create table vector_document (id bigint primary key, title varchar(100), embedding"
              + " vector(3))");
    }
  }

  @Override
  protected Connection connect() throws SQLException {
    return pgvector();
  }

  @Override
  protected PGvectorTemplates templates() {
    return new PGvectorTemplates();
  }

  @Override
  protected void createTable(Connection connection) throws SQLException {
    createDocumentTable(connection);
  }
}
