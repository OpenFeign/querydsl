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
import org.jetbrains.annotations.Nullable;

/**
 * {@code TrueFalseType} maps Boolean to 'T'/'F' on the JDBC level
 *
 * @author mc_fish
 */
public class TrueFalseType extends AbstractType<Boolean, String> {

  public TrueFalseType() {
    super(Types.VARCHAR);
  }

  public TrueFalseType(int type) {
    super(type);
  }

  @Override
  public Class<Boolean> getReturnedClass() {
    return Boolean.class;
  }

  @Override
  @Nullable
  public Boolean getValue(Row row, int startIndex) {
    String val = row.get(startIndex, String.class);
    return val != null ? val.equalsIgnoreCase("T") : null;
  }

  @Override
  protected String toDbValue(Boolean value) {
    return value ? "T" : "F";
  }

  @Override
  public Class<String> getDatabaseClass() {
    return String.class;
  }
}
