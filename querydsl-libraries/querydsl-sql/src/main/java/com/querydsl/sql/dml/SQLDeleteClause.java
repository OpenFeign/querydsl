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
package com.querydsl.sql.dml;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLTemplates;
import java.sql.Connection;
import java.util.function.Supplier;

/**
 * {@code SQLDeleteClause} defines a DELETE clause. If you need to subtype this, use {@link
 * AbstractSQLDeleteClause} instead.
 *
 * @author tiwe
 */
public class SQLDeleteClause extends AbstractSQLDeleteClause<SQLDeleteClause> {
  public SQLDeleteClause(Connection connection, SQLTemplates templates, RelationalPath<?> entity) {
    super(connection, new Configuration(templates), entity);
  }

  public SQLDeleteClause(
      Connection connection, Configuration configuration, RelationalPath<?> entity) {
    super(connection, configuration, entity);
  }

  public SQLDeleteClause(
      Supplier<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
    super(connection, configuration, entity);
  }
}
