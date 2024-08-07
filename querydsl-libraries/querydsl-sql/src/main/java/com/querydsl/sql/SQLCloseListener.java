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

import com.querydsl.core.QueryException;
import java.sql.SQLException;

/**
 * {@code SQLCloseListener} closes the JDBC connection at the end of the query or clause execution
 */
public final class SQLCloseListener extends SQLBaseListener {

  public static final SQLCloseListener DEFAULT = new SQLCloseListener();

  private SQLCloseListener() {}

  @Override
  public void end(SQLListenerContext context) {
    var connection = context.getConnection();
    if (connection != null && context.getData(AbstractSQLQuery.PARENT_CONTEXT) == null) {
      try {
        connection.close();
      } catch (SQLException e) {
        throw new QueryException(e);
      }
    }
  }
}
