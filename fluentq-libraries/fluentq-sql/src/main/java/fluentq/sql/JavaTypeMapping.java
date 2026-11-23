/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.sql;

import fluentq.core.util.PrimitiveUtils;
import fluentq.core.util.ReflectionUtils;
import fluentq.sql.types.BigDecimalType;
import fluentq.sql.types.BigIntegerType;
import fluentq.sql.types.BlobType;
import fluentq.sql.types.BooleanType;
import fluentq.sql.types.ByteType;
import fluentq.sql.types.BytesType;
import fluentq.sql.types.CalendarType;
import fluentq.sql.types.CharacterType;
import fluentq.sql.types.ClobType;
import fluentq.sql.types.CurrencyType;
import fluentq.sql.types.DateType;
import fluentq.sql.types.DoubleType;
import fluentq.sql.types.FloatType;
import fluentq.sql.types.InstantType;
import fluentq.sql.types.IntegerType;
import fluentq.sql.types.LocalDateTimeType;
import fluentq.sql.types.LocalDateType;
import fluentq.sql.types.LocalTimeType;
import fluentq.sql.types.LocaleType;
import fluentq.sql.types.LongType;
import fluentq.sql.types.ObjectType;
import fluentq.sql.types.OffsetDateTimeType;
import fluentq.sql.types.OffsetTimeType;
import fluentq.sql.types.ShortType;
import fluentq.sql.types.StringType;
import fluentq.sql.types.TimeType;
import fluentq.sql.types.TimestampType;
import fluentq.sql.types.Type;
import fluentq.sql.types.URLType;
import fluentq.sql.types.UtilDateType;
import fluentq.sql.types.UtilUUIDType;
import fluentq.sql.types.ZonedDateTimeType;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * {@code JavaTypeMapping} provides a mapping from Class to Type instances
 *
 * @author tiwe
 */
class JavaTypeMapping {

  private static final Type<Object> DEFAULT = new ObjectType();

  private static final Map<Class<?>, Type<?>> defaultTypes = new HashMap<>();

  static {
    registerDefault(new BigIntegerType());
    registerDefault(new BigDecimalType());
    registerDefault(new BlobType());
    registerDefault(new BooleanType());
    registerDefault(new BytesType());
    registerDefault(new ByteType());
    registerDefault(new CharacterType());
    registerDefault(new CalendarType());
    registerDefault(new ClobType());
    registerDefault(new CurrencyType());
    registerDefault(new DateType());
    registerDefault(new DoubleType());
    registerDefault(new FloatType());
    registerDefault(new IntegerType());
    registerDefault(new LocaleType());
    registerDefault(new LongType());
    registerDefault(new ObjectType());
    registerDefault(new ShortType());
    registerDefault(new StringType());
    registerDefault(new TimestampType());
    registerDefault(new TimeType());
    registerDefault(new URLType());
    registerDefault(new UtilDateType());
    registerDefault(new UtilUUIDType(false));
    registerDefault(new InstantType());
    registerDefault(new LocalDateTimeType());
    registerDefault(new LocalDateType());
    registerDefault(new LocalTimeType());
    registerDefault(new OffsetDateTimeType());
    registerDefault(new OffsetTimeType());
    registerDefault(new ZonedDateTimeType());
  }

  private static void registerDefault(Type<?> type) {
    defaultTypes.put(type.getReturnedClass(), type);
    Class<?> primitive = PrimitiveUtils.unwrap(type.getReturnedClass());
    if (primitive != null) {
      defaultTypes.put(primitive, type);
    }
  }

  private final Map<Class<?>, Type<?>> typeByClass = new HashMap<>();

  private final Map<Class<?>, Type<?>> resolvedTypesByClass = new HashMap<>();

  private final Map<String, Map<String, Type<?>>> typeByColumn = new HashMap<>();

  @Nullable
  public Type<?> getType(String table, String column) {
    var columns = typeByColumn.get(table);
    if (columns != null) {
      return columns.get(column);
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> Type<T> getType(Class<T> clazz) {
    Type<?> resolvedType = resolvedTypesByClass.get(clazz);
    if (resolvedType == null) {
      resolvedType = findType(clazz);
      if (resolvedType != null) {
        resolvedTypesByClass.put(clazz, resolvedType);
      } else {
        return (Type) DEFAULT;
      }
    }
    return (Type<T>) resolvedType;
  }

  @Nullable
  private Type<?> findType(Class<?> clazz) {
    // Look for a registered type in the class hierarchy
    Class<?> cl = clazz;
    do {
      if (typeByClass.containsKey(cl)) {
        return typeByClass.get(cl);
      } else if (defaultTypes.containsKey(cl)) {
        return defaultTypes.get(cl);
      }
      cl = cl.getSuperclass();
    } while (!cl.equals(Object.class));

    // Look for a registered type in any implemented interfaces
    var interfaces = ReflectionUtils.getImplementedInterfaces(clazz);
    for (Class<?> itf : interfaces) {
      if (typeByClass.containsKey(itf)) {
        return typeByClass.get(itf);
      } else if (defaultTypes.containsKey(itf)) {
        return defaultTypes.get(itf);
      }
    }
    return null;
  }

  public void register(Type<?> type) {
    typeByClass.put(type.getReturnedClass(), type);
    Class<?> primitive = PrimitiveUtils.unwrap(type.getReturnedClass());
    if (primitive != null) {
      typeByClass.put(primitive, type);
    }
    // Clear previous resolved types, so they won't impact future lookups
    resolvedTypesByClass.clear();
  }

  public void setType(String table, String column, Type<?> type) {
    var columns = typeByColumn.computeIfAbsent(table, k -> new HashMap<String, Type<?>>());
    columns.put(column, type);
  }
}
