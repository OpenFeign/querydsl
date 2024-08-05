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
package com.querydsl.spatial.jts;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.spatial.SpatialOps;
import com.vividsolutions.jts.geom.Point;
import org.jetbrains.annotations.Nullable;

/**
 * A Point is a 0-dimensional geometric object and represents a single location in coordinate space.
 * A Point has an x-coordinate value, a y-coordinate value. If called for by the associated Spatial
 * Reference System, it may also have coordinate values for z and m.
 *
 * @author tiwe
 * @param <T>
 */
public abstract class JTSPointExpression<T extends Point> extends JTSGeometryExpression<T> {

  private static final long serialVersionUID = -3549448861390349654L;

  @Nullable private transient volatile NumberExpression<Double> x, y, z, m;

  public JTSPointExpression(Expression<T> mixin) {
    super(mixin);
  }

  /**
   * The x-coordinate value for this Point.
   *
   * @return x-coordinate
   */
  public NumberExpression<Double> x() {
    if (x == null) {
      x = Expressions.numberOperation(Double.class, SpatialOps.X, mixin);
    }
    return x;
  }

  /**
   * The y-coordinate value for this Point.
   *
   * @return y-coordinate
   */
  public NumberExpression<Double> y() {
    if (y == null) {
      y = Expressions.numberOperation(Double.class, SpatialOps.Y, mixin);
    }
    return y;
  }

  /**
   * The z-coordinate value for this Point, if it has one. Returns NIL otherwise.
   *
   * @return z-coordinate
   */
  public NumberExpression<Double> z() {
    if (z == null) {
      z = Expressions.numberOperation(Double.class, SpatialOps.Z, mixin);
    }
    return z;
  }

  /**
   * The m-coordinate value for this Point, if it has one. Returns NIL otherwise.
   *
   * @return m-coordinate
   */
  public NumberExpression<Double> m() {
    if (m == null) {
      m = Expressions.numberOperation(Double.class, SpatialOps.M, mixin);
    }
    return m;
  }
}
