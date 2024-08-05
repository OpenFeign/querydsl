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
package com.querydsl.spatial;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.jetbrains.annotations.Nullable;

/**
 * A Surface is a 2-dimensional geometric object.
 *
 * @author tiwe
 * @param <T>
 */
public abstract class SurfaceExpression<T extends Geometry> extends GeometryExpression<T> {

  private static final long serialVersionUID = 3534197011234723698L;

  @Nullable private transient volatile PointExpression<Point> centroid, pointOnSurface;

  @Nullable private transient volatile NumberExpression<Double> area;

  public SurfaceExpression(Expression<T> mixin) {
    super(mixin);
  }

  /**
   * The area of this Surface, as measured in the spatial reference system of this Surface.
   *
   * @return area
   */
  public NumberExpression<Double> area() {
    if (area == null) {
      area = Expressions.numberOperation(Double.class, SpatialOps.AREA, mixin);
    }
    return area;
  }

  /**
   * The mathematical centroid for this Surface as a Point. The result is not guaranteed to be on
   * this Surface.
   *
   * @return centroid
   */
  public PointExpression<Point> centroid() {
    if (centroid == null) {
      centroid = GeometryExpressions.pointOperation(SpatialOps.CENTROID, mixin);
    }
    return centroid;
  }

  /**
   * A Point guaranteed to be on this Surface.
   *
   * @return point on surface
   */
  public PointExpression<Point> pointOnSurface() {
    if (pointOnSurface == null) {
      pointOnSurface = GeometryExpressions.pointOperation(SpatialOps.POINT_ON_SURFACE, mixin);
    }
    return pointOnSurface;
  }
}
