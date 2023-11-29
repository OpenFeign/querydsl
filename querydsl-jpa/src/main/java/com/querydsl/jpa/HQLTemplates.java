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
package com.querydsl.jpa;

import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HQLTemplates extends {@link JPQLTemplates} with Hibernate specific extensions
 *
 * @author tiwe
 */
public class HQLTemplates extends JPQLTemplates {

  private static final QueryHandler QUERY_HANDLER;

  static {
    QueryHandler instance;
    try {
      instance = (QueryHandler) Class.forName("com.querydsl.jpa.HibernateHandler").newInstance();
    } catch (NoClassDefFoundError | Exception e) {
      instance = DefaultQueryHandler.DEFAULT;
    }
    QUERY_HANDLER = instance;
  }

  private static final List<Operator> wrapElements =
      Arrays.<Operator>asList(
          Ops.QuantOps.ALL, Ops.QuantOps.ANY, Ops.QuantOps.AVG_IN_COL, Ops.EXISTS);

  public static final HQLTemplates DEFAULT = new HQLTemplates();

  private final Map<Class<?>, String> typeNames;

  public HQLTemplates() {
    this(DEFAULT_ESCAPE);
  }

  @SuppressWarnings("unchecked")
  public HQLTemplates(char escape) {
    super(escape, QUERY_HANDLER);

    Map<Class<?>, String> builder = new HashMap<>();
    builder.put(Byte.class, "byte");
    builder.put(Short.class, "short");
    builder.put(Integer.class, "integer");
    builder.put(Long.class, "long");
    builder.put(BigInteger.class, "big_integer");
    builder.put(Float.class, "float");
    builder.put(Double.class, "double");
    builder.put(BigDecimal.class, "big_decimal");
    typeNames = Collections.unmodifiableMap(builder);

    // add Hibernate Spatial mappings, if on classpath
    try {
      Class cl = Class.forName("com.querydsl.spatial.hibernate.HibernateSpatialSupport");
      add((Map) cl.getMethod("getSpatialOps").invoke(null));
    } catch (Exception e) {
      // do nothing
    }
  }

  @Override
  public boolean wrapElements(Operator operator) {
    // For example: JPaIntegration.docoExamples98_12
    return wrapElements.contains(operator);
  }

  @Override
  public String getTypeForCast(Class<?> cl) {
    String typeName = typeNames.get(cl);
    if (typeName == null) {
      return super.getTypeForCast(cl);
    }
    return typeName;
  }

  @Override
  public String getExistsProjection() {
    // TODO Required / supported just for Hibernate?
    return "1";
  }

  @Override
  public boolean wrapConstant(Object constant) {
    // related : https://hibernate.onjira.com/browse/HHH-6913
    Class<?> type = constant.getClass();
    return type.isArray() || Collection.class.isAssignableFrom(type);
  }

  @Override
  public boolean isWithForOn() {
    return true;
  }

  @Override
  public boolean isCaseWithLiterals() {
    return true;
  }
}
