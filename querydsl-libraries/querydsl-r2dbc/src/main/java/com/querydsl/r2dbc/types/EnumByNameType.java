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
package com.querydsl.r2dbc.types;

import io.r2dbc.spi.Row;
import java.sql.Types;

/**
 * {@code EnumByNameType} maps Enum types to their String names on the JDBC level
 *
 * @param <T>
 * @author mc_fish
 */
public class EnumByNameType<T extends Enum<T>> extends AbstractType<T, String> {

  private final Class<T> type;

  public EnumByNameType(Class<T> type) {
    this(Types.VARCHAR, type);
  }

  public EnumByNameType(int jdbcType, Class<T> type) {
    super(jdbcType);
    this.type = type;
  }

  @Override
  public Class<T> getReturnedClass() {
    return type;
  }

  @Override
  public T getValue(Row row, int startIndex) {
    String val = row.get(startIndex, String.class);
    return val != null ? Enum.valueOf(type, val) : null;
  }

  @Override
  protected String toDbValue(T value) {
    return value.name();
  }

  @Override
  public Class<String> getDatabaseClass() {
    return String.class;
  }
}
