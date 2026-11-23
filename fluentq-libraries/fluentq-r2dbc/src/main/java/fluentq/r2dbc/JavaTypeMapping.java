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
package fluentq.r2dbc;

import fluentq.core.util.PrimitiveUtils;
import fluentq.core.util.ReflectionUtils;
import fluentq.r2dbc.types.BigDecimalType;
import fluentq.r2dbc.types.BigIntegerType;
import fluentq.r2dbc.types.BlobType;
import fluentq.r2dbc.types.BooleanType;
import fluentq.r2dbc.types.ByteType;
import fluentq.r2dbc.types.BytesType;
import fluentq.r2dbc.types.CharacterType;
import fluentq.r2dbc.types.ClobType;
import fluentq.r2dbc.types.CurrencyType;
import fluentq.r2dbc.types.DateType;
import fluentq.r2dbc.types.DoubleType;
import fluentq.r2dbc.types.FloatType;
import fluentq.r2dbc.types.IntegerType;
import fluentq.r2dbc.types.JSR310InstantType;
import fluentq.r2dbc.types.JSR310LocalDateTimeType;
import fluentq.r2dbc.types.JSR310LocalDateType;
import fluentq.r2dbc.types.JSR310LocalTimeType;
import fluentq.r2dbc.types.JSR310OffsetDateTimeType;
import fluentq.r2dbc.types.JSR310OffsetTimeType;
import fluentq.r2dbc.types.JSR310ZonedDateTimeType;
import fluentq.r2dbc.types.LocaleType;
import fluentq.r2dbc.types.LongType;
import fluentq.r2dbc.types.NullType;
import fluentq.r2dbc.types.ObjectType;
import fluentq.r2dbc.types.ShortType;
import fluentq.r2dbc.types.StringType;
import fluentq.r2dbc.types.TimeType;
import fluentq.r2dbc.types.TimestampType;
import fluentq.r2dbc.types.Type;
import fluentq.r2dbc.types.URLType;
import fluentq.r2dbc.types.UtilDateType;
import fluentq.r2dbc.types.UtilUUIDType;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * {@code JavaTypeMapping} provides a mapping from Class to Type instances
 *
 * @author mc_fish
 */
class JavaTypeMapping {

  private static final Type<Object, Object> DEFAULT = new ObjectType();

  private static final Map<Class<?>, Type<?, ?>> defaultTypes = new HashMap<>();

  static {
    registerDefault(new BigIntegerType());
    registerDefault(new BigDecimalType());
    registerDefault(new BlobType());
    registerDefault(new BooleanType());
    registerDefault(new BytesType());
    registerDefault(new ByteType());
    registerDefault(new CharacterType());
    registerDefault(new ClobType());
    registerDefault(new CurrencyType());
    registerDefault(new DateType());
    registerDefault(new DoubleType());
    registerDefault(new FloatType());
    registerDefault(new IntegerType());
    registerDefault(new LocaleType());
    registerDefault(new LongType());
    registerDefault(new NullType());
    registerDefault(new ObjectType());
    registerDefault(new ShortType());
    registerDefault(new StringType());
    registerDefault(new TimestampType());
    registerDefault(new TimeType());
    registerDefault(new URLType());
    registerDefault(new UtilDateType());
    registerDefault(new UtilUUIDType());

    // initialize java time api (JSR 310) converters only if java 8 is available
    registerDefault(new JSR310InstantType());
    registerDefault(new JSR310LocalDateTimeType());
    registerDefault(new JSR310LocalDateType());
    registerDefault(new JSR310LocalTimeType());
    registerDefault(new JSR310OffsetDateTimeType());
    registerDefault(new JSR310OffsetTimeType());
    registerDefault(new JSR310ZonedDateTimeType());
  }

  private static void registerDefault(Type<?, ?> type) {
    defaultTypes.put(type.getReturnedClass(), type);
    Class<?> primitive = PrimitiveUtils.unwrap(type.getReturnedClass());
    if (primitive != null) {
      defaultTypes.put(primitive, type);
    }
  }

  private final Map<Class<?>, Type<?, ?>> typeByClass = new HashMap<>();

  private final Map<Class<?>, Type<?, ?>> resolvedTypesByClass = new HashMap<>();

  private final Map<String, Map<String, Type<?, ?>>> typeByColumn = new HashMap<>();

  @Nullable
  public Type<?, ?> getType(String table, String column) {
    var columns = typeByColumn.get(table);
    if (columns != null) {
      return columns.get(column);
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> Type<T, ?> getType(Class<T> clazz) {
    Type<?, ?> resolvedType = resolvedTypesByClass.get(clazz);
    if (resolvedType == null) {
      resolvedType = findType(clazz);
      if (resolvedType != null) {
        resolvedTypesByClass.put(clazz, resolvedType);
      } else {
        return (Type) DEFAULT;
      }
    }
    return (Type<T, ?>) resolvedType;
  }

  @Nullable
  private Type<?, ?> findType(Class<?> clazz) {
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

  public void register(Type<?, ?> type) {
    typeByClass.put(type.getReturnedClass(), type);
    Class<?> primitive = PrimitiveUtils.unwrap(type.getReturnedClass());
    if (primitive != null) {
      typeByClass.put(primitive, type);
    }
    // Clear previous resolved types, so they won't impact future lookups
    resolvedTypesByClass.clear();
  }

  public void setType(String table, String column, Type<?, ?> type) {
    var columns = typeByColumn.get(table);
    if (columns == null) {
      columns = new HashMap<>();
      typeByColumn.put(table, columns);
    }
    columns.put(column, type);
  }
}
