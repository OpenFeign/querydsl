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
import java.util.Currency;
import org.jetbrains.annotations.Nullable;

/**
 * {@code CurrencyType} maps Currency to String on the JDBC level
 *
 * @author mc_fish
 */
public class CurrencyType extends AbstractType<Currency, String> {

  public CurrencyType() {
    super(Types.VARCHAR);
  }

  public CurrencyType(int type) {
    super(type);
  }

  @Override
  public Class<Currency> getReturnedClass() {
    return Currency.class;
  }

  @Override
  @Nullable
  public Currency getValue(Row row, int startIndex) {
    String val = row.get(startIndex, String.class);
    return val != null ? Currency.getInstance(val) : null;
  }

  @Override
  protected String toDbValue(Currency value) {
    return value.getCurrencyCode();
  }

  @Override
  public Class<String> getDatabaseClass() {
    return String.class;
  }
}
