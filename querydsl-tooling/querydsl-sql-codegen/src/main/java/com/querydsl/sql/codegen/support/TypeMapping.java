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
package com.querydsl.sql.codegen.support;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.types.Type;

/**
 * {@code TypeMapping} customizes the mapping from table + column to a type.
 *
 * @author tiwe
 */
public class TypeMapping implements Mapping {

  private String table, column, type;

  public TypeMapping() {
    super();
  }

  public TypeMapping(String table, String column, String type) {
    this();
    this.table = table;
    this.column = column;
    this.type = type;
  }

  @Override
  public void apply(Configuration configuration) {
    try {
      Class<?> typeClass = Class.forName(type);
      if (Type.class.isAssignableFrom(typeClass)) {
        configuration.register(table, column, (Type<?>) typeClass.newInstance());
      } else {
        configuration.register(table, column, typeClass);
      }
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      throw new RuntimeException(e);
    }
  }

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
