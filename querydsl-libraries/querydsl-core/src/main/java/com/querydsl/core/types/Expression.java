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

import java.io.Serializable;
import org.jetbrains.annotations.Nullable;

/**
 * {@code Expression} defines a general typed expression in a Query instance. The generic type
 * parameter is a reference to the type the expression is bound to.
 *
 * <p>The central Expression subinterfaces are
 *
 * <ul>
 *   <li>{@link Constant} - for constants such as Strings, numbers and entity instances
 *   <li>{@link FactoryExpression} - for row based result processing
 *   <li>{@link Operation} - for common supported operations and function calls
 *   <li>{@link ParamExpression} - for bindable query parameters
 *   <li>{@link Path} - for variables, properties and collection member access
 *   <li>{@link SubQueryExpression} - for subqueries
 *   <li>{@link TemplateExpression} - for custom syntax
 * </ul>
 *
 * @author tiwe
 * @param <T> expression type
 */
public interface Expression<T> extends Serializable {

  /**
   * Accept the visitor with the given context
   *
   * @param <R> return type
   * @param <C> context type
   * @param v visitor
   * @param context context of visit
   * @return result of visit
   */
  @Nullable
  <R, C> R accept(Visitor<R, C> v, @Nullable C context);

  /**
   * Get the java type for this expression
   *
   * @return type of expression
   */
  Class<? extends T> getType();
}
