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
package com.querydsl.sql;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code WindowRows} provides the building of the rows/range part of the window function expression
 *
 * @param <A> expression type
 * @author tiwe
 */
public class WindowRows<A> {

  private static final String AND = " and";

  private static final String BETWEEN = " between";

  private static final String CURRENT_ROW = " current row";

  private static final String FOLLOWING = " following";

  private static final String PRECEDING = " preceding";

  private static final String UNBOUNDED = " unbounded";

  /** Intermediate step */
  public class Between {

    public BetweenAnd unboundedPreceding() {
      str.append(UNBOUNDED);
      str.append(PRECEDING);
      return and();
    }

    public BetweenAnd currentRow() {
      str.append(CURRENT_ROW);
      return and();
    }

    public BetweenAnd preceding(Expression<Integer> expr) {
      appendOffset(expr, PRECEDING);
      return and();
    }

    public BetweenAnd preceding(int i) {
      return preceding(ConstantImpl.create(i));
    }

    public BetweenAnd following(Expression<Integer> expr) {
      appendOffset(expr, FOLLOWING);
      return and();
    }

    public BetweenAnd following(int i) {
      return following(ConstantImpl.create(i));
    }
  }

  /** Intermediate step */
  public class BetweenAnd {

    public WindowFunction<A> unboundedFollowing() {
      str.append(UNBOUNDED);
      str.append(FOLLOWING);
      return build();
    }

    public WindowFunction<A> currentRow() {
      str.append(CURRENT_ROW);
      return build();
    }

    public WindowFunction<A> preceding(Expression<Integer> expr) {
      appendOffset(expr, PRECEDING);
      return build();
    }

    public WindowFunction<A> preceding(int i) {
      return preceding(ConstantImpl.create(i));
    }

    public WindowFunction<A> following(Expression<Integer> expr) {
      appendOffset(expr, FOLLOWING);
      return build();
    }

    public WindowFunction<A> following(int i) {
      return following(ConstantImpl.create(i));
    }
  }

  private final WindowFunction<A> rv;

  private final StringBuilder str = new StringBuilder();

  private final List<Expression<?>> args = new ArrayList<>();

  private int offset;

  public WindowRows(WindowFunction<A> windowFunction, String prefix, int offset) {
    this.rv = windowFunction;
    this.offset = offset;
    str.append(prefix);
  }

  public Between between() {
    str.append(BETWEEN);
    return new Between();
  }

  public WindowFunction<A> unboundedPreceding() {
    str.append(UNBOUNDED);
    str.append(PRECEDING);
    return build();
  }

  public WindowFunction<A> currentRow() {
    str.append(CURRENT_ROW);
    return build();
  }

  public WindowFunction<A> preceding(Expression<Integer> expr) {
    appendOffset(expr, PRECEDING);
    return build();
  }

  public WindowFunction<A> preceding(int i) {
    return preceding(ConstantImpl.create(i));
  }

  private void appendOffset(Expression<Integer> expr, String keyword) {
    args.add(expr);
    str.append(" {").append(offset++).append("}").append(keyword);
  }

  private BetweenAnd and() {
    str.append(AND);
    return new BetweenAnd();
  }

  private WindowFunction<A> build() {
    return rv.withRowsOrRange(str.toString(), args);
  }
}
