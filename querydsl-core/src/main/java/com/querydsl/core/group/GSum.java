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
package com.querydsl.core.group;

import com.querydsl.core.types.Expression;
import com.querydsl.core.util.MathUtils;
import java.math.BigDecimal;

class GSum<T extends Number> extends AbstractGroupExpression<T, T> {

  private static final long serialVersionUID = 3518868612387641383L;

  @SuppressWarnings("unchecked")
  GSum(Expression<T> expr) {
    super((Class) expr.getType(), expr);
  }

  @Override
  public GroupCollector<T, T> createGroupCollector() {
    return new GroupCollector<T, T>() {
      private BigDecimal sum = BigDecimal.ZERO;

      @Override
      public void add(T t) {
        if (t != null) {
          sum = sum.add(new BigDecimal(t.toString()));
        }
      }

      @Override
      public T get() {
        return MathUtils.cast(sum, getType());
      }
    };
  }
}
