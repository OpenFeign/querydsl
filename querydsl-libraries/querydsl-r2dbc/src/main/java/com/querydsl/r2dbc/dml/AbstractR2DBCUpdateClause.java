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
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.dml.ReactiveUpdateClause;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.r2dbc.Configuration;
import com.querydsl.r2dbc.R2DBCConnectionProvider;
import com.querydsl.r2dbc.R2dbcUtils;
import com.querydsl.r2dbc.SQLSerializer;
import com.querydsl.r2dbc.binding.BindTarget;
import com.querydsl.r2dbc.binding.StatementWrapper;
import com.querydsl.r2dbc.types.Null;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.dml.Mapper;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.jetbrains.annotations.Range;
import reactor.core.publisher.Mono;

/**
 * Provides a base class for dialect-specific UPDATE clauses.
 *
 * @param <C> The type extending this class.
 * @author mc_fish
 */
public abstract class AbstractR2DBCUpdateClause<C extends AbstractR2DBCUpdateClause<C>>
    extends AbstractR2DBCClause<C> implements ReactiveUpdateClause<C> {

  protected static final Logger logger =
      Logger.getLogger(AbstractR2DBCUpdateClause.class.getName());

  protected final RelationalPath<?> entity;

  protected Map<Path<?>, Expression<?>> updates = new LinkedHashMap<>();

  protected QueryMetadata metadata = new DefaultQueryMetadata();

  protected transient String queryString;

  protected transient List<Object> constants;

  public AbstractR2DBCUpdateClause(
      Connection connection, Configuration configuration, RelationalPath<?> entity) {
    super(configuration, connection);
    this.entity = entity;
    metadata.addJoin(JoinType.DEFAULT, entity);
  }

  public AbstractR2DBCUpdateClause(
      R2DBCConnectionProvider connection, Configuration configuration, RelationalPath<?> entity) {
    super(configuration, connection);
    this.entity = entity;
    metadata.addJoin(JoinType.DEFAULT, entity);
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
    updates = new LinkedHashMap<>();
    metadata = new DefaultQueryMetadata();
    metadata.addJoin(JoinType.DEFAULT, entity);
  }

  protected Statement createStatement(Connection connection) {
    var serializer = createSerializerAndSerialize(null);
    return prepareStatementAndSetParameters(connection, serializer);
  }

  @Override
  public Mono<Long> execute() {
    return getConnection().map(this::createStatement).flatMap(this::executeStatement);
  }

  private Mono<Long> executeStatement(Statement stmt) {
    return Mono.from(stmt.execute())
        .flatMap(result -> Mono.from(result.getRowsUpdated()))
        .map(Long::valueOf);
  }

  @Override
  public List<SQLBindings> getSQL() {
    var serializer = createSerializer(true);
    serializer.serializeUpdate(metadata, entity, updates);
    return Collections.singletonList(createBindings(metadata, serializer));
  }

  @Override
  public <T> C set(Path<T> path, T value) {
    if (value instanceof Expression<?>) {
      updates.put(path, (Expression<?>) value);
    } else if (value != null) {
      updates.put(path, ConstantImpl.create(value));
    } else {
      setNull(path);
    }
    return (C) this;
  }

  @Override
  public <T> C set(Path<T> path, Expression<? extends T> expression) {
    if (expression != null) {
      updates.put(path, expression);
    } else {
      setNull(path);
    }
    return (C) this;
  }

  @Override
  public <T> C setNull(Path<T> path) {
    updates.put(path, Null.CONSTANT);
    return (C) this;
  }

  @Override
  public C set(List<? extends Path<?>> paths, List<?> values) {
    for (var i = 0; i < paths.size(); i++) {
      if (values.get(i) instanceof Expression) {
        updates.put(paths.get(i), (Expression<?>) values.get(i));
      } else if (values.get(i) != null) {
        updates.put(paths.get(i), ConstantImpl.create(values.get(i)));
      } else {
        updates.put(paths.get(i), Null.CONSTANT);
      }
    }
    return (C) this;
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
    serializer.serializeUpdate(metadata, entity, updates);
    return serializer.toString();
  }

  /**
   * Populate the UPDATE clause with the properties of the given bean. The properties need to match
   * the fields of the clause's entity instance. Primary key columns are skipped in the population.
   *
   * @param bean bean to use for population
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public C populate(Object bean) {
    return populate(bean, DefaultMapper.DEFAULT);
  }

  /**
   * Populate the UPDATE clause with the properties of the given bean using the given Mapper.
   *
   * @param obj object to use for population
   * @param mapper mapper to use
   * @return the current object
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public <T> C populate(T obj, Mapper<T> mapper) {
    var primaryKeyColumns =
        entity.getPrimaryKey() != null
            ? entity.getPrimaryKey().getLocalColumns()
            : Collections.emptyList();
    var values = mapper.createMap(entity, obj);
    for (Map.Entry<Path<?>, Object> entry : values.entrySet()) {
      if (!primaryKeyColumns.contains(entry.getKey())) {
        set((Path) entry.getKey(), entry.getValue());
      }
    }
    return (C) this;
  }

  @Override
  public boolean isEmpty() {
    return updates.isEmpty();
  }

  private SQLSerializer createSerializerAndSerialize(R2DBCUpdateBatch batch) {
    var serializer = createSerializer(true);
    serializer.serializeUpdate(metadata, entity, updates);
    return serializer;
  }

  private Statement prepareStatementAndSetParameters(
      Connection connection, SQLSerializer serializer) {
    var originalSql = serializer.toString();
    var constants = serializer.getConstants();
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
}
