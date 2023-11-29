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

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.sql.DataSource;

/**
 * Factory class for query and DML clause creation
 *
 * @author tiwe
 */
public class SQLQueryFactory extends AbstractSQLQueryFactory<SQLQuery<?>> {

  static class DataSourceProvider implements Supplier<Connection> {

    private final DataSource ds;

    DataSourceProvider(DataSource ds) {
      this.ds = ds;
    }

    @Override
    public Connection get() {
      try {
        return ds.getConnection();
      } catch (SQLException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }

  public SQLQueryFactory(SQLTemplates templates, Supplier<Connection> connection) {
    this(new Configuration(templates), connection);
  }

  public SQLQueryFactory(Configuration configuration, Supplier<Connection> connProvider) {
    super(configuration, connProvider);
  }

  public SQLQueryFactory(Configuration configuration, DataSource dataSource) {
    this(configuration, dataSource, true);
  }

  public SQLQueryFactory(Configuration configuration, DataSource dataSource, boolean release) {
    super(configuration, new DataSourceProvider(dataSource));
    if (release) {
      configuration.addListener(SQLCloseListener.DEFAULT);
    }
  }

  @Override
  public SQLQuery<?> query() {
    return new SQLQuery<Void>(connection, configuration);
  }

  @Override
  public <T> SQLQuery<T> select(Expression<T> expr) {
    return query().select(expr);
  }

  @Override
  public SQLQuery<Tuple> select(Expression<?>... exprs) {
    return query().select(exprs);
  }

  @Override
  public <T> SQLQuery<T> selectDistinct(Expression<T> expr) {
    return query().select(expr).distinct();
  }

  @Override
  public SQLQuery<Tuple> selectDistinct(Expression<?>... exprs) {
    return query().select(exprs).distinct();
  }

  @Override
  public SQLQuery<Integer> selectZero() {
    return select(Expressions.ZERO);
  }

  @Override
  public SQLQuery<Integer> selectOne() {
    return select(Expressions.ONE);
  }

  @Override
  public <T> SQLQuery<T> selectFrom(RelationalPath<T> expr) {
    return select(expr).from(expr);
  }
}
