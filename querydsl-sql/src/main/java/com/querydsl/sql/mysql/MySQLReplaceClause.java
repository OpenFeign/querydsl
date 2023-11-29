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
package com.querydsl.sql.mysql;

import com.querydsl.core.QueryFlag.Position;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.dml.SQLInsertClause;
import java.sql.Connection;

/**
 * {@code MySQLReplaceClause} is a REPLACE INTO clause
 *
 * <p>REPLACE works exactly like INSERT, except that if an old row in the table has the same value
 * as a new row for a PRIMARY KEY or a UNIQUE index, the old row is deleted before the new row is
 * inserted.
 *
 * @author tiwe
 */
public class MySQLReplaceClause extends SQLInsertClause {

  protected static final String REPLACE_INTO = "replace into ";

  public MySQLReplaceClause(
      Connection connection, SQLTemplates templates, RelationalPath<?> entity) {
    super(connection, templates, entity);
    addFlag(Position.START_OVERRIDE, REPLACE_INTO);
  }

  public MySQLReplaceClause(
      Connection connection, Configuration configuration, RelationalPath<?> entity) {
    super(connection, configuration, entity);
    addFlag(Position.START_OVERRIDE, REPLACE_INTO);
  }
}
