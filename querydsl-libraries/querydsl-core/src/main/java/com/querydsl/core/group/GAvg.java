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
import java.math.MathContext;

/**
 * GAvg
 *
 * @param <T> t
 */
@SuppressWarnings("unchecked")
public class GAvg<T extends Number> extends AbstractGroupExpression<T, T> {

  private static final long serialVersionUID = 3518868612387641383L;

  private final MathContext mathContext;

  public GAvg(Expression<T> expr) {
    this(expr, MathContext.DECIMAL128);
  }

  GAvg(Expression<T> expr, MathContext mathContext) {
    super((Class) expr.getType(), expr);
    this.mathContext = mathContext;
  }

  @Override
  public GroupCollector<T, T> createGroupCollector() {
    return new GroupCollector<T, T>() {
      private int count = 0;
      private BigDecimal sum = BigDecimal.ZERO;

      @Override
      public void add(T t) {
        count++;
        if (t != null) {
          sum = sum.add(new BigDecimal(t.toString()));
        }
      }

      @Override
      public T get() {
        BigDecimal avg = sum.divide(BigDecimal.valueOf(count), mathContext);
        return MathUtils.cast(avg, getType());
      }
    };
  }
}
