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
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Helpers shared by {@link com.querydsl.jpa.impl.JPAInsertClause} and {@link
 * com.querydsl.jpa.hibernate.HibernateInsertClause} to support {@code executeWithKey()} via native
 * SQL INSERT.
 *
 * <p>This is an internal API and not intended for direct use by application code.
 */
public final class JpaInsertNativeHelper {

  private JpaInsertNativeHelper() {}

  /**
   * Resolve the effective column paths from either the {@code set()}-style inserts map or the
   * {@code columns()}-style list. The {@code set()}-style takes precedence when present.
   */
  public static List<Path<?>> effectiveColumns(
      Map<Path<?>, Expression<?>> inserts, List<Path<?>> columns) {
    if (!inserts.isEmpty()) {
      return new ArrayList<>(inserts.keySet());
    }
    return new ArrayList<>(columns);
  }

  /**
   * Resolve the effective value expressions, in the order matching {@link #effectiveColumns(Map,
   * List)}. Raw values from the {@code values()}-style call are wrapped as constants; expressions
   * are passed through unchanged.
   */
  public static List<Expression<?>> effectiveValues(
      Map<Path<?>, Expression<?>> inserts, List<Object> values) {
    if (!inserts.isEmpty()) {
      return new ArrayList<>(inserts.values());
    }
    var result = new ArrayList<Expression<?>>(values.size());
    for (Object v : values) {
      if (v instanceof Expression<?> expression) {
        result.add(expression);
      } else {
        result.add(Expressions.constant(v));
      }
    }
    return result;
  }

  /**
   * Resolve constant values from the serializer, unwrapping {@link Param} expressions against the
   * provided parameter bindings.
   *
   * @param constants the constants accumulated by the serializer
   * @param params the parameter bindings collected from {@link
   *     com.querydsl.core.QueryMetadata#getParams()}
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
   * Execute a native SQL INSERT with {@code RETURN_GENERATED_KEYS} and return the generated key.
   *
   * @param <T> the key type
   * @param conn the JDBC connection (not closed by this method)
   * @param sql the native SQL INSERT string
   * @param params the parameter values to bind, in positional order
   * @param keyType the expected key type
   * @return the generated key, or {@code null} if no rows were inserted
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
}
