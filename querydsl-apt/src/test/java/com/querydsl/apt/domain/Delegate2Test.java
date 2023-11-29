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

import static org.junit.Assert.assertNotNull;

import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.junit.Test;

public class Delegate2Test {

  @QueryEntity
  public static class Entity {

    Point point;
  }

  public static class Point {}

  @QueryDelegate(Point.class)
  public static NumberExpression<Integer> geoDistance(Path<Point> point, Point other) {
    return Expressions.numberTemplate(
        Integer.class, "geo_distance({0},{1})", point, ConstantImpl.create(other));
  }

  @Test
  public void test() {
    QDelegate2Test_Entity entity = QDelegate2Test_Entity.entity;
    assertNotNull(entity.point.geoDistance(new Point()));
  }
}
