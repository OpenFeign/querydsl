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
import com.querydsl.core.support.EnumConversion;
import com.querydsl.core.support.NumberConversion;
import com.querydsl.core.support.NumberConversions;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLOps;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code Conversions} provides module specific projection conversion functionality
 *
 * @author tiwe
 */
public final class Conversions {

  public static <RT> Expression<RT> convert(Expression<RT> expr) {
    if (expr instanceof FactoryExpression) {
      FactoryExpression<RT> factoryExpr = (FactoryExpression<RT>) expr;
      for (Expression<?> e : factoryExpr.getArgs()) {
        if (needsConstantRemoval(e)) {
          return convert(new ConstantHidingExpression<RT>(factoryExpr));
        }
      }
      for (Expression<?> e : factoryExpr.getArgs()) {
        if (needsNumberConversion(e)) {
          return new NumberConversions<RT>(factoryExpr);
        }
      }
    } else if (needsNumberConversion(expr)) {
      return new NumberConversion<RT>(expr);
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

  private static boolean isEntityPathAndNeedsWrapping(Expression<?> expr) {
    if ((expr instanceof Path && expr.getType().isAnnotationPresent(Entity.class))
        || (expr instanceof EntityPath && !RelationalPath.class.isInstance(expr))) {
      Path<?> path = (Path<?>) expr;
      if (path.getMetadata().getParent() == null) {
        return true;
      }
    }
    return false;
  }

  private static <RT> FactoryExpression<RT> createEntityPathConversions(
      FactoryExpression<RT> factoryExpr) {
    List<Expression<?>> conversions = new ArrayList<>();
    for (Expression<?> e : factoryExpr.getArgs()) {
      if (isEntityPathAndNeedsWrapping(e)) {
        conversions.add(ExpressionUtils.operation(e.getType(), SQLOps.ALL, e));
      } else {
        conversions.add(e);
      }
    }
    return FactoryExpressionUtils.wrap(factoryExpr, conversions);
  }

  public static <RT> Expression<RT> convertForNativeQuery(Expression<RT> expr) {
    if (isEntityPathAndNeedsWrapping(expr)) {
      return ExpressionUtils.operation(expr.getType(), SQLOps.ALL, expr);
    } else if (Number.class.isAssignableFrom(expr.getType())) {
      return new NumberConversion<RT>(expr);
    } else if (Enum.class.isAssignableFrom(expr.getType())) {
      return new EnumConversion<RT>(expr);
    } else if (expr instanceof FactoryExpression) {
      FactoryExpression<RT> factoryExpr = (FactoryExpression<RT>) expr;
      boolean numberConversions = false;
      boolean hasEntityPath = false;
      for (Expression<?> e : factoryExpr.getArgs()) {
        if (isEntityPathAndNeedsWrapping(e)) {
          hasEntityPath = true;
        } else if (Number.class.isAssignableFrom(e.getType())) {
          numberConversions = true;
        } else if (Enum.class.isAssignableFrom(e.getType())) {
          numberConversions = true;
        }
      }
      if (hasEntityPath) {
        factoryExpr = createEntityPathConversions(factoryExpr);
      }
      if (numberConversions) {
        factoryExpr = new NumberConversions<RT>(factoryExpr);
      }
      return factoryExpr;
    }
    return expr;
  }

  private Conversions() {}
}
