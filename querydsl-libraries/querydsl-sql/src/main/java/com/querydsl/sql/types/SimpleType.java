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
package com.querydsl.sql.types;

import java.sql.*;
import org.jetbrains.annotations.Nullable;

/** {@code SimpleType} maps Java classes on the JDBC level */
public class SimpleType implements Type<Object> {

  private String classname;

  public SimpleType(String classname) {
    this.classname = classname;
  }

  @Override
  public int[] getSQLTypes() {
    return new int[] {Types.OTHER};
  }

  @Override
  public Class<Object> getReturnedClass() {
    return Object.class;
  }

  @Override
  public String getLiteral(Object value) {
    return classname;
  }

  public String getClassname() {
    return classname;
  }

  @Override
  public @Nullable Object getValue(ResultSet rs, int startIndex) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, Object value) throws SQLException {
    // TODO Auto-generated method stub

  }
}
