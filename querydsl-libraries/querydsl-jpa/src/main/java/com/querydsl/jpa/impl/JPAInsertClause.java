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
package com.querydsl.jpa.impl;

import com.querydsl.core.JoinType;
import com.querydsl.core.QueryException;
import com.querydsl.core.dml.InsertClause;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAQueryMixin;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.JpaInsertNativeHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * UpdateClause implementation for JPA
 *
 * @author tiwe
 */
public class JPAInsertClause implements InsertClause<JPAInsertClause> {

  private final QueryMixin<?> queryMixin = new JPAQueryMixin<Void>();

  private final Map<Path<?>, Expression<?>> inserts = new LinkedHashMap<>();

  private final List<Path<?>> columns = new ArrayList<>();

  private final List<Object> values = new ArrayList<>();

  private final EntityManager entityManager;

  private final JPQLTemplates templates;

  private SubQueryExpression<?> subQuery;

  @Nullable private LockModeType lockMode;

  public JPAInsertClause(EntityManager em, EntityPath<?> entity) {
    this(em, entity, JPAProvider.getTemplates(em));
  }

  public JPAInsertClause(EntityManager em, EntityPath<?> entity, JPQLTemplates templates) {
    this.entityManager = em;
    this.templates = templates;
    queryMixin.addJoin(JoinType.DEFAULT, entity);
  }

  @Override
  public long execute() {
    var serializer = new JPQLSerializer(templates, entityManager);
    serializer.serializeForInsert(
        queryMixin.getMetadata(),
        inserts.isEmpty() ? columns : inserts.keySet(),
        values,
        subQuery,
        inserts);

    var query = entityManager.createQuery(serializer.toString());
    if (lockMode != null) {
      query.setLockMode(lockMode);
    }
    JPAUtil.setConstants(query, serializer.getConstants(), queryMixin.getMetadata().getParams());
    return query.executeUpdate();
  }

  /**
   * Execute the clause and return the generated key with the type of the given path. If no rows
   * were created, null is returned, otherwise the key of the first row is returned.
   *
   * <p>This method bypasses JPQL and executes a native SQL INSERT via JDBC to retrieve the
   * generated key. It requires that the JPA provider supports {@code
   * EntityManager.unwrap(Connection.class)}.
   *
   * <p>Note: {@code INSERT ... SELECT} subqueries are not supported by this method.
   *
   * @param <T> key type
   * @param path path for key (used to determine return type)
   * @return generated key, or null if no rows were created
   * @throws QueryException if a database error occurs or the operation is not supported
   */
  @SuppressWarnings("unchecked")
  @Nullable
  public <T> T executeWithKey(Path<T> path) {
    return executeWithKey((Class<T>) path.getType());
  }

  /**
   * Execute the clause and return the generated key cast to the given type. If no rows were
   * created, null is returned, otherwise the key of the first row is returned.
   *
   * @param <T> key type
   * @param type class of the key type
   * @return generated key, or null if no rows were created
   * @throws QueryException if a database error occurs or the operation is not supported
   */
  @Nullable
  public <T> T executeWithKey(Class<T> type) {
    if (subQuery != null) {
      throw new UnsupportedOperationException(
          "executeWithKey is not supported for INSERT ... SELECT subqueries");
    }

    var effectiveColumns = JpaInsertNativeHelper.effectiveColumns(inserts, columns);
    if (effectiveColumns.isEmpty()) {
      throw new IllegalStateException("No columns specified for insert");
    }

    var entityClass = queryMixin.getMetadata().getJoins().get(0).getTarget().getType();

    // Serialize to collect constant values
    var serializer = new JPQLSerializer(templates, entityManager);
    serializer.serializeForInsert(
        queryMixin.getMetadata(), effectiveColumns, values, subQuery, inserts);

    var params =
        JpaInsertNativeHelper.resolveConstants(
            serializer.getConstants(), queryMixin.getMetadata().getParams());

    var sql = JpaInsertNativeHelper.buildNativeInsertSQL(entityClass, effectiveColumns);

    try {
      return entityManager
          .unwrap(org.hibernate.Session.class)
          .doReturningWork(
              connection ->
                  JpaInsertNativeHelper.executeAndReturnKey(connection, sql, params, type));
    } catch (Exception e) {
      throw new QueryException("Failed to execute insert with generated key", e);
    }
  }

  public JPAInsertClause setLockMode(LockModeType lockMode) {
    this.lockMode = lockMode;
    return this;
  }

  @Override
  public String toString() {
    var serializer = new JPQLSerializer(templates, entityManager);
    serializer.serializeForInsert(
        queryMixin.getMetadata(),
        inserts.isEmpty() ? columns : inserts.keySet(),
        values,
        subQuery,
        inserts);
    return serializer.toString();
  }

  @Override
  public JPAInsertClause columns(Path<?>... columns) {
    this.columns.addAll(Arrays.asList(columns));
    return this;
  }

  @Override
  public JPAInsertClause select(SubQueryExpression<?> sq) {
    subQuery = sq;
    return this;
  }

  @Override
  public JPAInsertClause values(Object... v) {
    this.values.addAll(Arrays.asList(v));
    return this;
  }

  @Override
  public boolean isEmpty() {
    return columns.isEmpty();
  }

  @Override
  public <T> JPAInsertClause set(Path<T> path, T value) {
    if (value != null) {
      inserts.put(path, Expressions.constant(value));
    } else {
      setNull(path);
    }
    return this;
  }

  @Override
  public <T> JPAInsertClause set(Path<T> path, Expression<? extends T> expression) {
    if (expression != null) {
      inserts.put(path, expression);
    } else {
      setNull(path);
    }
    return this;
  }

  @Override
  public <T> JPAInsertClause setNull(Path<T> path) {
    inserts.put(path, Expressions.nullExpression(path));
    return this;
  }
}
