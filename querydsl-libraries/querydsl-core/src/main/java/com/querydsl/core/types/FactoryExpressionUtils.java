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
package com.querydsl.core.types;

import com.querydsl.core.util.ArrayUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to expand {@link FactoryExpression} constructor arguments and compress {@link
 * FactoryExpression} invocation arguments
 *
 * @author tiwe
 */
public final class FactoryExpressionUtils {

  /**
   * {@code FactoryExpressionAdapter} provides an adapter implementation of the {@link
   * FactoryExpression} interface
   *
   * @param <T>
   */
  public static class FactoryExpressionAdapter<T> extends ExpressionBase<T>
      implements FactoryExpression<T> {

    private static final long serialVersionUID = -2742333128230913512L;

    private final FactoryExpression<T> inner;

    private final List<Expression<?>> args;

    FactoryExpressionAdapter(FactoryExpression<T> inner) {
      super(inner.getType());
      this.inner = inner;
      this.args = expand(inner.getArgs());
    }

    FactoryExpressionAdapter(FactoryExpression<T> inner, List<Expression<?>> args) {
      super(inner.getType());
      this.inner = inner;
      this.args = expand(args);
    }

    @Override
    public List<Expression<?>> getArgs() {
      return args;
    }

    @Override
    public T newInstance(Object... a) {
      return inner.newInstance(compress(inner.getArgs(), a));
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
      return v.visit(this, context);
    }

    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      } else if (o instanceof FactoryExpression<?> e) {
        return args.equals(e.getArgs()) && getType().equals(e.getType());
      } else {
        return false;
      }
    }
  }

  public static FactoryExpression<?> wrap(List<? extends Expression<?>> projection) {
    var usesFactoryExpressions = false;
    for (Expression<?> e : projection) {
      usesFactoryExpressions |= e instanceof FactoryExpression;
    }
    if (usesFactoryExpressions) {
      return wrap(new ArrayConstructorExpression<>(projection.toArray(new Expression<?>[0])));
    } else {
      return null;
    }
  }

  public static <T> FactoryExpression<T> wrap(
      FactoryExpression<T> expr, List<Expression<?>> conversions) {
    return new FactoryExpressionAdapter<>(expr, conversions);
  }

  public static <T> FactoryExpression<T> wrap(FactoryExpression<T> expr) {
    for (Expression<?> arg : expr.getArgs()) {
      if (arg instanceof ProjectionRole<?> role) {
        arg = role.getProjection();
      }
      if (arg instanceof FactoryExpression<?>) {
        return new FactoryExpressionAdapter<>(expr);
      }
    }
    return expr;
  }

  private static List<Expression<?>> expand(List<Expression<?>> exprs) {
    List<Expression<?>> rv = new ArrayList<>(exprs.size());
    for (Expression<?> expr : exprs) {
      if (expr instanceof ProjectionRole<?> role) {
        expr = role.getProjection();
      }
      if (expr instanceof FactoryExpression<?> expression) {
        rv.addAll(expand(expression.getArgs()));
      } else {
        rv.add(expr);
      }
    }
    return rv;
  }

  private static int countArguments(FactoryExpression<?> expr) {
    var counter = 0;
    for (Expression<?> arg : expr.getArgs()) {
      if (arg instanceof ProjectionRole<?> role) {
        arg = role.getProjection();
      }
      if (arg instanceof FactoryExpression<?> expression) {
        counter += countArguments(expression);
      } else {
        counter++;
      }
    }
    return counter;
  }

  private static Object[] compress(List<Expression<?>> exprs, Object[] args) {
    var rv = new Object[exprs.size()];
    var offset = 0;
    for (var i = 0; i < exprs.size(); i++) {
      Expression<?> expr = exprs.get(i);
      if (expr instanceof ProjectionRole<?> role) {
        expr = role.getProjection();
      }
      if (expr instanceof FactoryExpression<?> fe) {
        var fullArgsLength = countArguments(fe);
        var compressed =
            compress(fe.getArgs(), ArrayUtils.subarray(args, offset, offset + fullArgsLength));
        rv[i] = fe.newInstance(compressed);
        offset += fullArgsLength;
      } else {
        rv[i] = args[offset];
        offset++;
      }
    }
    return rv;
  }

  private FactoryExpressionUtils() {}
}
