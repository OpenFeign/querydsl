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

import com.querydsl.sql.DB2Templates;
import com.querydsl.sql.SQLTemplates;

/** {@code DB2VectorTemplates} is a vector enabled SQL dialect for Db2 12.1 */
public class DB2VectorTemplates extends DB2Templates {

  @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Intentional
  public static final DB2VectorTemplates DEFAULT = new DB2VectorTemplates();

  public static Builder builder() {
    return new Builder() {
      @Override
      protected SQLTemplates build(char escape, boolean quote) {
        return new DB2VectorTemplates(escape, quote);
      }
    };
  }

  public DB2VectorTemplates() {
    this('\\', false);
  }

  public DB2VectorTemplates(boolean quote) {
    this('\\', quote);
  }

  public DB2VectorTemplates(char escape, boolean quote) {
    super(escape, quote);
    addCustomType(DB2VectorType.DEFAULT);
    add(VectorOperatorSqlPatterns.createDB2OperatorTemplates());
  }
}
