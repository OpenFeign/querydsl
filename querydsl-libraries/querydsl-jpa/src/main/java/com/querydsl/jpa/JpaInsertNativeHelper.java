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
package com.querydsl.jpa;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Param;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Helper for building native SQL INSERT statements from JPA entity metadata. Used by {@link
 * com.querydsl.jpa.impl.JPAInsertClause} and {@link
 * com.querydsl.jpa.hibernate.HibernateInsertClause} to support {@code executeWithKey()}.
 *
 * <p>This is an internal API and not intended for direct use by application code.
 */
public final class JpaInsertNativeHelper {

  private JpaInsertNativeHelper() {}

  /**
   * Resolve the SQL table name for an entity class.
   *
   * @param entityClass the JPA entity class
   * @return the SQL table name
   */
  public static String resolveTableName(Class<?> entityClass) {
    if (entityClass.isAnnotationPresent(Table.class)) {
      var table = entityClass.getAnnotation(Table.class);
      if (!table.name().isEmpty()) {
        var sb = new StringBuilder();
        if (!table.schema().isEmpty()) {
          sb.append(table.schema()).append('.');
        }
        sb.append(table.name());
        return sb.toString();
      }
    }
    return entityClass.getSimpleName();
  }

  /**
   * Resolve the SQL column name for a path. Reads {@code @Column} annotation if present, otherwise
   * falls back to the path metadata name.
   *
   * @param path the query path
   * @return the SQL column name
   */
  public static String resolveColumnName(Path<?> path) {
    if (path.getAnnotatedElement() != null
        && path.getAnnotatedElement().isAnnotationPresent(Column.class)) {
      var column = path.getAnnotatedElement().getAnnotation(Column.class);
      if (!column.name().isEmpty()) {
        return column.name();
      }
    }
    return path.getMetadata().getName();
  }

  /**
   * Build a native SQL INSERT statement from entity metadata and column paths.
   *
   * @param entityClass the entity class (for table name resolution)
   * @param columns the columns to insert
   * @return the native SQL INSERT string with positional parameters
   */
  public static String buildNativeInsertSQL(Class<?> entityClass, Collection<Path<?>> columns) {
    var tableName = resolveTableName(entityClass);
    var sb = new StringBuilder();
    sb.append("INSERT INTO ").append(tableName).append(" (");

    var first = true;
    for (Path<?> col : columns) {
      if (!first) {
        sb.append(", ");
      }
      sb.append(resolveColumnName(col));
      first = false;
    }

    sb.append(") VALUES (");
    first = true;
    for (int i = 0; i < columns.size(); i++) {
      if (!first) {
        sb.append(", ");
      }
      sb.append('?');
      first = false;
    }
    sb.append(')');

    return sb.toString();
  }

  /**
   * Resolve constant values from the serializer, unwrapping {@link Param} expressions.
   *
   * @param constants the constants from the serializer
   * @param params the parameter bindings
   * @return resolved values ready for JDBC binding
   */
  public static Object[] resolveConstants(
      List<Object> constants, Map<ParamExpression<?>, Object> params) {
    var result = new Object[constants.size()];
    for (var i = 0; i < constants.size(); i++) {
      var val = constants.get(i);
      if (val instanceof Param<?> param) {
        val = params.get(val);
        if (val == null) {
          throw new ParamNotSetException(param);
        }
      }
      result[i] = val;
    }
    return result;
  }

  /**
   * Execute a native SQL INSERT with RETURN_GENERATED_KEYS and return the generated key.
   *
   * @param <T> the key type
   * @param conn the JDBC connection (not closed by this method)
   * @param sql the native SQL INSERT string
   * @param params the parameter values to bind
   * @param keyType the expected key type
   * @return the generated key, or null if no rows were inserted
   * @throws SQLException if a database error occurs
   */
  @Nullable
  public static <T> T executeAndReturnKey(
      java.sql.Connection conn, String sql, Object[] params, Class<T> keyType) throws SQLException {
    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      for (int i = 0; i < params.length; i++) {
        stmt.setObject(i + 1, params[i]);
      }
      stmt.executeUpdate();

      try (ResultSet rs = stmt.getGeneratedKeys()) {
        if (rs.next()) {
          return rs.getObject(1, keyType);
        }
        return null;
      }
    }
  }

  /**
   * Collect effective columns and values from either the set-style inserts map or the
   * columns/values lists.
   *
   * @param inserts the set-style inserts (path to expression mapping)
   * @param columns the columns list (from columns().values() style)
   * @param values the values list
   * @param serializer used to extract constant values from expressions
   * @return the effective column paths
   */
  public static Collection<Path<?>> effectiveColumns(
      Map<Path<?>, Expression<?>> inserts, List<Path<?>> columns) {
    if (!inserts.isEmpty()) {
      return inserts.keySet();
    }
    return columns;
  }
}
