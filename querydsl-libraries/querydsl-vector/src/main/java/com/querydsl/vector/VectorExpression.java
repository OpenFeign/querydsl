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
import org.jetbrains.annotations.Nullable;

/**
 * {@code VectorExpression} represents an embedding vector column or value
 *
 * @param <T> Java representation of the vector, typically {@code float[]}
 */
public abstract class VectorExpression<T> extends SimpleExpression<T> {

  private static final long serialVersionUID = 4187062417529561203L;

  @Nullable private transient volatile NumberExpression<Integer> dims;

  @Nullable private transient volatile NumberExpression<Double> norm;

  public VectorExpression(Expression<T> mixin) {
    super(mixin);
  }

  /** Euclidean distance */
  public NumberExpression<Double> l2Distance(Expression<T> other) {
    return distance(VectorOps.L2_DISTANCE, other);
  }

  public NumberExpression<Double> l2Distance(T other) {
    return l2Distance(ConstantImpl.create(other));
  }

  /** Squared Euclidean distance, cheaper than {@link #l2Distance} and ranks the same */
  public NumberExpression<Double> l2SquaredDistance(Expression<T> other) {
    return distance(VectorOps.L2_SQUARED_DISTANCE, other);
  }

  public NumberExpression<Double> l2SquaredDistance(T other) {
    return l2SquaredDistance(ConstantImpl.create(other));
  }

  /** Cosine distance, {@code 1 - cosine similarity} */
  public NumberExpression<Double> cosineDistance(Expression<T> other) {
    return distance(VectorOps.COSINE_DISTANCE, other);
  }

  public NumberExpression<Double> cosineDistance(T other) {
    return cosineDistance(ConstantImpl.create(other));
  }

  /** Dot product */
  public NumberExpression<Double> innerProduct(Expression<T> other) {
    return distance(VectorOps.INNER_PRODUCT, other);
  }

  public NumberExpression<Double> innerProduct(T other) {
    return innerProduct(ConstantImpl.create(other));
  }

  /** Negated dot product, ascending order ranks the most similar vectors first */
  public NumberExpression<Double> negativeInnerProduct(Expression<T> other) {
    return distance(VectorOps.NEGATIVE_INNER_PRODUCT, other);
  }

  public NumberExpression<Double> negativeInnerProduct(T other) {
    return negativeInnerProduct(ConstantImpl.create(other));
  }

  /** Manhattan (taxicab) distance */
  public NumberExpression<Double> l1Distance(Expression<T> other) {
    return distance(VectorOps.L1_DISTANCE, other);
  }

  public NumberExpression<Double> l1Distance(T other) {
    return l1Distance(ConstantImpl.create(other));
  }

  public NumberExpression<Integer> dims() {
    if (dims == null) {
      dims = Expressions.numberOperation(Integer.class, VectorOps.DIMS, mixin);
    }
    return dims;
  }

  /** Euclidean norm */
  public NumberExpression<Double> norm() {
    if (norm == null) {
      norm = Expressions.numberOperation(Double.class, VectorOps.NORM, mixin);
    }
    return norm;
  }

  private NumberExpression<Double> distance(VectorOps op, Expression<T> other) {
    return Expressions.numberOperation(Double.class, op, mixin, other);
  }
}
