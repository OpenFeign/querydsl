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

/**
 * {@code IndexData} defines index data
 *
 * @author tiwe
 */
public class IndexData {

  private final String name;

  private final String[] columns;

  private boolean unique;

  public IndexData(String name, String[] columns) {
    this.name = name;
    this.columns = columns.clone();
  }

  public String getName() {
    return name;
  }

  public String[] getColumns() {
    return columns.clone();
  }

  public boolean isUnique() {
    return unique;
  }

  public void setUnique(boolean unique) {
    this.unique = unique;
  }
}
