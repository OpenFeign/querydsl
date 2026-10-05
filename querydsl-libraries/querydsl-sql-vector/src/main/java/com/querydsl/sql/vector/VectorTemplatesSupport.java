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
package com.querydsl.sql.vector;

import com.querydsl.core.types.Operator;
import com.querydsl.vector.VectorOps;
import java.util.HashMap;
import java.util.Map;

/** Vector operator templates per database, for composing vector enabled SQLTemplates */
public final class VectorTemplatesSupport {

  private VectorTemplatesSupport() {}

  public static Map<Operator, String> getPGvectorOps() {
    Map<Operator, String> ops = new HashMap<>();
    ops.put(VectorOps.L2_DISTANCE, "({0} <-> {1})");
    ops.put(VectorOps.L2_SQUARED_DISTANCE, "(({0} <-> {1}) ^ 2)");
    ops.put(VectorOps.COSINE_DISTANCE, "({0} <=> {1})");
    ops.put(VectorOps.INNER_PRODUCT, "(({0} <#> {1}) * -1)");
    ops.put(VectorOps.NEGATIVE_INNER_PRODUCT, "({0} <#> {1})");
    ops.put(VectorOps.L1_DISTANCE, "({0} <+> {1})");
    ops.put(VectorOps.DIMS, "vector_dims({0})");
    ops.put(VectorOps.NORM, "vector_norm({0})");
    return ops;
  }

  public static Map<Operator, String> getOracleOps() {
    Map<Operator, String> ops = new HashMap<>();
    ops.put(VectorOps.L2_DISTANCE, "vector_distance({0}, {1}, EUCLIDEAN)");
    ops.put(VectorOps.L2_SQUARED_DISTANCE, "vector_distance({0}, {1}, EUCLIDEAN_SQUARED)");
    ops.put(VectorOps.COSINE_DISTANCE, "vector_distance({0}, {1}, COSINE)");
    ops.put(VectorOps.INNER_PRODUCT, "(vector_distance({0}, {1}, DOT) * -1)");
    ops.put(VectorOps.NEGATIVE_INNER_PRODUCT, "vector_distance({0}, {1}, DOT)");
    ops.put(VectorOps.L1_DISTANCE, "vector_distance({0}, {1}, MANHATTAN)");
    ops.put(VectorOps.DIMS, "vector_dimension_count({0})");
    ops.put(VectorOps.NORM, "vector_norm({0})");
    return ops;
  }
}
