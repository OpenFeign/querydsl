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
package com.querydsl.vector;

import com.querydsl.core.types.Operator;

/** {@code VectorOps} provides {@link Operator} instances for vector similarity operations */
public enum VectorOps implements Operator {
  L2_DISTANCE(Double.class),
  L2_SQUARED_DISTANCE(Double.class),
  COSINE_DISTANCE(Double.class),
  INNER_PRODUCT(Double.class),
  NEGATIVE_INNER_PRODUCT(Double.class),
  L1_DISTANCE(Double.class),
  DIMENSION_COUNT(Integer.class),
  L2_NORM(Double.class);

  private final Class<?> type;

  VectorOps(Class<?> type) {
    this.type = type;
  }

  @Override
  public Class<?> getType() {
    return type;
  }
}
