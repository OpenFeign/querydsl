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

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.MutableExpressionBase;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * {@code WindowFunction} is a builder for window function expressions
 *
 * @param <A> expression type
 * @author tiwe
 */
public class WindowFunction<A> extends MutableExpressionBase<A> {

  private static final String ORDER_BY = "order by ";

  private static final String PARTITION_BY = "partition by ";

  private static final long serialVersionUID = -4130672293308756779L;

  private final List<OrderSpecifier<?>> orderBy = new ArrayList<>();

  private final List<Expression<?>> partitionBy = new ArrayList<>();

  private final Expression<A> target;

  @Nullable private transient volatile SimpleExpression<A> value;

  private String rowsOrRange;

  private List<Expression<?>> rowsOrRangeArgs;

  public WindowFunction(Expression<A> expr) {
    super(expr.getType());
    this.target = expr;
  }

  public SimpleExpression<A> getValue() {
    if (value == null) {
      var size = 0;
      List<Expression<?>> args = new ArrayList<>();
      var builder = new StringBuilder();
      builder.append("{0} over (");
      args.add(target);
      size++;
      if (!partitionBy.isEmpty()) {
        builder.append(PARTITION_BY);
        var first = true;
        for (Expression<?> expr : partitionBy) {
          if (!first) {
            builder.append(", ");
          }
          builder.append("{").append(size).append("}");
          args.add(expr);
          size++;
          first = false;
        }
      }
      if (!orderBy.isEmpty()) {
        if (!partitionBy.isEmpty()) {
          builder.append(" ");
        }
        builder.append(ORDER_BY);
        builder.append("{").append(size).append("}");
        args.add(ExpressionUtils.orderBy(orderBy));
        size++;
      }
      if (rowsOrRange != null) {
        builder.append(rowsOrRange);
        args.addAll(rowsOrRangeArgs);
        size += rowsOrRangeArgs.size();
      }
      builder.append(")");
      value =
          Expressions.template(
              target.getType(), builder.toString(), Collections.unmodifiableList(args));
    }
    return value;
  }

  @SuppressWarnings("unchecked")
  public SimpleExpression<A> as(Expression<A> alias) {
    return Expressions.operation(getType(), Ops.ALIAS, this, alias);
  }

  public SimpleExpression<A> as(String alias) {
    return Expressions.operation(
        getType(), Ops.ALIAS, this, ExpressionUtils.path(getType(), alias));
  }

  @Override
  public <R, C> R accept(Visitor<R, C> v, C context) {
    return getValue().accept(v, context);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof WindowFunction<?> so) {
      return so.target.equals(target)
          && so.partitionBy.equals(partitionBy)
          && so.orderBy.equals(orderBy);
    } else {
      return false;
    }
  }

  public BooleanExpression eq(Expression<A> expr) {
    return getValue().eq(expr);
  }

  public BooleanExpression eq(A arg) {
    return getValue().eq(arg);
  }

  public BooleanExpression ne(Expression<A> expr) {
    return getValue().ne(expr);
  }

  public BooleanExpression ne(A arg) {
    return getValue().ne(arg);
  }

  public WindowFunction<A> orderBy(ComparableExpressionBase<?> orderBy) {
    value = null;
    this.orderBy.add(orderBy.asc());
    return this;
  }

  public WindowFunction<A> orderBy(ComparableExpressionBase<?>... orderBy) {
    value = null;
    for (ComparableExpressionBase<?> e : orderBy) {
      this.orderBy.add(e.asc());
    }
    return this;
  }

  public WindowFunction<A> orderBy(OrderSpecifier<?> orderBy) {
    value = null;
    this.orderBy.add(orderBy);
    return this;
  }

  public WindowFunction<A> orderBy(OrderSpecifier<?>... orderBy) {
    value = null;
    Collections.addAll(this.orderBy, orderBy);
    return this;
  }

  public WindowFunction<A> partitionBy(Expression<?> partitionBy) {
    value = null;
    this.partitionBy.add(partitionBy);
    return this;
  }

  public WindowFunction<A> partitionBy(Expression<?>... partitionBy) {
    value = null;
    Collections.addAll(this.partitionBy, partitionBy);
    return this;
  }

  WindowFunction<A> withRowsOrRange(String s, List<Expression<?>> args) {
    rowsOrRange = s;
    rowsOrRangeArgs = args;
    return this;
  }

  public WindowRows<A> rows() {
    value = null;
    var offset = orderBy.size() + partitionBy.size() + 1;
    return new WindowRows<>(this, " rows", offset);
  }

  public WindowRows<A> range() {
    value = null;
    var offset = orderBy.size() + partitionBy.size() + 1;
    return new WindowRows<>(this, " range", offset);
  }
}
