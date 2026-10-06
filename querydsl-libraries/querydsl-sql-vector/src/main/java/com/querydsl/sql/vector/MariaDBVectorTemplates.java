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
package com.querydsl.sql.vector;

import com.querydsl.sql.MariaDBTemplates;
import com.querydsl.sql.SQLTemplates;

/** {@code MariaDBVectorTemplates} is a vector enabled SQL dialect for MariaDB 11.7 and later */
public class MariaDBVectorTemplates extends MariaDBTemplates {

  @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Intentional
  public static final MariaDBVectorTemplates DEFAULT = new MariaDBVectorTemplates();

  public static Builder builder() {
    return new Builder() {
      @Override
      protected SQLTemplates build(char escape, boolean quote) {
        return new MariaDBVectorTemplates(escape, quote);
      }
    };
  }

  public MariaDBVectorTemplates() {
    this('\\', false);
  }

  public MariaDBVectorTemplates(boolean quote) {
    this('\\', quote);
  }

  public MariaDBVectorTemplates(char escape, boolean quote) {
    super(escape, quote);
    addCustomType(MariaDBVectorType.DEFAULT);
    add(VectorOperatorSqlPatterns.createMariaDBOperatorTemplates());
  }
}
