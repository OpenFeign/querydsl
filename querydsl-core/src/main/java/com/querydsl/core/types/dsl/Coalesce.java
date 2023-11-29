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

import com.querydsl.core.types.*;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code Coalesce} defines a coalesce function invocation. The coalesce function returns null if
 * all arguments are null and the first non-null argument otherwise
 *
 * <p>Coalesce doesn't provide the full interface for comparable expressions. To get an immutable
 * copy with the full expressiveness of Comparable expressions, call getValue().
 *
 * @author tiwe
 * @param <T> expression type
 */
@SuppressWarnings("unchecked")
public class Coalesce<T extends Comparable> extends MutableExpressionBase<T> {

  private static final long serialVersionUID = 445439522266250417L;

  private final List<Expression<? extends T>> exprs = new ArrayList<Expression<? extends T>>();

  private transient volatile ComparableExpression<T> value;

  public Coalesce(Class<? extends T> type, Expression<T> expr) {
    super(type);
    add(expr);
  }

  public Coalesce(Class<? extends T> type, Expression<?>... exprs) {
    super(type);
    // NOTE : type parameters for the varargs, would result in compiler warnings
    for (Expression expr : exprs) {
      add(expr);
    }
  }

  public Coalesce(Expression... exprs) {
    // NOTE : type parameters for the varargs, would result in compiler warnings
    this((Class) (exprs.length > 0 ? exprs[0].getType() : Object.class), exprs);
  }

  @Override
  public <R, C> R accept(Visitor<R, C> v, C context) {
    return getValue().accept(v, context);
  }

  public ComparableExpression<T> getValue() {
    if (value == null) {
      value = Expressions.comparableOperation(getType(), Ops.COALESCE, getExpressionList());
    }
    return value;
  }

  /**
   * Create an alias for the expression
   *
   * @return this as alias
   */
  public DslExpression<T> as(Path<T> alias) {
    return Expressions.dslOperation(getType(), Ops.ALIAS, this, alias);
  }

  /**
   * Create an alias for the expression
   *
   * @return this as alias
   */
  public DslExpression<T> as(String alias) {
    return as(ExpressionUtils.path(getType(), alias));
  }

  public final Coalesce<T> add(Expression<T> expr) {
    value = null;
    this.exprs.add(expr);
    return this;
  }

  public OrderSpecifier<T> asc() {
    return getValue().asc();
  }

  public OrderSpecifier<T> desc() {
    return getValue().desc();
  }

  public final Coalesce<T> add(T constant) {
    return add(ConstantImpl.create(constant));
  }

  public BooleanExpression asBoolean() {
    return Expressions.booleanOperation(Ops.COALESCE, getExpressionList());
  }

  public DateExpression<T> asDate() {
    return Expressions.dateOperation(getType(), Ops.COALESCE, getExpressionList());
  }

  public DateTimeExpression<T> asDateTime() {
    return Expressions.dateTimeOperation(getType(), Ops.COALESCE, getExpressionList());
  }

  public EnumExpression<?> asEnum() {
    return Expressions.enumOperation((Class) getType(), Ops.COALESCE, getExpressionList());
  }

  public NumberExpression<?> asNumber() {
    return Expressions.numberOperation((Class) getType(), Ops.COALESCE, getExpressionList());
  }

  public StringExpression asString() {
    return Expressions.stringOperation(Ops.COALESCE, getExpressionList());
  }

  public TimeExpression<T> asTime() {
    return Expressions.timeOperation(getType(), Ops.COALESCE, getExpressionList());
  }

  private Expression<?> getExpressionList() {
    return ExpressionUtils.list(getType(), exprs);
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof Coalesce<?>) {
      Coalesce<?> c = (Coalesce<?>) o;
      return c.exprs.equals(exprs);
    } else {
      return false;
    }
  }
}
