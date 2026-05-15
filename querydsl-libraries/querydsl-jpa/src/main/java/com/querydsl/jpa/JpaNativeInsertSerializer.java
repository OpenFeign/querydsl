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
import com.querydsl.core.types.Path;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.List;

/**
 * Serializer that emits a native SQL {@code INSERT} statement from JPA entity metadata
 * ({@code @Table}/{@code @Column} annotations) and a list of column/value expressions.
 *
 * <p>Unlike {@link NativeSQLSerializer}, which targets Hibernate native queries with positional
 * {@code ?N} placeholders, this serializer emits plain {@code ?} placeholders for direct binding to
 * a JDBC {@link java.sql.PreparedStatement}, and dispatches each value expression through the
 * visitor pattern so function templates, paths, parameters and other non-trivial expressions
 * serialize into SQL correctly.
 *
 * <p>This is an internal API used by {@link com.querydsl.jpa.impl.JPAInsertClause} and {@link
 * com.querydsl.jpa.hibernate.HibernateInsertClause} to support {@code executeWithKey()}.
 */
public final class JpaNativeInsertSerializer extends SQLSerializer {

  public JpaNativeInsertSerializer(Configuration configuration) {
    super(configuration);
  }

  @Override
  protected void appendAsColumnName(Path<?> path, boolean precededByDot) {
    if (path.getAnnotatedElement() != null
        && path.getAnnotatedElement().isAnnotationPresent(Column.class)) {
      var column = path.getAnnotatedElement().getAnnotation(Column.class);
      if (!column.name().isEmpty()) {
        append(getTemplates().quoteIdentifier(column.name(), precededByDot));
        return;
      }
    }
    super.appendAsColumnName(path, precededByDot);
  }

  /**
   * Serialize a single-row {@code INSERT} statement for the given entity, columns, and value
   * expressions. Each value expression is dispatched through the visitor pattern, so function
   * templates, paths, parameters, and other expressions are serialized into SQL with positional
   * {@code ?} placeholders. Bound values are accumulated and accessible via {@link
   * #getConstants()}.
   *
   * @param entityClass the JPA entity class (used to resolve the table name via {@link Table})
   * @param columns the column paths to insert into
   * @param values the value expressions, one per column, in matching order
   */
  public void serializeInsert(
      Class<?> entityClass, List<Path<?>> columns, List<Expression<?>> values) {
    serializeInsertRows(entityClass, columns, List.of(values));
  }

  /**
   * Serialize a multi-row {@code INSERT} statement for the given entity, columns, and rows of value
   * expressions. Emits {@code INSERT INTO t (c1, c2) VALUES (?, ?), (?, ?), ...}.
   *
   * @param entityClass the JPA entity class (used to resolve the table name via {@link Table})
   * @param columns the column paths to insert into
   * @param rows the rows of value expressions; each row's size must match the column count
   */
  public void serializeInsertRows(
      Class<?> entityClass, List<Path<?>> columns, List<List<Expression<?>>> rows) {
    if (rows.isEmpty()) {
      throw new IllegalArgumentException("No rows specified for insert");
    }
    for (var row : rows) {
      if (columns.size() != row.size()) {
        throw new IllegalArgumentException(
            "Column count ("
                + columns.size()
                + ") does not match value count ("
                + row.size()
                + ")");
      }
    }

    var templates = getTemplates();
    append(templates.getInsertInto());
    appendTable(entityClass);

    if (!columns.isEmpty()) {
      append(" (");
      var first = true;
      for (Path<?> col : columns) {
        if (!first) {
          append(", ");
        }
        appendAsColumnName(col, false);
        first = false;
      }
      append(")");
    }

    var oldSkipParent = skipParent;
    skipParent = true;
    try {
      append(templates.getValues());
      var firstRow = true;
      for (var row : rows) {
        if (!firstRow) {
          append(", ");
        }
        append("(");
        var firstValue = true;
        for (Expression<?> value : row) {
          if (!firstValue) {
            append(", ");
          }
          handle(value);
          firstValue = false;
        }
        append(")");
        firstRow = false;
      }
    } finally {
      skipParent = oldSkipParent;
    }
  }

  private void appendTable(Class<?> entityClass) {
    SQLTemplates templates = getTemplates();
    String schema = "";
    String tableName = entityClass.getSimpleName();
    if (entityClass.isAnnotationPresent(Table.class)) {
      var table = entityClass.getAnnotation(Table.class);
      if (!table.name().isEmpty()) {
        tableName = table.name();
      }
      schema = table.schema();
    }
    if (!schema.isEmpty()) {
      append(templates.quoteIdentifier(schema));
      append(".");
      append(templates.quoteIdentifier(tableName, true));
    } else {
      append(templates.quoteIdentifier(tableName));
    }
  }
}
