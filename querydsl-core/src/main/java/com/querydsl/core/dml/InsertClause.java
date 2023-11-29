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

import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;

/**
 * {@code InsertClause} defines a generic interface for Insert clauses
 *
 * @author tiwe
 * @param <C> concrete subtype
 */
public interface InsertClause<C extends InsertClause<C>> extends StoreClause<C> {

  /**
   * Define the columns to be populated
   *
   * @param columns columns to be populated
   * @return the current object
   */
  C columns(Path<?>... columns);

  /**
   * Define the populate via subquery
   *
   * @param subQuery sub query to be used for population
   * @return the current object
   */
  C select(SubQueryExpression<?> subQuery);

  /**
   * Define the value bindings
   *
   * @param v values to be inserted
   * @return the current object
   */
  C values(Object... v);
}
