/*
 * Copyright 2010, Mysema Ltd
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
package com.querydsl.codegen.utils.model;

import java.util.HashSet;
import java.util.Set;

/**
 * TypeCategory defines the expression type used for a Field
 *
 * @author tiwe
 */
public enum TypeCategory {
  /** */
  SIMPLE(null),
  /** */
  MAP(null),
  /** */
  COLLECTION(null),
  /** */
  LIST(COLLECTION),
  /** */
  SET(COLLECTION),
  /** */
  ARRAY(null),
  /** */
  COMPARABLE(SIMPLE),
  /** */
  BOOLEAN(COMPARABLE, Boolean.class.getName()),
  /** */
  DATE(COMPARABLE, java.sql.Date.class.getName(), java.time.LocalDate.class.getName()),
  /** */
  DATETIME(
      COMPARABLE,
      java.util.Calendar.class.getName(),
      java.util.Date.class.getName(),
      java.sql.Timestamp.class.getName(),
      java.time.Instant.class.getName(),
      java.time.LocalDateTime.class.getName(),
      java.time.OffsetDateTime.class.getName(),
      java.time.ZonedDateTime.class.getName()),
  /** */
  ENUM(COMPARABLE),
  /** */
  CUSTOM(null),

  /** */
  ENTITY(null),

  /** */
  NUMERIC(COMPARABLE),
  /** */
  STRING(COMPARABLE, String.class.getName()),
  /** */
  TIME(
      COMPARABLE,
      java.sql.Time.class.getName(),
      java.time.LocalTime.class.getName(),
      java.time.OffsetTime.class.getName());

  private final TypeCategory superType;

  private final Set<String> types;

  TypeCategory(TypeCategory superType, String... types) {
    this.superType = superType;
    this.types = new HashSet<String>(types.length);
    for (String type : types) {
      this.types.add(type);
    }
  }

  public TypeCategory getSuperType() {
    return superType;
  }

  public boolean supports(Class<?> cl) {
    return supports(cl.getName());
  }

  public boolean supports(String className) {
    return types.contains(className);
  }

  /**
   * transitive and reflexive subCategoryOf check
   *
   * @param ancestor
   * @return
   */
  public boolean isSubCategoryOf(TypeCategory ancestor) {
    if (this == ancestor) {
      return true;
    } else if (superType == null) {
      return false;
    } else {
      return superType == ancestor || superType.isSubCategoryOf(ancestor);
    }
  }

  public static TypeCategory get(String className) {
    for (TypeCategory category : values()) {
      if (category.supports(className)) {
        return category;
      }
    }
    return SIMPLE;
  }
}
