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

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;

/** {@code VectorExpression} is a column or a value that holds an embedding vector. */
public abstract class VectorExpression extends SimpleExpression<float[]> {

  private static final long serialVersionUID = 4187062417529561203L;

  public VectorExpression(Expression<float[]> mixin) {
    super(mixin);
  }

  public NumberExpression<Double> l2Distance(Expression<float[]> other) {
    return createVectorOperation(VectorOps.L2_DISTANCE, other);
  }

  public NumberExpression<Double> l2Distance(float[] other) {
    return l2Distance(ConstantImpl.create(other));
  }

  /**
   * Squared Euclidean distance. It is cheaper than {@link #l2Distance} but gives the same order of
   * results.
   */
  public NumberExpression<Double> l2SquaredDistance(Expression<float[]> other) {
    return createVectorOperation(VectorOps.L2_SQUARED_DISTANCE, other);
  }

  public NumberExpression<Double> l2SquaredDistance(float[] other) {
    return l2SquaredDistance(ConstantImpl.create(other));
  }

  /** Cosine distance, {@code 1 - cosine similarity} */
  public NumberExpression<Double> cosineDistance(Expression<float[]> other) {
    return createVectorOperation(VectorOps.COSINE_DISTANCE, other);
  }

  public NumberExpression<Double> cosineDistance(float[] other) {
    return cosineDistance(ConstantImpl.create(other));
  }

  public NumberExpression<Double> innerProduct(Expression<float[]> other) {
    return createVectorOperation(VectorOps.INNER_PRODUCT, other);
  }

  public NumberExpression<Double> innerProduct(float[] other) {
    return innerProduct(ConstantImpl.create(other));
  }

  /**
   * Negative dot product. Sorting from lowest to highest puts the most similar vectors at the top.
   */
  public NumberExpression<Double> negativeInnerProduct(Expression<float[]> other) {
    return createVectorOperation(VectorOps.NEGATIVE_INNER_PRODUCT, other);
  }

  public NumberExpression<Double> negativeInnerProduct(float[] other) {
    return negativeInnerProduct(ConstantImpl.create(other));
  }

  /** Manhattan (taxicab) distance */
  public NumberExpression<Double> l1Distance(Expression<float[]> other) {
    return createVectorOperation(VectorOps.L1_DISTANCE, other);
  }

  public NumberExpression<Double> l1Distance(float[] other) {
    return l1Distance(ConstantImpl.create(other));
  }

  public NumberExpression<Integer> dimensionCount() {
    return Expressions.numberOperation(Integer.class, VectorOps.DIMENSION_COUNT, mixin);
  }

  public NumberExpression<Double> l2Norm() {
    return Expressions.numberOperation(Double.class, VectorOps.L2_NORM, mixin);
  }

  private NumberExpression<Double> createVectorOperation(
      VectorOps vectorOperator, Expression<float[]> other) {
    return Expressions.numberOperation(Double.class, vectorOperator, mixin, other);
  }
}
