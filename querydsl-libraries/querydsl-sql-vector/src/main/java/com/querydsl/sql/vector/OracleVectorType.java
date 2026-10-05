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
import oracle.jdbc.OracleType;
import org.jetbrains.annotations.Nullable;

/** Binds {@code float[]} to the Oracle 23ai {@code VECTOR} type */
public class OracleVectorType extends AbstractType<float[]> {

  public static final OracleVectorType DEFAULT = new OracleVectorType();

  public OracleVectorType() {
    super(Types.OTHER);
  }

  @Override
  public Class<float[]> getReturnedClass() {
    return float[].class;
  }

  @Override
  @Nullable
  public float[] getValue(ResultSet rs, int startIndex) throws SQLException {
    return rs.getObject(startIndex, float[].class);
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, float[] value) throws SQLException {
    st.setObject(startIndex, value, OracleType.VECTOR_FLOAT32);
  }

  @Override
  public String getLiteral(float[] vector) {
    return "TO_VECTOR('" + VectorText.format(vector) + "')";
  }
}
