/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.r2dbc.ddl;

import com.google.common.base.Joiner;
import com.querydsl.r2dbc.Configuration;
import com.querydsl.r2dbc.SQLTemplates;
import io.r2dbc.spi.Connection;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * CreateTableClause defines a CREATE TABLE clause
 *
 * @author mc_fish
 */
public class CreateTableClause {

  // private static final Logger logger = LoggerFactory.getLogger(CreateTableClause.class);

  private static final Joiner COMMA_JOINER = Joiner.on(", ");

  private final Connection connection;

  private final Configuration configuration;

  private final SQLTemplates templates;

  private final String table;

  private final List<ColumnData> columns = new ArrayList<>();

  private final List<IndexData> indexes = new ArrayList<>();

  private PrimaryKeyData primaryKey;

  private final List<ForeignKeyData> foreignKeys = new ArrayList<>();

  public CreateTableClause(Connection connection, Configuration c, String table) {
    this.connection = connection;
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
    var typeName = configuration.getTypeName(type);
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
    for (var i = 0; i < columns.length; i++) {
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
  public Mono<Void> execute() {
    var builder = new StringBuilder();
    builder.append(templates.getCreateTable()).append(table).append(" (\n");
    List<String> lines = new ArrayList<>(columns.size() + foreignKeys.size() + 1);
    // columns
    for (ColumnData column : columns) {
      var line = new StringBuilder();
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
      var line = new StringBuilder();
      line.append("CONSTRAINT ").append(primaryKey.getName()).append(" ");
      line.append("PRIMARY KEY(").append(COMMA_JOINER.join(primaryKey.getColumns())).append(")");
      lines.add(line.toString());
    }

    // foreign keys
    for (ForeignKeyData foreignKey : foreignKeys) {
      var line = new StringBuilder();
      line.append("CONSTRAINT ").append(foreignKey.getName()).append(" ");
      line.append("FOREIGN KEY(")
          .append(COMMA_JOINER.join(foreignKey.getForeignColumns()))
          .append(") ");
      line.append("REFERENCES ")
          .append(foreignKey.getTable())
          .append("(")
          .append(COMMA_JOINER.join(foreignKey.getParentColumns()))
          .append(")");
      lines.add(line.toString());
    }
    builder.append("  ").append(Joiner.on(",\n  ").join(lines));
    builder.append("\n)\n");
    // logger.info(builder.toString());

    var statement = connection.createStatement(builder.toString());
    return Mono.from(statement.execute())
        .flatMapIterable(__ -> indexes)
        .flatMap(
            index -> {
              var indexColumns = COMMA_JOINER.join(index.getColumns());
              var prefix = templates.getCreateIndex();
              if (index.isUnique()) {
                prefix = templates.getCreateUniqueIndex();
              }
              var sql =
                  prefix + index.getName() + templates.getOn() + table + "(" + indexColumns + ")";
              // logger.info(sql);

              var stmt = connection.createStatement(sql);
              return Mono.from(stmt.execute());
            })
        .collectList()
        .then();
  }
}
