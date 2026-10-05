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
import org.postgresql.util.PGobject;

/** Binds {@code float[]} to the pgvector {@code vector} type */
public class PGvectorType extends AbstractType<float[]> {

  public static final PGvectorType DEFAULT = new PGvectorType();

  public PGvectorType() {
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
    var vector = new PGobject();
    vector.setType("vector");
    vector.setValue(VectorText.format(value));
    st.setObject(startIndex, vector);
  }

  @Override
  public String getLiteral(float[] value) {
    return "'" + VectorText.format(value) + "'::vector";
  }
}
