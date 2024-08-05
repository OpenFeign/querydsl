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
package com.querydsl.sql.spatial;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import net.postgis.jdbc.jts.JtsGeometry;
import org.locationtech.jts.geom.Geometry;

class JtsGeometryType extends AbstractType<Geometry> {

  public static final JtsGeometryType DEFAULT = new JtsGeometryType();

  public JtsGeometryType() {
    super(Types.STRUCT);
  }

  @Override
  public Class<Geometry> getReturnedClass() {
    return Geometry.class;
  }

  @Override
  public Geometry getValue(ResultSet rs, int startIndex) throws SQLException {
    var value = rs.getObject(startIndex, JtsGeometry.class);
    return value.getGeometry();
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, Geometry value) throws SQLException {
    st.setObject(startIndex, new JtsGeometry(value));
  }
}
