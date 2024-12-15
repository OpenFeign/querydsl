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
package com.querydsl.jpa;

import com.querydsl.core.support.ConstantHidingExpression;
import com.querydsl.core.support.NumberConversion;
import com.querydsl.core.support.NumberConversions;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;

/**
 * {@code Conversions} provides module specific projection conversion functionality
 *
 * @author tiwe
 */
public final class Conversions {

  public static <RT> Expression<RT> convert(Expression<RT> expr) {
    if (expr instanceof FactoryExpression) {
      var factoryExpr = (FactoryExpression<RT>) expr;
      for (Expression<?> e : factoryExpr.getArgs()) {
        if (needsConstantRemoval(e)) {
          return convert(new ConstantHidingExpression<>(factoryExpr));
        }
      }
      for (Expression<?> e : factoryExpr.getArgs()) {
        if (needsNumberConversion(e)) {
          return new NumberConversions<>(factoryExpr);
        }
      }
    } else if (needsNumberConversion(expr)) {
      return new NumberConversion<>(expr);
    }
    return expr;
  }

  private static boolean needsConstantRemoval(Expression<?> expr) {
    expr = ExpressionUtils.extract(expr);
    return expr instanceof Constant
        || expr.equals(Expressions.TRUE)
        || expr.equals(Expressions.FALSE)
        || (expr instanceof Operation
            && ((Operation<?>) expr).getOperator() == Ops.ALIAS
            && needsConstantRemoval(((Operation<?>) expr).getArg(0)));
  }

  private static boolean needsNumberConversion(Expression<?> expr) {
    expr = ExpressionUtils.extract(expr);
    return Number.class.isAssignableFrom(expr.getType()) && !Path.class.isInstance(expr);
  }

  private Conversions() {}
}
