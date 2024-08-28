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
package com.querydsl.apt.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.annotations.QueryType;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import jakarta.persistence.Entity;
import java.util.Map;
import javax.jdo.annotations.PersistenceCapable;
import org.junit.Test;

public class QueryProjectionTest {

  // all three annotations are needed, because all are used as entity annotations
  @QueryEntity
  @Entity
  @PersistenceCapable
  public static class EntityWithProjection {

    public EntityWithProjection(long id) {}

    @QueryProjection
    public EntityWithProjection(String param0) {}

    @QueryProjection
    public EntityWithProjection(@QueryType(PropertyType.SIMPLE) Long param0) {}

    @QueryProjection
    public EntityWithProjection(long param0, CharSequence param1) {}

    @QueryProjection
    public EntityWithProjection(String param0, CharSequence param1) {}
  }

  @Test
  public void entity_case() {
    NumberExpression<Long> longExpr = Expressions.numberPath(Long.class, "x");
    StringExpression stringExpr = Expressions.stringPath("x");

    QQueryProjectionTest_EntityWithProjection.create(longExpr).newInstance(0L);
    QQueryProjectionTest_EntityWithProjection.create(stringExpr).newInstance("");
    QQueryProjectionTest_EntityWithProjection.create(longExpr, stringExpr).newInstance(0L, "");
    QQueryProjectionTest_EntityWithProjection.create(stringExpr, stringExpr).newInstance("", "");
  }

  public static class DTOWithProjection {

    public DTOWithProjection(long id) {}

    @QueryProjection
    public DTOWithProjection(@QueryType(PropertyType.SIMPLE) Long param0) {}

    @QueryProjection
    public DTOWithProjection(String param0) {}

    @QueryProjection
    public DTOWithProjection(EntityWithProjection param0) {}

    @QueryProjection
    public DTOWithProjection(long param0, CharSequence param1) {}

    @QueryProjection
    public DTOWithProjection(String param0, CharSequence param1) {}

    @QueryProjection
    public DTOWithProjection(
        DTOWithProjection param0, long param1, Long param2, String param3, CharSequence param4) {}

    @QueryProjection
    public DTOWithProjection(
        String param0,
        CharSequence param1,
        Map<Long, String> param2,
        Map<DTOWithProjection, Long> param3) {}
  }

  @Test
  public void dto_case() throws SecurityException, NoSuchMethodException {
    NumberExpression<Long> longExpr = Expressions.numberPath(Long.class, "x");
    StringExpression stringExpr = Expressions.stringPath("x");

    new QQueryProjectionTest_DTOWithProjection(longExpr).newInstance(0L);
    new QQueryProjectionTest_DTOWithProjection(stringExpr).newInstance("");
    new QQueryProjectionTest_DTOWithProjection(longExpr, stringExpr).newInstance(0L, "");
    new QQueryProjectionTest_DTOWithProjection(stringExpr, stringExpr).newInstance("", "");
  }

  @QueryProjection
  public static record RecordProjection(long param0, String param1) {}

  @Test
  public void record_case() throws SecurityException, NoSuchMethodException {
    NumberExpression<Long> longExpr = Expressions.numberPath(Long.class, "x");
    StringExpression stringExpr = Expressions.stringPath("x");

    new QQueryProjectionTest_RecordProjection(longExpr, stringExpr).newInstance(0L, "");
  }
}
