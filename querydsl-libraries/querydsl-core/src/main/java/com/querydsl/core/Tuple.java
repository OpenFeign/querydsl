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
package com.querydsl.core;

import com.querydsl.core.types.Expression;
import org.jetbrains.annotations.Nullable;

/**
 * {@code Tuple} defines an interface for generic query result projection
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * List<Tuple> result = query.from(employee).select(employee.firstName, employee.lastName).fetch();
 * for (Tuple row : result) {
 *     System.out.println("firstName " + row.get(employee.firstName));
 *     System.out.println("lastName " + row.get(employee.lastName));
 * }
 * }</pre>
 *
 * @author tiwe
 */
public interface Tuple {

  /**
   * Get a Tuple element by index
   *
   * @param <T> type of element
   * @param index zero based index
   * @param type type of element
   * @return element in array
   */
  @Nullable
  <T> T get(int index, Class<T> type);

  /**
   * Get a tuple element by expression
   *
   * @param <T> type of element
   * @param expr expression key
   * @return result element that matches the expression
   */
  @Nullable
  <T> T get(Expression<T> expr);

  /**
   * Get the size of the Tuple
   *
   * @return row element count
   */
  int size();

  /**
   * Get the content as an Object array
   *
   * @return tuple in array form
   */
  Object[] toArray();

  /**
   * All Tuples should override equals and hashCode. For compatibility across different Tuple
   * implementations, equality check should use {@code java.util.Arrays#equals(Object[], Object[])}
   * with {@code #toArray()} as parameters.
   */
  boolean equals(Object o);

  /**
   * All Tuples should override equals and hashCode. For compatibility across different Tuple
   * implementations, hashCode should use {@code java.util.Arrays#hashCode(Object[])} with {@code
   * #toArray()} as parameter.
   */
  int hashCode();
}
