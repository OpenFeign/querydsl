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

/**
 * Vector operator templates for each database. These help compose SQLTemplates that use vectors.
 */
public final class VectorOperatorSqlPatterns {

  private VectorOperatorSqlPatterns() {}

  public static Map<Operator, String> createPGvectorOperatorTemplates() {
    Map<Operator, String> operatorTemplates = new HashMap<>();
    operatorTemplates.put(VectorOps.L2_DISTANCE, "({0} <-> {1})");
    operatorTemplates.put(VectorOps.L2_SQUARED_DISTANCE, "(({0} <-> {1}) ^ 2)");
    operatorTemplates.put(VectorOps.COSINE_DISTANCE, "({0} <=> {1})");
    operatorTemplates.put(VectorOps.INNER_PRODUCT, "(({0} <#> {1}) * -1)");
    operatorTemplates.put(VectorOps.NEGATIVE_INNER_PRODUCT, "({0} <#> {1})");
    operatorTemplates.put(VectorOps.L1_DISTANCE, "({0} <+> {1})");
    operatorTemplates.put(VectorOps.DIMENSION_COUNT, "vector_dims({0})");
    operatorTemplates.put(VectorOps.L2_NORM, "vector_norm({0})");
    return operatorTemplates;
  }

  public static Map<Operator, String> createOracleOperatorTemplates() {
    Map<Operator, String> operatorTemplates = new HashMap<>();
    operatorTemplates.put(VectorOps.L2_DISTANCE, "vector_distance({0}, {1}, EUCLIDEAN)");
    operatorTemplates.put(
        VectorOps.L2_SQUARED_DISTANCE, "vector_distance({0}, {1}, EUCLIDEAN_SQUARED)");
    operatorTemplates.put(VectorOps.COSINE_DISTANCE, "vector_distance({0}, {1}, COSINE)");
    operatorTemplates.put(VectorOps.INNER_PRODUCT, "(vector_distance({0}, {1}, DOT) * -1)");
    operatorTemplates.put(VectorOps.NEGATIVE_INNER_PRODUCT, "vector_distance({0}, {1}, DOT)");
    operatorTemplates.put(VectorOps.L1_DISTANCE, "vector_distance({0}, {1}, MANHATTAN)");
    operatorTemplates.put(VectorOps.DIMENSION_COUNT, "vector_dimension_count({0})");
    operatorTemplates.put(VectorOps.L2_NORM, "vector_norm({0})");
    return operatorTemplates;
  }

  public static Map<Operator, String> createDB2OperatorTemplates() {
    var operatorTemplates = createOracleOperatorTemplates();
    operatorTemplates.put(VectorOps.L2_NORM, "vector_norm({0}, EUCLIDEAN)");
    return operatorTemplates;
  }

  /** SQL Server 2025 has no Manhattan distance, so {@link VectorOps#L1_DISTANCE} is missing */
  public static Map<Operator, String> createSQLServerOperatorTemplates() {
    Map<Operator, String> operatorTemplates = new HashMap<>();
    operatorTemplates.put(VectorOps.L2_DISTANCE, "vector_distance('euclidean', {0}, {1})");
    operatorTemplates.put(
        VectorOps.L2_SQUARED_DISTANCE, "square(vector_distance('euclidean', {0}, {1}))");
    operatorTemplates.put(VectorOps.COSINE_DISTANCE, "vector_distance('cosine', {0}, {1})");
    operatorTemplates.put(VectorOps.INNER_PRODUCT, "(vector_distance('dot', {0}, {1}) * -1)");
    operatorTemplates.put(VectorOps.NEGATIVE_INNER_PRODUCT, "vector_distance('dot', {0}, {1})");
    operatorTemplates.put(VectorOps.DIMENSION_COUNT, "vectorproperty({0}, 'Dimensions')");
    operatorTemplates.put(VectorOps.L2_NORM, "vector_norm({0}, 'norm2')");
    return operatorTemplates;
  }

  /**
   * MariaDB only has Euclidean and cosine distance, so the inner product, Manhattan distance and
   * norm operators are missing
   */
  public static Map<Operator, String> createMariaDBOperatorTemplates() {
    Map<Operator, String> operatorTemplates = new HashMap<>();
    operatorTemplates.put(VectorOps.L2_DISTANCE, "vec_distance_euclidean({0}, {1})");
    operatorTemplates.put(
        VectorOps.L2_SQUARED_DISTANCE, "power(vec_distance_euclidean({0}, {1}), 2)");
    operatorTemplates.put(VectorOps.COSINE_DISTANCE, "vec_distance_cosine({0}, {1})");
    operatorTemplates.put(VectorOps.DIMENSION_COUNT, "(length({0}) div 4)");
    return operatorTemplates;
  }
}
