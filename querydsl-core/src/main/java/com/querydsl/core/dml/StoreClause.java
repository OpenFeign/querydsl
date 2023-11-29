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
package com.querydsl.core.dml;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import org.jetbrains.annotations.Nullable;

/**
 * Parent interface for {@link InsertClause} and {@link UpdateClause}
 *
 * @author tiwe
 * @param <C> concrete subtype
 */
public interface StoreClause<C extends StoreClause<C>> extends DMLClause<C> {

  /**
   * Add a value binding
   *
   * @param <T>
   * @param path path to be updated
   * @param value value to set
   * @return the current object
   */
  <T> C set(Path<T> path, @Nullable T value);

  /**
   * Add an expression binding
   *
   * @param <T>
   * @param path path to be updated
   * @param expression binding
   * @return the current object
   */
  <T> C set(Path<T> path, Expression<? extends T> expression);

  /**
   * Bind the given path to null
   *
   * @param path path to be updated
   * @return the current object
   */
  <T> C setNull(Path<T> path);

  /**
   * Returns true, if no bindings have been set, otherwise false.
   *
   * @return true, if empty, false, if not
   */
  boolean isEmpty();
}
