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
import com.querydsl.core.types.ExpressionUtils;
import java.io.Serializable;
import java.util.Objects;

/**
 * Defines a positioned flag in a Query for customization of query serialization
 *
 * @author tiwe
 */
public class QueryFlag implements Serializable {

  private static final long serialVersionUID = -7131081607441961628L;

  /** The different {@code QueryFlag} positions */
  public enum Position {

    /** WITH part (used in SQL) */
    WITH,

    /** Start of the query */
    START,

    /** Override for the first element (e.g SELECT, INSERT) */
    START_OVERRIDE,

    /** After the first element (after select) */
    AFTER_SELECT,

    /** After the projection (after select ...) */
    AFTER_PROJECTION,

    /** Before the filter conditions (where) */
    BEFORE_FILTERS,

    /** After the filter conditions (where) */
    AFTER_FILTERS,

    /** Before group by */
    BEFORE_GROUP_BY,

    /** After group by */
    AFTER_GROUP_BY,

    /** Before having */
    BEFORE_HAVING,

    /** After having */
    AFTER_HAVING,

    /** Before order (by) */
    BEFORE_ORDER,

    /** After order (by) */
    AFTER_ORDER,

    /** After all other tokens */
    END
  }

  private final Position position;

  private final Expression<?> flag;

  public QueryFlag(Position position, String flag) {
    this(position, ExpressionUtils.template(Object.class, flag));
  }

  public QueryFlag(Position position, Expression<?> flag) {
    this.position = position;
    this.flag = flag;
  }

  public Position getPosition() {
    return position;
  }

  public Expression<?> getFlag() {
    return flag;
  }

  @Override
  public int hashCode() {
    return Objects.hash(position, flag);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof QueryFlag) {
      QueryFlag other = (QueryFlag) obj;
      return other.position.equals(position) && other.flag.equals(flag);
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return position + " : " + flag;
  }
}
