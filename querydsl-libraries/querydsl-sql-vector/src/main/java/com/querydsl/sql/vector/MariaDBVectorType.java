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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.jetbrains.annotations.Nullable;

/**
 * Binds {@code float[]} to the MariaDB {@code VECTOR} type, which stores little-endian 32-bit
 * floats
 */
public class MariaDBVectorType extends AbstractType<float[]> {

  public static final MariaDBVectorType DEFAULT = new MariaDBVectorType();

  public MariaDBVectorType() {
    super(Types.OTHER);
  }

  @Override
  public Class<float[]> getReturnedClass() {
    return float[].class;
  }

  @Override
  @Nullable
  public float[] getValue(ResultSet rs, int startIndex) throws SQLException {
    var bytes = rs.getBytes(startIndex);
    if (bytes == null) {
      return null;
    }
    var floats = new float[bytes.length / Float.BYTES];
    ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(floats);
    return floats;
  }

  @Override
  public void setValue(PreparedStatement st, int startIndex, float[] value) throws SQLException {
    var bytes = ByteBuffer.allocate(value.length * Float.BYTES).order(ByteOrder.LITTLE_ENDIAN);
    bytes.asFloatBuffer().put(value);
    st.setBytes(startIndex, bytes.array());
  }

  @Override
  public String getLiteral(float[] vector) {
    return "vec_fromtext('" + VectorText.format(vector) + "')";
  }
}
