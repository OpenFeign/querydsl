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

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * {@code RelationalPathBase} is a base class for {@link RelationalPath} implementations
 *
 * @author tiwe
 * @param <T> entity type
 */
public class RelationalPathBase<T> extends BeanPath<T> implements RelationalPath<T> {

  private static final long serialVersionUID = -7031357250283629202L;

  @Nullable private PrimaryKey<T> primaryKey;

  private final Map<Path<?>, ColumnMetadata> columnMetadata = new LinkedHashMap<>();

  private final List<ForeignKey<?>> foreignKeys = new ArrayList<>();

  private final List<ForeignKey<?>> inverseForeignKeys = new ArrayList<>();

  private final String schema, table;

  private final SchemaAndTable schemaAndTable;

  private transient FactoryExpression<T> projection;

  private transient NumberExpression<Long> count, countDistinct;

  public RelationalPathBase(Class<? extends T> type, String variable, String schema, String table) {
    this(type, PathMetadataFactory.forVariable(variable), schema, table);
  }

  public RelationalPathBase(
      Class<? extends T> type, PathMetadata metadata, String schema, String table) {
    super(type, metadata);
    this.schema = schema;
    this.table = table;
    this.schemaAndTable = new SchemaAndTable(schema, table);
  }

  protected PrimaryKey<T> createPrimaryKey(Path<?>... columns) {
    primaryKey = new PrimaryKey<T>(this, columns);
    return primaryKey;
  }

  protected <F> ForeignKey<F> createForeignKey(Path<?> local, String foreign) {
    ForeignKey<F> foreignKey = new ForeignKey<F>(this, local, foreign);
    foreignKeys.add(foreignKey);
    return foreignKey;
  }

  protected <F> ForeignKey<F> createForeignKey(
      List<? extends Path<?>> local, List<String> foreign) {
    ForeignKey<F> foreignKey =
        new ForeignKey<F>(this, new ArrayList<>(local), new ArrayList<>(foreign));
    foreignKeys.add(foreignKey);
    return foreignKey;
  }

  protected <F> ForeignKey<F> createInvForeignKey(Path<?> local, String foreign) {
    ForeignKey<F> foreignKey = new ForeignKey<F>(this, local, foreign);
    inverseForeignKeys.add(foreignKey);
    return foreignKey;
  }

  protected <F> ForeignKey<F> createInvForeignKey(
      List<? extends Path<?>> local, List<String> foreign) {
    ForeignKey<F> foreignKey =
        new ForeignKey<F>(this, new ArrayList<>(local), new ArrayList<>(foreign));
    inverseForeignKeys.add(foreignKey);
    return foreignKey;
  }

  protected <P extends Path<?>> P addMetadata(P path, ColumnMetadata metadata) {
    columnMetadata.put(path, metadata);
    return path;
  }

  @Override
  public NumberExpression<Long> count() {
    if (count == null) {
      if (primaryKey != null) {
        count =
            Expressions.numberOperation(
                Long.class, Ops.AggOps.COUNT_AGG, primaryKey.getLocalColumns().get(0));
      } else {
        throw new IllegalStateException("No count expression can be created");
      }
    }
    return count;
  }

  @Override
  public NumberExpression<Long> countDistinct() {
    if (countDistinct == null) {
      if (primaryKey != null) {
        // TODO handle multiple column primary keys properly
        countDistinct =
            Expressions.numberOperation(
                Long.class, Ops.AggOps.COUNT_DISTINCT_AGG, primaryKey.getLocalColumns().get(0));
      } else {
        throw new IllegalStateException("No count distinct expression can be created");
      }
    }
    return countDistinct;
  }

  /**
   * Compares the two relational paths using primary key columns
   *
   * @param right rhs of the comparison
   * @return this == right
   */
  @Override
  public BooleanExpression eq(T right) {
    if (right instanceof RelationalPath) {
      return primaryKeyOperation(Ops.EQ, primaryKey, ((RelationalPath) right).getPrimaryKey());
    } else {
      return super.eq(right);
    }
  }

  /**
   * Compares the two relational paths using primary key columns
   *
   * @param right rhs of the comparison
   * @return this == right
   */
  @Override
  public BooleanExpression eq(Expression<? super T> right) {
    if (right instanceof RelationalPath) {
      return primaryKeyOperation(Ops.EQ, primaryKey, ((RelationalPath) right).getPrimaryKey());
    } else {
      return super.eq(right);
    }
  }

  /**
   * Compares the two relational paths using primary key columns
   *
   * @param right rhs of the comparison
   * @return this != right
   */
  @Override
  public BooleanExpression ne(T right) {
    if (right instanceof RelationalPath) {
      return primaryKeyOperation(Ops.NE, primaryKey, ((RelationalPath) right).getPrimaryKey());
    } else {
      return super.ne(right);
    }
  }

  /**
   * Compares the two relational paths using primary key columns
   *
   * @param right rhs of the comparison
   * @return this != right
   */
  @Override
  public BooleanExpression ne(Expression<? super T> right) {
    if (right instanceof RelationalPath) {
      return primaryKeyOperation(Ops.NE, primaryKey, ((RelationalPath) right).getPrimaryKey());
    } else {
      return super.ne(right);
    }
  }

  private BooleanExpression primaryKeyOperation(Operator op, PrimaryKey<?> pk1, PrimaryKey<?> pk2) {
    if (pk1 == null || pk2 == null) {
      throw new UnsupportedOperationException("No primary keys available");
    }
    if (pk1.getLocalColumns().size() != pk2.getLocalColumns().size()) {
      throw new UnsupportedOperationException("Size mismatch for primary key columns");
    }
    BooleanExpression rv = null;
    for (int i = 0; i < pk1.getLocalColumns().size(); i++) {
      BooleanExpression pred =
          Expressions.booleanOperation(
              op, pk1.getLocalColumns().get(i), pk2.getLocalColumns().get(i));
      rv = rv != null ? rv.and(pred) : pred;
    }
    return rv;
  }

  @Override
  public FactoryExpression<T> getProjection() {
    if (projection == null) {
      projection = RelationalPathUtils.createProjection(this);
    }
    return projection;
  }

  public Path<?>[] all() {
    return columnMetadata.keySet().toArray(new Path<?>[0]);
  }

  @Override
  protected <P extends Path<?>> P add(P path) {
    return path;
  }

  @Override
  public List<Path<?>> getColumns() {
    return new ArrayList<>(this.columnMetadata.keySet());
  }

  @Override
  public Collection<ForeignKey<?>> getForeignKeys() {
    return foreignKeys;
  }

  @Override
  public Collection<ForeignKey<?>> getInverseForeignKeys() {
    return inverseForeignKeys;
  }

  @Override
  public PrimaryKey<T> getPrimaryKey() {
    return primaryKey;
  }

  @Override
  public SchemaAndTable getSchemaAndTable() {
    return schemaAndTable;
  }

  @Override
  public String getSchemaName() {
    return schema;
  }

  @Override
  public String getTableName() {
    return table;
  }

  @Override
  public ColumnMetadata getMetadata(Path<?> column) {
    return columnMetadata.get(column);
  }
}
