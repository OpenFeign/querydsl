/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.sql.Time;
import org.junit.Test;

public class TypeCategoryTest {

  @Test
  public void IsSubCategoryOf() {
    assertThat(TypeCategory.BOOLEAN.isSubCategoryOf(TypeCategory.COMPARABLE)).isTrue();
    assertThat(TypeCategory.STRING.isSubCategoryOf(TypeCategory.COMPARABLE)).isTrue();
    assertThat(TypeCategory.NUMERIC.isSubCategoryOf(TypeCategory.COMPARABLE)).isTrue();
    assertThat(TypeCategory.COMPARABLE.isSubCategoryOf(TypeCategory.SIMPLE)).isTrue();
    assertThat(TypeCategory.ENTITY.isSubCategoryOf(TypeCategory.SIMPLE)).isFalse();
  }

  @Test
  public void Get() {
    assertThat(TypeCategory.get(Boolean.class.getName())).isEqualTo(TypeCategory.BOOLEAN);
    assertThat(TypeCategory.get(String.class.getName())).isEqualTo(TypeCategory.STRING);
    assertThat(TypeCategory.get(Date.class.getName())).isEqualTo(TypeCategory.DATE);
    assertThat(TypeCategory.get(Time.class.getName())).isEqualTo(TypeCategory.TIME);
  }

  @Test
  public void Java8() {
    assertThat(TypeCategory.get("java.time.Instant")).isEqualTo(TypeCategory.DATETIME);
    assertThat(TypeCategory.get("java.time.LocalDate")).isEqualTo(TypeCategory.DATE);
    assertThat(TypeCategory.get("java.time.LocalDateTime")).isEqualTo(TypeCategory.DATETIME);
    assertThat(TypeCategory.get("java.time.LocalTime")).isEqualTo(TypeCategory.TIME);
    assertThat(TypeCategory.get("java.time.OffsetDateTime")).isEqualTo(TypeCategory.DATETIME);
    assertThat(TypeCategory.get("java.time.OffsetTime")).isEqualTo(TypeCategory.TIME);
    assertThat(TypeCategory.get("java.time.ZonedDateTime")).isEqualTo(TypeCategory.DATETIME);
  }
}
