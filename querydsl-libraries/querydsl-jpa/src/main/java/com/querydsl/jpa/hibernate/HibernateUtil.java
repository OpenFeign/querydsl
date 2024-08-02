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
package com.querydsl.jpa.hibernate;

import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.ParamNotSetException;
import com.querydsl.core.types.dsl.Param;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.type.BasicTypeReference;
import org.hibernate.type.StandardBasicTypes;

/**
 * {@code HibernateUtil} provides static utility methods for Hibernate
 *
 * @author tiwe
 */
public final class HibernateUtil {

  private static final Set<Class<?>> BUILT_IN =
      Collections.unmodifiableSet(
          new HashSet<>(
              Arrays.asList(
                  Boolean.class,
                  Byte.class,
                  Character.class,
                  Double.class,
                  Float.class,
                  Integer.class,
                  Long.class,
                  Short.class,
                  String.class,
                  BigDecimal.class,
                  byte[].class,
                  Byte[].class,
                  java.util.Date.class,
                  java.util.Calendar.class,
                  java.sql.Date.class,
                  java.sql.Time.class,
                  java.sql.Timestamp.class,
                  java.util.Locale.class,
                  java.util.TimeZone.class,
                  java.util.Currency.class,
                  Class.class,
                  java.io.Serializable.class,
                  java.sql.Blob.class,
                  java.sql.Clob.class)));

  private static final Map<Class<?>, BasicTypeReference<?>> TYPES;

  static {
    Map<Class<?>, BasicTypeReference<?>> builder = new HashMap<>();
    builder.put(Byte.class, StandardBasicTypes.BYTE);
    builder.put(Short.class, StandardBasicTypes.SHORT);
    builder.put(Integer.class, StandardBasicTypes.INTEGER);
    builder.put(Long.class, StandardBasicTypes.LONG);
    builder.put(BigInteger.class, StandardBasicTypes.BIG_INTEGER);
    builder.put(Double.class, StandardBasicTypes.DOUBLE);
    builder.put(Float.class, StandardBasicTypes.FLOAT);
    builder.put(BigDecimal.class, StandardBasicTypes.BIG_DECIMAL);
    builder.put(String.class, StandardBasicTypes.STRING);
    builder.put(Character.class, StandardBasicTypes.CHARACTER);
    builder.put(Date.class, StandardBasicTypes.DATE);
    builder.put(Boolean.class, StandardBasicTypes.BOOLEAN);
    TYPES = Collections.unmodifiableMap(builder);
  }

  private HibernateUtil() {}

  public static void setConstants(
      org.hibernate.query.Query<?> query,
      List<Object> constants,
      Map<ParamExpression<?>, Object> params) {
    for (var i = 0; i < constants.size(); i++) {
      var val = constants.get(i);

      if (val instanceof Param) {
        Param<?> param = (Param<?>) val;
        val = params.get(val);
        if (val == null) {
          throw new ParamNotSetException(param);
        }
      }

      setValueWithNumberedLabel(query, i + 1, val);
    }
  }

  private static void setValueWithNumberedLabel(
      org.hibernate.query.Query<?> query, Integer key, Object val) {
    if (val instanceof Collection<?> collection) {
      query.setParameterList(key, collection);
    } else if (val instanceof Object[] objects && !BUILT_IN.contains(val.getClass())) {
      query.setParameterList(key, objects);
    } else if (val instanceof Number && TYPES.containsKey(val.getClass())) {
      query.setParameter(key, val, getType(val.getClass()));
    } else {
      query.setParameter(key, val);
    }
  }

  public static BasicTypeReference getType(Class<?> clazz) {
    return TYPES.get(clazz);
  }
}
