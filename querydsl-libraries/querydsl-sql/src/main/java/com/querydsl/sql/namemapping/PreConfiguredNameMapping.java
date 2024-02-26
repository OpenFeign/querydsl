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
package com.querydsl.sql.namemapping;

import com.querydsl.sql.SchemaAndTable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@link NameMapping} implementation that allows specifying exact schema, table and column name
 * mappings.
 */
public class PreConfiguredNameMapping implements NameMapping {

  private final Map<SchemaAndTable, SchemaAndTable> schemaTables = new HashMap<>();

  private final Map<String, String> tables = new HashMap<>();

  private final Map<SchemaAndTable, Map<String, String>> schemaTableColumns = new HashMap<>();

  private final Map<String, Map<String, String>> tableColumns = new HashMap<>();

  public Optional<SchemaAndTable> getOverride(SchemaAndTable key) {
    if (!schemaTables.isEmpty() && key.getSchema() != null) {
      if (schemaTables.containsKey(key)) {
        return Optional.ofNullable(schemaTables.get(key));
      }
    }

    if (tables.containsKey(key.getTable())) {
      String table = tables.get(key.getTable());
      return Optional.of(new SchemaAndTable(key.getSchema(), table));
    }
    return Optional.empty();
  }

  public Optional<String> getColumnOverride(SchemaAndTable key, String column) {
    Map<String, String> columnOverrides;
    String newColumn = null;
    columnOverrides = schemaTableColumns.get(key);
    if (columnOverrides != null && (newColumn = columnOverrides.get(column)) != null) {
      return Optional.of(newColumn);
    }
    columnOverrides = tableColumns.get(key.getTable());
    if (columnOverrides != null && (newColumn = columnOverrides.get(column)) != null) {
      return Optional.of(newColumn);
    }
    return Optional.empty();
  }

  public String registerTableOverride(String oldTable, String newTable) {
    return tables.put(oldTable, newTable);
  }

  public SchemaAndTable registerTableOverride(SchemaAndTable from, SchemaAndTable to) {
    return schemaTables.put(from, to);
  }

  public String registerColumnOverride(
      String schema, String table, String oldColumn, String newColumn) {
    SchemaAndTable key = new SchemaAndTable(schema, table);
    Map<String, String> columnOverrides =
        schemaTableColumns.computeIfAbsent(key, k -> new HashMap<String, String>());
    return columnOverrides.put(oldColumn, newColumn);
  }

  public String registerColumnOverride(String table, String oldColumn, String newColumn) {
    Map<String, String> columnOverrides =
        tableColumns.computeIfAbsent(table, k -> new HashMap<String, String>());
    return columnOverrides.put(oldColumn, newColumn);
  }
}
