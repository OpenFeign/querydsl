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
import java.sql.Types;
import org.jetbrains.annotations.Nullable;

/**
 * Binds {@code float[]} to the Db2 {@code VECTOR(n, FLOAT32)} type. The Db2 driver has no vector
 * parameter type, so each parameter is sent as text and converted with {@code vector(?, n,
 * float32)}.
 */
public class DB2VectorType extends AbstractType<float[]> {

  public static final DB2VectorType DEFAULT = new DB2VectorType();

  public DB2VectorType() {
    super(Types.OTHER);
  }

  @Override
  public Class<float[]> getReturnedClass() {
    return float[].class;
  }

  @Override
  @Nullable
  public float[] getValue(ResultSet rs, int startIndex) throws SQLException {
    var text = rs.getString(startIndex);
    return text != null ? VectorText.parse(text) : null;
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, float[] value) throws SQLException {
    st.setString(startIndex, VectorText.format(value));
  }

  @Override
  public String getLiteral(float[] vector) {
    return "vector('" + VectorText.format(vector) + "', " + vector.length + ", float32)";
  }

  @Override
  public String getParameterTemplate(float[] vector) {
    return "vector({0}, " + vector.length + ", float32)";
  }
}
