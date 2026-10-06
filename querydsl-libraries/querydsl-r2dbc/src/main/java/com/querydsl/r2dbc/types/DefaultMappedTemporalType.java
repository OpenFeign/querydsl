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

import com.querydsl.r2dbc.binding.BindMarker;
import com.querydsl.r2dbc.binding.BindTarget;
import io.r2dbc.spi.Row;
import java.time.temporal.Temporal;

/**
 * {@code DefaultMappedTemporalType} reads the driver's default {@code java.time} value instead of
 * asking for a {@link Temporal}
 *
 * @param <T> returned type
 */
public class DefaultMappedTemporalType<T> implements Type<T, Temporal> {

  private final AbstractType<T, Temporal> type;

  public DefaultMappedTemporalType(AbstractType<T, Temporal> type) {
    this.type = type;
  }

  @Override
  public int[] getSQLTypes() {
    return type.getSQLTypes();
  }

  @Override
  public Class<T> getReturnedClass() {
    return type.getReturnedClass();
  }

  @Override
  public Class<Temporal> getDatabaseClass() {
    return type.getDatabaseClass();
  }

  @Override
  public String getLiteral(T value) {
    return type.getLiteral(value);
  }

  @Override
  public T getValue(Row row, int startIndex) {
    var value = (Temporal) row.get(startIndex);
    return value != null ? type.fromDbValue(value) : null;
  }

  @Override
  public void setValue(BindMarker bindMarker, BindTarget bindTarget, T value) {
    type.setValue(bindMarker, bindTarget, value);
  }
}
