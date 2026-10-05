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
import com.querydsl.core.types.Visitor;
import org.jetbrains.annotations.Nullable;

/** Factory methods for vector expressions */
public final class VectorExpressions {

  private VectorExpressions() {}

  /**
   * A vector constant, like a query embedding. In JPA, the other side must be a vector property,
   * because Hibernate cannot guess the type of a standalone array parameter.
   */
  public static VectorExpression createConstantVector(float[] vectorValue) {
    return new ConstantVector(ConstantImpl.create(vectorValue));
  }

  private static final class ConstantVector extends VectorExpression {

    private static final long serialVersionUID = 2614723907150983620L;

    ConstantVector(Expression<float[]> constant) {
      super(constant);
    }

    @Override
    @Nullable
    public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
      return mixin.accept(v, context);
    }
  }
}
