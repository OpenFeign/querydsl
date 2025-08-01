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
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.OperationImpl;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Visitor;
import java.io.Serial;
import java.util.Arrays;
import java.util.List;

/**
 * {@code ComparableOperation} represents Comparable operations
 *
 * @author tiwe
 * @param <T> expression type
 */
public class ComparableOperation<T extends Comparable<?>> extends ComparableExpression<T>
    implements Operation<T> {

  @Serial private static final long serialVersionUID = 1129243977606098865L;

  private final OperationImpl<T> opMixin;

  protected ComparableOperation(OperationImpl<T> mixin) {
    super(mixin);
    this.opMixin = mixin;
  }

  protected ComparableOperation(Class<? extends T> type, Operator op, Expression<?>... args) {
    this(type, op, Arrays.asList(args));
  }

  protected ComparableOperation(Class<? extends T> type, Operator op, List<Expression<?>> args) {
    super(ExpressionUtils.operation(type, op, args));
    this.opMixin = (OperationImpl<T>) mixin;
  }

  @Override
  public final <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(opMixin, context);
  }

  @Override
  public Expression<?> getArg(int index) {
    return opMixin.getArg(index);
  }

  @Override
  public List<Expression<?>> getArgs() {
    return opMixin.getArgs();
  }

  @Override
  public Operator getOperator() {
    return opMixin.getOperator();
  }
}
