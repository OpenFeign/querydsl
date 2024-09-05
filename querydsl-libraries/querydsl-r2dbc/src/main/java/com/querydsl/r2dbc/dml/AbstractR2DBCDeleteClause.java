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
package com.querydsl.r2dbc.dml;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.dml.reactive.ReactiveDeleteClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.ValidatingVisitor;
import com.querydsl.r2dbc.Configuration;
import com.querydsl.r2dbc.R2DBCConnectionProvider;
import com.querydsl.r2dbc.R2dbcUtils;
import com.querydsl.r2dbc.SQLSerializer;
import com.querydsl.r2dbc.binding.BindTarget;
import com.querydsl.r2dbc.binding.StatementWrapper;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.jetbrains.annotations.Range;
import reactor.core.publisher.Mono;

/**
 * Provides a base class for dialect-specific DELETE clauses.
 *
 * @param <C> The type extending this class.
 * @author mc_fish
 */
public abstract class AbstractR2DBCDeleteClause<C extends AbstractR2DBCDeleteClause<C>>
    extends AbstractR2DBCClause<C> implements ReactiveDeleteClause<C> {

  protected static final Logger logger =
      Logger.getLogger(AbstractR2DBCDeleteClause.class.getName());

  protected static final ValidatingVisitor validatingVisitor =
      new ValidatingVisitor(
          """
          Undeclared path '%s'. \
          A delete operation can only reference a single table. \
          Consider this alternative: DELETE ... WHERE EXISTS (subquery)""");

  protected final RelationalPath<?> entity;

  protected DefaultQueryMetadata metadata = new DefaultQueryMetadata();

  protected transient String queryString;

  protected transient List<Object> constants;

  public AbstractR2DBCDeleteClause(
      Connection connection, Configuration configuration, RelationalPath<?> entity) {
    super(configuration, connection);
    this.entity = entity;
    metadata.addJoin(JoinType.DEFAULT, entity);
    metadata.setValidatingVisitor(validatingVisitor);
  }

  public AbstractR2DBCDeleteClause(
      R2DBCConnectionProvider connectionProvider,
      Configuration configuration,
      RelationalPath<?> entity) {
    super(configuration, connectionProvider);
    this.entity = entity;
    metadata.addJoin(JoinType.DEFAULT, entity);
    metadata.setValidatingVisitor(validatingVisitor);
  }

  /**
   * Add the given String literal at the given position as a query flag
   *
   * @param position position
   * @param flag query flag
   * @return the current object
   */
  public C addFlag(Position position, String flag) {
    metadata.addFlag(new QueryFlag(position, flag));
    return (C) this;
  }

  /**
   * Add the given Expression at the given position as a query flag
   *
   * @param position position
   * @param flag query flag
   * @return the current object
   */
  public C addFlag(Position position, Expression<?> flag) {
    metadata.addFlag(new QueryFlag(position, flag));
    return (C) this;
  }

  @Override
  public void clear() {
    metadata = new DefaultQueryMetadata();
    metadata.addJoin(JoinType.DEFAULT, entity);
    metadata.setValidatingVisitor(validatingVisitor);
  }

  private Statement prepareStatementAndSetParameters(
      Connection connection, SQLSerializer serializer) {
    var constants = serializer.getConstants();
    var originalSql = serializer.toString();
    queryString =
        R2dbcUtils.replaceBindingArguments(
            configuration.getBindMarkerFactory().create(), constants, originalSql);

    logQuery(logger, queryString, serializer.getConstants());
    var statement = connection.createStatement(queryString);
    BindTarget bindTarget = new StatementWrapper(statement);

    setParameters(
        bindTarget,
        configuration.getBindMarkerFactory().create(),
        serializer.getConstants(),
        serializer.getConstantPaths(),
        metadata.getParams());

    return statement;
  }

  private SQLSerializer createSerializerAndSerialize() {
    var serializer = createSerializer(true);
    serializer.serializeDelete(metadata, entity);
    return serializer;
  }

  private Mono<Statement> createStatement() {
    return getConnection()
        .map(
            connection -> {
              var serializer = createSerializerAndSerialize();
              return prepareStatementAndSetParameters(connection, serializer);
            });
  }

  @Override
  public Mono<Long> execute() {
    return getConnection()
        .flatMap(
            connection -> {
              return createStatement()
                  .flatMap(statement -> Mono.from(statement.execute()))
                  .flatMap(result -> Mono.from(result.getRowsUpdated()))
                  .map(Long::valueOf)
                  .doOnError(e -> Mono.error(configuration.translate(queryString, constants, e)));
            });
  }

  @Override
  public List<SQLBindings> getSQL() {
    var serializer = createSerializer(true);
    serializer.serializeDelete(metadata, entity);
    return Collections.singletonList(createBindings(metadata, serializer));
  }

  public C where(Predicate p) {
    metadata.addWhere(p);
    return (C) this;
  }

  @Override
  public C where(Predicate... o) {
    for (Predicate p : o) {
      metadata.addWhere(p);
    }
    return (C) this;
  }

  public C limit(@Range(from = 0, to = Integer.MAX_VALUE) long limit) {
    metadata.setModifiers(QueryModifiers.limit(limit));
    return (C) this;
  }

  @Override
  public String toString() {
    var serializer = createSerializer(true);
    serializer.serializeDelete(metadata, entity);
    return serializer.toString();
  }
}
