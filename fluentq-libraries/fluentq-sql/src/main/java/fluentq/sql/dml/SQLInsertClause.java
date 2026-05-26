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
package fluentq.sql.dml;

import fluentq.sql.Configuration;
import fluentq.sql.RelationalPath;
import fluentq.sql.SQLQuery;
import fluentq.sql.SQLTemplates;
import java.sql.Connection;
import java.util.function.Supplier;

/**
 * SQLInsertClause defines an INSERT INTO clause If you need to subtype this, use {@link
 * AbstractSQLInsertClause} instead.
 *
 * @author tiwe
 */
public class SQLInsertClause extends AbstractSQLInsertClause<SQLInsertClause> {
  public SQLInsertClause(Connection connection, SQLTemplates templates, RelationalPath<?> entity) {
    this(connection, new Configuration(templates), entity);
  }

  public SQLInsertClause(
      Connection connection,
      SQLTemplates templates,
      RelationalPath<?> entity,
      SQLQuery<?> subQuery) {
    this(connection, new Configuration(templates), entity, subQuery);
  }

  public SQLInsertClause(
      Connection connection,
      Configuration configuration,
      RelationalPath<?> entity,
      SQLQuery<?> subQuery) {
    super(connection, configuration, entity, subQuery);
  }

  public SQLInsertClause(
      Connection connection, Configuration configuration, RelationalPath<?> entity) {
    super(connection, configuration, entity);
  }

  public SQLInsertClause(
      Supplier<Connection> connection,
      Configuration configuration,
      RelationalPath<?> entity,
      SQLQuery<?> subQuery) {
    super(connection, configuration, entity, subQuery);
  }

  public SQLInsertClause(
      Supplier<Connection> connection, Configuration configuration, RelationalPath<?> entity) {
    super(connection, configuration, entity);
  }
}
