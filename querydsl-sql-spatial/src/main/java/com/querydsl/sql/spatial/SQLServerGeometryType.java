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
import org.geolatte.geom.Geometry;
import org.geolatte.geom.codec.Wkt;
import org.geolatte.geom.codec.db.sqlserver.Decoders;
import org.geolatte.geom.codec.db.sqlserver.Encoders;
import org.jetbrains.annotations.Nullable;

class SQLServerGeometryType extends AbstractType<Geometry> {

  public static final SQLServerGeometryType DEFAULT = new SQLServerGeometryType();

  private static final int DEFAULT_SRID = 4326;

  SQLServerGeometryType() {
    super(Types.BLOB);
  }

  @Override
  public Class<Geometry> getReturnedClass() {
    return Geometry.class;
  }

  @Override
  @Nullable
  public Geometry getValue(ResultSet rs, int startIndex) throws SQLException {
    byte[] bytes = rs.getBytes(startIndex);
    if (bytes != null) {
      return Decoders.decode(bytes);
    } else {
      return null;
    }
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, Geometry value) throws SQLException {
    byte[] bytes = Encoders.encode(value);
    st.setBytes(startIndex, bytes);
  }

  @Override
  public String getLiteral(Geometry geometry) {
    String str = Wkt.newEncoder(Wkt.Dialect.POSTGIS_EWKT_1).encode(geometry);
    if (geometry.getSRID() > -1) {
      return "geometry::STGeomFromText('" + str + "', " + geometry.getSRID() + ")";
    } else {
      return "geometry::STGeomFromText('" + str + "', " + DEFAULT_SRID + ")";
    }
  }
}
