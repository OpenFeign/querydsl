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
package fluentq.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.annotations.QueryDelegate;
import fluentq.core.annotations.QueryEntity;
import fluentq.core.types.ConstantImpl;
import fluentq.core.types.Path;
import fluentq.core.types.dsl.Expressions;
import fluentq.core.types.dsl.NumberExpression;
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
    var entity = QDelegate2Test_Entity.entity;
    assertThat(entity.point.geoDistance(new Point())).isNotNull();
  }
}
