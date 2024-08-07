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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;

/**
 * {@code SQLXMLType} maps SQLXML to SQLXML on the JDBC level
 *
 * @author tiwe
 */
public class SQLXMLType extends AbstractType<SQLXML> {

  public SQLXMLType() {
    super(Types.SQLXML);
  }

  public SQLXMLType(int type) {
    super(type);
  }

  @Override
  public SQLXML getValue(ResultSet rs, int startIndex) throws SQLException {
    return rs.getSQLXML(startIndex);
  }

  @Override
  public Class<SQLXML> getReturnedClass() {
    return SQLXML.class;
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, SQLXML value) throws SQLException {
    st.setSQLXML(startIndex, value);
  }
}
