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
package com.querydsl.vector.hibernate;

import com.querydsl.core.types.Operator;
import com.querydsl.vector.VectorOps;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code HibernateVectorSupport} maps vector operators to the HQL functions registered by {@code
 * hibernate-vector}
 */
public final class HibernateVectorSupport {

  private HibernateVectorSupport() {}

  public static Map<Operator, String> getVectorOps() {
    Map<Operator, String> ops = new HashMap<>();
    ops.put(VectorOps.L2_DISTANCE, "l2_distance({0}, {1})");
    ops.put(VectorOps.L2_SQUARED_DISTANCE, "l2_squared_distance({0}, {1})");
    ops.put(VectorOps.COSINE_DISTANCE, "cosine_distance({0}, {1})");
    ops.put(VectorOps.INNER_PRODUCT, "inner_product({0}, {1})");
    ops.put(VectorOps.NEGATIVE_INNER_PRODUCT, "negative_inner_product({0}, {1})");
    ops.put(VectorOps.L1_DISTANCE, "l1_distance({0}, {1})");
    ops.put(VectorOps.DIMS, "vector_dims({0})");
    ops.put(VectorOps.NORM, "vector_norm({0})");
    return ops;
  }
}
