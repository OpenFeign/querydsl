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
package com.querydsl.sql.postgresql;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.*;
import java.sql.Connection;
import java.util.function.Supplier;

/**
 * PostgreSQL specific implementation of SQLQueryFactory
 *
 * @author tiwe
 */
public class PostgreSQLQueryFactory extends AbstractSQLQueryFactory<PostgreSQLQuery<?>> {

  public PostgreSQLQueryFactory(Configuration configuration, Supplier<Connection> connection) {
    super(configuration, connection);
  }

  public PostgreSQLQueryFactory(Supplier<Connection> connection) {
    this(new Configuration(new PostgreSQLTemplates()), connection);
  }

  public PostgreSQLQueryFactory(SQLTemplates templates, Supplier<Connection> connection) {
    this(new Configuration(templates), connection);
  }

  @Override
  public PostgreSQLQuery<?> query() {
    return new PostgreSQLQuery<Void>(connection, configuration);
  }

  @Override
  public <T> PostgreSQLQuery<T> select(Expression<T> expr) {
    return query().select(expr);
  }

  @Override
  public PostgreSQLQuery<Tuple> select(Expression<?>... exprs) {
    return query().select(exprs);
  }

  @Override
  public <T> PostgreSQLQuery<T> selectDistinct(Expression<T> expr) {
    return query().select(expr).distinct();
  }

  @Override
  public PostgreSQLQuery<Tuple> selectDistinct(Expression<?>... exprs) {
    return query().select(exprs).distinct();
  }

  @Override
  public PostgreSQLQuery<Integer> selectZero() {
    return select(Expressions.ZERO);
  }

  @Override
  public PostgreSQLQuery<Integer> selectOne() {
    return select(Expressions.ONE);
  }

  @Override
  public <T> PostgreSQLQuery<T> selectFrom(RelationalPath<T> expr) {
    return select(expr).from(expr);
  }
}
