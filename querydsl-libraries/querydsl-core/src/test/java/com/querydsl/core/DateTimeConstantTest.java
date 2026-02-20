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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.DateTimeExpression;
import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Test;

class DateTimeConstantTest {

  @Test
  void test() {
    var cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.YEAR, 2000);
    cal.set(Calendar.HOUR_OF_DAY, 13);
    cal.set(Calendar.MINUTE, 30);
    cal.set(Calendar.SECOND, 12);
    cal.set(Calendar.MILLISECOND, 3);

    DateTimeExpression<Date> date = DateTimeConstant.create(cal.getTime());
    assertThat(date.dayOfMonth()).hasToString("1");
    assertThat(date.month()).hasToString("1");
    assertThat(date.year()).hasToString("2000");
    assertThat(date.dayOfWeek()).hasToString("7");
    assertThat(date.dayOfYear()).hasToString("1");
    assertThat(date.hour()).hasToString("13");
    assertThat(date.minute()).hasToString("30");
    assertThat(date.second()).hasToString("12.0");
    assertThat(date.milliSecond()).hasToString("3");
  }
}
