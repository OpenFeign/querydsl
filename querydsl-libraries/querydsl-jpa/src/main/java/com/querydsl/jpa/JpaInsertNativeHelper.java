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
import com.querydsl.sql.SQLTemplates;
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
   * Resolve the SQL table name for an entity class (unquoted).
   *
   * @param entityClass the JPA entity class
   * @return the raw SQL table name (schema.table if schema is set)
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
   * Resolve the SQL column name for a path (unquoted). Reads {@code @Column} annotation if present,
   * otherwise falls back to the path metadata name.
   *
   * @param path the query path
   * @return the raw SQL column name
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
   * Quote a table name using the given {@link SQLTemplates}. Handles schema-qualified names by
   * quoting schema and table parts separately.
   *
   * @param templates the SQL templates providing dialect-specific quoting rules
   * @param tableName the raw table name (may be schema-qualified as "schema.table")
   * @return the properly quoted table name
   */
  private static String quoteTableName(SQLTemplates templates, String tableName) {
    var dotIndex = tableName.indexOf('.');
    if (dotIndex > 0) {
      var schema = tableName.substring(0, dotIndex);
      var table = tableName.substring(dotIndex + 1);
      return templates.quoteIdentifier(schema) + "." + templates.quoteIdentifier(table, true);
    }
    return templates.quoteIdentifier(tableName);
  }

  /**
   * Build a native SQL INSERT statement from entity metadata and column paths, with identifiers
   * properly quoted using the given {@link SQLTemplates}.
   *
   * @param templates the SQL templates providing dialect-specific quoting rules
   * @param entityClass the entity class (for table name resolution)
   * @param columns the columns to insert
   * @return the native SQL INSERT string with positional parameters
   */
  public static String buildNativeInsertSQL(
      SQLTemplates templates, Class<?> entityClass, Collection<Path<?>> columns) {
    var sb = new StringBuilder();
    sb.append("INSERT INTO ")
        .append(quoteTableName(templates, resolveTableName(entityClass)))
        .append(" (");

    var first = true;
    for (Path<?> col : columns) {
      if (!first) {
        sb.append(", ");
      }
      sb.append(templates.quoteIdentifier(resolveColumnName(col)));
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
   * Build a native SQL INSERT statement using {@link SQLTemplates#DEFAULT} for identifier quoting.
   *
   * @param entityClass the entity class (for table name resolution)
   * @param columns the columns to insert
   * @return the native SQL INSERT string with positional parameters
   * @deprecated prefer {@link #buildNativeInsertSQL(SQLTemplates, Class, Collection)} with explicit
   *     templates so dialect-specific quoting is applied
   */
  @Deprecated
  public static String buildNativeInsertSQL(Class<?> entityClass, Collection<Path<?>> columns) {
    return buildNativeInsertSQL(SQLTemplates.DEFAULT, entityClass, columns);
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
