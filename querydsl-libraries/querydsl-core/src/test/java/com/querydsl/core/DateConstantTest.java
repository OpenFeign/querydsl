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

import com.querydsl.core.types.dsl.DateExpression;
import java.sql.Date;
import java.util.Calendar;
import org.junit.jupiter.api.Test;

class DateConstantTest {

  @Test
  void test() {
    // 1.1.2000
    var cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.MONTH, 0);
    cal.set(Calendar.YEAR, 2000);

    DateExpression<Date> date = DateConstant.create(new Date(cal.getTimeInMillis()));
    assertThat(date.dayOfMonth()).hasToString("1");
    assertThat(date.month()).hasToString("1");
    assertThat(date.year()).hasToString("2000");
    assertThat(date.dayOfWeek()).hasToString("7");
    assertThat(date.dayOfYear()).hasToString("1");
  }
}
