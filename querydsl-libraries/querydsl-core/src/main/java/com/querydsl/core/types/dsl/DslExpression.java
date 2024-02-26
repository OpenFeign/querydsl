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
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;

/**
 * {@code DslExpression} is the base class for DSL expressions, but {@link SimpleExpression} is the
 * base class for scalar Expressions
 *
 * @author tiwe
 * @param <T> expression type
 */
public abstract class DslExpression<T> implements Expression<T> {

  private static final long serialVersionUID = -3383063447710753290L;

  protected final Expression<T> mixin;

  protected final int hashCode;

  public DslExpression(Expression<T> mixin) {
    this.mixin = mixin;
    this.hashCode = mixin.hashCode();
  }

  @Override
  public final Class<? extends T> getType() {
    return mixin.getType();
  }

  /**
   * Create an alias for the expression
   *
   * @return this as alias
   */
  @SuppressWarnings("unchecked")
  public DslExpression<T> as(Path<T> alias) {
    return Expressions.dslOperation(getType(), Ops.ALIAS, mixin, alias);
  }

  /**
   * Create an alias for the expression
   *
   * @return this as alias
   */
  public DslExpression<T> as(String alias) {
    return as(ExpressionUtils.path(getType(), alias));
  }

  @Override
  public boolean equals(Object o) { // can be overwritten
    return mixin.equals(o);
  }

  @Override
  public final int hashCode() {
    return hashCode;
  }

  @Override
  public final String toString() {
    return mixin.toString();
  }
}
