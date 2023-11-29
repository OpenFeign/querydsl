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
package com.querydsl.core.group;

/**
 * A stateful collector of column values for a group.
 *
 * @param <T> Element type
 * @param <R> Target type (e.g. List, Set)
 * @author sasa
 */
public interface GroupCollector<T, R> {

  /**
   * Add given value to this group
   *
   * @param o value to add
   */
  void add(T o);

  /**
   * Get the value for this group
   *
   * @return value of this group.
   */
  R get();
}
