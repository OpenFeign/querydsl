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
package com.querydsl.sql;

import java.util.Set;

/**
 * {@code SQLServer2008Templates} is an SQL dialect for Microsoft SQL Server 2008
 *
 * @author tiwe
 */
public class SQLServer2008Templates extends SQLServer2005Templates {

  @SuppressWarnings("FieldNameHidesFieldInSuperclass") // Intentional
  public static final SQLServer2008Templates DEFAULT = new SQLServer2008Templates();

  public static Builder builder() {
    return new Builder() {
      @Override
      protected SQLTemplates build(char escape, boolean quote) {
        return new SQLServer2008Templates(escape, quote);
      }
    };
  }

  public SQLServer2008Templates() {
    this(Keywords.SQLSERVER2008, '\\', false);
  }

  public SQLServer2008Templates(boolean quote) {
    this(Keywords.SQLSERVER2008, '\\', quote);
  }

  public SQLServer2008Templates(char escape, boolean quote) {
    this(Keywords.SQLSERVER2008, escape, quote);
  }

  protected SQLServer2008Templates(Set<String> keywords, char escape, boolean quote) {
    super(keywords, escape, quote);
  }
}
