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

import com.querydsl.sql.OracleTemplates;
import com.querydsl.sql.SQLTemplates;

/** {@code OracleVectorTemplates} is a vector enabled SQL dialect for Oracle 23ai */
public class OracleVectorTemplates extends OracleTemplates {

  @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Intentional
  public static final OracleVectorTemplates DEFAULT = new OracleVectorTemplates();

  public static Builder builder() {
    return new Builder() {
      @Override
      protected SQLTemplates build(char escape, boolean quote) {
        return new OracleVectorTemplates(escape, quote);
      }
    };
  }

  public OracleVectorTemplates() {
    this('\\', false);
  }

  public OracleVectorTemplates(boolean quote) {
    this('\\', quote);
  }

  public OracleVectorTemplates(char escape, boolean quote) {
    super(escape, quote);
    addCustomType(OracleVectorType.DEFAULT);
    add(VectorOperatorSqlPatterns.createOracleOperatorTemplates());
  }
}
