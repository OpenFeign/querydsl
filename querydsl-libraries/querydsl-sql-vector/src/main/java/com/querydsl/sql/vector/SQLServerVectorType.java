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

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import microsoft.sql.Types;
import microsoft.sql.Vector;
import microsoft.sql.Vector.VectorDimensionType;
import org.jetbrains.annotations.Nullable;

/** Binds {@code float[]} to the SQL Server 2025 {@code VECTOR} type */
public class SQLServerVectorType extends AbstractType<float[]> {

  public static final SQLServerVectorType DEFAULT = new SQLServerVectorType();

  public SQLServerVectorType() {
    super(Types.VECTOR);
  }

  @Override
  public Class<float[]> getReturnedClass() {
    return float[].class;
  }

  @Override
  @Nullable
  public float[] getValue(ResultSet rs, int startIndex) throws SQLException {
    var vector = rs.getObject(startIndex, Vector.class);
    if (vector == null) {
      return null;
    }
    var components = vector.getData();
    var floats = new float[components.length];
    for (var i = 0; i < components.length; i++) {
      floats[i] = ((Number) components[i]).floatValue();
    }
    return floats;
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, float[] value) throws SQLException {
    var components = new Float[value.length];
    for (var i = 0; i < value.length; i++) {
      components[i] = value[i];
    }
    st.setObject(
        startIndex,
        new Vector(value.length, VectorDimensionType.FLOAT32, components),
        Types.VECTOR);
  }

  @Override
  public String getLiteral(float[] vector) {
    return "cast('" + VectorText.format(vector) + "' as vector(" + vector.length + "))";
  }
}
