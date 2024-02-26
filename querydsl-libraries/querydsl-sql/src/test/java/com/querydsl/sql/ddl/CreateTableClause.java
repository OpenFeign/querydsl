/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.sql.ddl;

import com.querydsl.core.QueryException;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * CreateTableClause defines a CREATE TABLE clause
 *
 * @author tiwe
 */
public class CreateTableClause {

  private static final Logger logger = Logger.getLogger(CreateTableClause.class.getName());

  private final Connection connection;

  private final Configuration configuration;

  private final SQLTemplates templates;

  private final String table;

  private final List<ColumnData> columns = new ArrayList<ColumnData>();

  private final List<IndexData> indexes = new ArrayList<IndexData>();

  private PrimaryKeyData primaryKey;

  private final List<ForeignKeyData> foreignKeys = new ArrayList<ForeignKeyData>();

  public CreateTableClause(Connection conn, Configuration c, String table) {
    this.connection = conn;
    this.configuration = c;
    this.templates = c.getTemplates();
    this.table = templates.quoteIdentifier(table);
  }

  /**
   * Add a new column definition
   *
   * @param name
   * @param type
   * @return
   */
  public CreateTableClause column(String name, Class<?> type) {
    String typeName = configuration.getTypeName(type);
    columns.add(new ColumnData(templates.quoteIdentifier(name), typeName));
    return this;
  }

  private ColumnData lastColumn() {
    return columns.getLast();
  }

  /**
   * Set the last added column to not null
   *
   * @return
   */
  public CreateTableClause notNull() {
    lastColumn().setNullAllowed(false);
    return this;
  }

  /**
   * Set the size of the last column's type
   *
   * @param size
   * @return
   */
  public CreateTableClause size(int size) {
    lastColumn().setSize(size);
    return this;
  }

  /**
   * Set the last column to auto increment
   *
   * @return
   */
  public CreateTableClause autoIncrement() {
    lastColumn().setAutoIncrement(true);
    return this;
  }

  /**
   * Set the primary key
   *
   * @param name
   * @param columns
   * @return
   */
  public CreateTableClause primaryKey(String name, String... columns) {
    for (int i = 0; i < columns.length; i++) {
      columns[i] = templates.quoteIdentifier(columns[i]);
    }
    primaryKey = new PrimaryKeyData(templates.quoteIdentifier(name), columns);
    return this;
  }

  /**
   * Add an index
   *
   * @param name
   * @param columns
   * @return
   */
  public CreateTableClause index(String name, String... columns) {
    indexes.add(new IndexData(name, columns));
    return this;
  }

  /**
   * Set the last added index to unique
   *
   * @return
   */
  public CreateTableClause unique() {
    indexes.getLast().setUnique(true);
    return this;
  }

  /**
   * Add a foreign key
   *
   * @param name
   * @param columns
   * @return
   */
  public ForeignKeyBuilder foreignKey(String name, String... columns) {
    return new ForeignKeyBuilder(
        this, templates, foreignKeys, templates.quoteIdentifier(name), columns);
  }

  /** Execute the clause */
  @SuppressWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
  public void execute() {
    StringBuilder builder = new StringBuilder();
    builder.append(templates.getCreateTable()).append(table).append(" (\n");
    List<String> lines = new ArrayList<String>(columns.size() + foreignKeys.size() + 1);
    // columns
    for (ColumnData column : columns) {
      StringBuilder line = new StringBuilder();
      line.append(column.getName()).append(" ").append(column.getType().toUpperCase());
      if (column.getSize() != null) {
        line.append("(").append(column.getSize()).append(")");
      }
      if (!column.isNullAllowed()) {
        line.append(templates.getNotNull().toUpperCase());
      }
      if (column.isAutoIncrement()) {
        line.append(templates.getAutoIncrement().toUpperCase());
      }
      lines.add(line.toString());
    }

    // primary key
    if (primaryKey != null) {
      StringBuilder line = new StringBuilder();
      line.append("CONSTRAINT ").append(primaryKey.getName()).append(" ");
      line.append("PRIMARY KEY(").append(String.join(", ", primaryKey.getColumns())).append(")");
      lines.add(line.toString());
    }

    // foreign keys
    for (ForeignKeyData foreignKey : foreignKeys) {
      StringBuilder line = new StringBuilder();
      line.append("CONSTRAINT ").append(foreignKey.getName()).append(" ");
      line.append("FOREIGN KEY(")
          .append(String.join(", ", foreignKey.getForeignColumns()))
          .append(") ");
      line.append("REFERENCES ")
          .append(foreignKey.getTable())
          .append("(")
          .append(String.join(", ", foreignKey.getParentColumns()))
          .append(")");
      lines.add(line.toString());
    }
    builder.append("  ").append(String.join(",\n  ", lines));
    builder.append("\n)\n");
    logger.info(builder.toString());

    try (Statement stmt = connection.createStatement()) {
      stmt.execute(builder.toString());

      // indexes
      for (IndexData index : indexes) {
        String indexColumns = String.join(", ", index.getColumns());
        String prefix = templates.getCreateIndex();
        if (index.isUnique()) {
          prefix = templates.getCreateUniqueIndex();
        }
        String sql =
            prefix + index.getName() + templates.getOn() + table + "(" + indexColumns + ")";
        logger.info(sql);
        stmt.execute(sql);
      }
    } catch (SQLException e) {
      System.err.println(builder.toString());
      throw new QueryException(e.getMessage(), e);
    }
  }
}
