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
package com.querydsl.sql;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * {@code WindowFirstLast} is a builder for window function expressions
 *
 * @author tiwe
 * @param <T>
 */
public class WindowFirstLast<T> extends MutableExpressionBase<T> {

  private static final long serialVersionUID = 4107262569593794721L;

  private static final String ORDER_BY = "order by ";

  private final List<OrderSpecifier<?>> orderBy = new ArrayList<OrderSpecifier<?>>();

  @Nullable private transient volatile SimpleExpression<T> value;

  private final Expression<T> target;

  private final boolean first;

  public WindowFirstLast(WindowOver<T> target, boolean first) {
    super(target.getType());
    this.target = target;
    this.first = first;
  }

  @Override
  public <R, C> R accept(Visitor<R, C> v, C context) {
    return getValue().accept(v, context);
  }

  public WindowFirstLast<T> orderBy(ComparableExpressionBase<?> orderBy) {
    value = null;
    this.orderBy.add(orderBy.asc());
    return this;
  }

  public WindowFirstLast<T> orderBy(ComparableExpressionBase<?>... orderBy) {
    value = null;
    for (ComparableExpressionBase<?> e : orderBy) {
      this.orderBy.add(e.asc());
    }
    return this;
  }

  public WindowFirstLast<T> orderBy(OrderSpecifier<?> orderBy) {
    value = null;
    this.orderBy.add(orderBy);
    return this;
  }

  public WindowFirstLast<T> orderBy(OrderSpecifier<?>... orderBy) {
    value = null;
    Collections.addAll(this.orderBy, orderBy);
    return this;
  }

  SimpleExpression<T> getValue() {
    if (value == null) {
      if (orderBy.isEmpty()) {
        // TODO this check should be static
        throw new IllegalStateException("No order by arguments given");
      }
      List<Expression<?>> args = new ArrayList<>();
      StringBuilder builder = new StringBuilder();
      builder.append("{0} keep (dense_rank ");
      args.add(target);
      builder.append(first ? "first " : "last ");
      builder.append(ORDER_BY);
      builder.append("{1}");
      args.add(ExpressionUtils.orderBy(orderBy));
      builder.append(")");
      value =
          Expressions.template(
              target.getType(), builder.toString(), CollectionUtils.unmodifiableList(args));
    }
    return value;
  }

  public WindowFunction<T> over() {
    return new WindowFunction<T>(getValue());
  }
}
