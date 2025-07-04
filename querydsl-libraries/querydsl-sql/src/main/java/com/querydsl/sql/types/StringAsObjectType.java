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

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * {@code StringAsObjectType} maps String to String on the JDBC level
 *
 * @author tiwe
 */
public class StringAsObjectType extends AbstractType<String> {

  public static final StringAsObjectType DEFAULT = new StringAsObjectType();

  public StringAsObjectType() {
    super(Types.VARCHAR);
  }

  public StringAsObjectType(int type) {
    super(type);
  }

  @Override
  public String getValue(ResultSet rs, int startIndex) throws SQLException {
    var o = rs.getObject(startIndex);
    if (o instanceof String string) {
      return string;
    } else if (o instanceof Clob clob) {
      return clob.getSubString(1, (int) clob.length());
    } else if (o != null) {
      return o.toString();
    } else {
      return null;
    }
  }

  @Override
  public Class<String> getReturnedClass() {
    return String.class;
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, String value) throws SQLException {
    st.setString(startIndex, value);
  }
}
