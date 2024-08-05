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

import com.querydsl.core.util.PrimitiveUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.jetbrains.annotations.Nullable;

/**
 * {@code ArrayType} maps Java arrays to JDBC arrays
 *
 * @param <T>
 */
public class ArrayType<T> extends AbstractType<T> {

  private static void copy(Object source, Object target, int length) {
    // Note: System.arrayCopy doesn't handle copying from/to primitive arrays properly
    for (var i = 0; i < length; i++) {
      var val = java.lang.reflect.Array.get(source, i);
      java.lang.reflect.Array.set(target, i, val);
    }
  }

  private final Class<T> type;

  private final String typeName;

  private final boolean convertPrimitives;

  public ArrayType(Class<T> type, String typeName) {
    super(Types.ARRAY);
    this.type = type;
    this.typeName = typeName;
    this.convertPrimitives = type.getComponentType().isPrimitive();
  }

  @Override
  public Class<T> getReturnedClass() {
    return type;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public T getValue(ResultSet rs, int startIndex) throws SQLException {
    var arr = rs.getArray(startIndex);
    if (arr != null) {
      /*
       * The Javadoc for getArray() is annoyingly ambiguous about what it returns.
       *
       * It says that the method can return a primitive array.
       *
       * But it does not say anything about the type of the array when the method
       * returns an array of objects.
       *
       * In that case, it could return T[] (Postgres appears to do this)
       * or it could return Object[] (H2 and HSQLDB appear to do this).
       *
       * The JDBC specification does not offer any additional clarity.
       *
       * In any case, what we need to return is T[]. Otherwise the caller will get
       * ClassCastExceptions at runtime.
       *
       * Note that we cannot cast arr.getArray() to Object[] because, if the returned
       * array is a primitive array, that would cause ClassCastException.
       */
      var rv = arr.getArray();
      if (!type.isAssignableFrom(rv.getClass())) {
        var length = java.lang.reflect.Array.getLength(rv);
        var rv2 = java.lang.reflect.Array.newInstance(type.getComponentType(), length);
        copy(rv, rv2, length);
        return (T) rv2;
      } else {
        return (T) rv;
      }
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setValue(PreparedStatement st, int startIndex, T value) throws SQLException {
    if (convertPrimitives) {
      // primitives in
      var length = java.lang.reflect.Array.getLength(value);
      var value2 =
          java.lang.reflect.Array.newInstance(PrimitiveUtils.wrap(type.getComponentType()), length);
      copy(value, value2, length);
      value = (T) value2;
    }
    var arr = st.getConnection().createArrayOf(typeName, (Object[]) value);
    st.setArray(startIndex, arr);
  }
}
