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

import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.TimeExpression;
import java.util.Calendar;

/**
 * @author tiwe
 */
public final class TimeConstant<D extends java.util.Date> extends TimeExpression<D>
    implements Constant<D> {

  private static final long serialVersionUID = -7835941761930555480L;

  public static <D extends java.util.Date> TimeExpression<D> create(D time) {
    return new TimeConstant<D>(time);
  }

  private final Calendar calendar;

  private final D time;

  @SuppressWarnings("unchecked")
  public TimeConstant(D time) {
    super(ConstantImpl.create(time));
    this.calendar = Calendar.getInstance();
    this.time = (D) time.clone();
    calendar.setTime(time);
  }

  @Override
  public <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(this, context);
  }

  @Override
  public NumberExpression<Integer> hour() {
    return NumberConstant.create(calendar.get(Calendar.HOUR_OF_DAY));
  }

  @Override
  public NumberExpression<Integer> minute() {
    return NumberConstant.create(calendar.get(Calendar.MINUTE));
  }

  @Override
  public NumberExpression<Float> second() {
    return NumberConstant.create((float) calendar.get(Calendar.SECOND));
  }

  @Override
  public NumberExpression<Integer> milliSecond() {
    return NumberConstant.create(calendar.get(Calendar.MILLISECOND));
  }

  @Override
  public D getConstant() {
    return time;
  }
}
