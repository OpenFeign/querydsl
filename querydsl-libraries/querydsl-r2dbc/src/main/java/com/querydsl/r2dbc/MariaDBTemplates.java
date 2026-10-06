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
package com.querydsl.r2dbc;

import com.querydsl.r2dbc.types.DateType;
import com.querydsl.r2dbc.types.DefaultMappedTemporalType;
import com.querydsl.r2dbc.types.JSR310InstantType;
import com.querydsl.r2dbc.types.JSR310LocalDateTimeType;
import com.querydsl.r2dbc.types.JSR310LocalDateType;
import com.querydsl.r2dbc.types.JSR310LocalTimeType;
import com.querydsl.r2dbc.types.JSR310OffsetDateTimeType;
import com.querydsl.r2dbc.types.JSR310OffsetTimeType;
import com.querydsl.r2dbc.types.JSR310ZonedDateTimeType;
import com.querydsl.r2dbc.types.TimeType;
import com.querydsl.r2dbc.types.UtilDateType;
import com.querydsl.sql.Keywords;

/**
 * {@code MariaDBTemplates} is an SQL dialect for MariaDB. The date and time types read the driver's
 * default {@code java.time} value, because r2dbc-mariadb decodes a {@code Temporal} request as an
 * {@code Instant} in the JVM time zone.
 */
public class MariaDBTemplates extends MySQLTemplates {

  @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Intentional
  public static final MariaDBTemplates DEFAULT = new MariaDBTemplates();

  public static Builder builder() {
    return new Builder() {
      @Override
      protected SQLTemplates build(char escape, boolean quote) {
        return new MariaDBTemplates(escape, quote);
      }
    };
  }

  public MariaDBTemplates() {
    this('\\', false);
  }

  public MariaDBTemplates(boolean quote) {
    this('\\', quote);
  }

  public MariaDBTemplates(char escape, boolean quote) {
    super(Keywords.MARIADB, escape, quote);
    addCustomType(new DefaultMappedTemporalType<>(new TimeType()));
    addCustomType(new DefaultMappedTemporalType<>(new DateType()));
    addCustomType(new DefaultMappedTemporalType<>(new UtilDateType()));
    addCustomType(new DefaultMappedTemporalType<>(new JSR310LocalTimeType()));
    addCustomType(new DefaultMappedTemporalType<>(new JSR310LocalDateType()));
    addCustomType(new DefaultMappedTemporalType<>(new JSR310LocalDateTimeType()));
    addCustomType(new DefaultMappedTemporalType<>(new JSR310OffsetDateTimeType()));
    addCustomType(new DefaultMappedTemporalType<>(new JSR310OffsetTimeType()));
    addCustomType(new DefaultMappedTemporalType<>(new JSR310ZonedDateTimeType()));
    addCustomType(new DefaultMappedTemporalType<>(new JSR310InstantType()));
  }
}
