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

import com.querydsl.core.types.dsl.TimeExpression;
import java.sql.Time;
import java.util.Calendar;
import org.junit.Test;

public class TimeConstantTest {

  @Test
  public void test() {
    var cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 13);
    cal.set(Calendar.MINUTE, 30);
    cal.set(Calendar.SECOND, 12);
    cal.set(Calendar.MILLISECOND, 3);

    TimeExpression<Time> time = TimeConstant.create(new Time(cal.getTimeInMillis()));
    assertThat(time.hour()).hasToString("13");
    assertThat(time.minute()).hasToString("30");
    assertThat(time.second()).hasToString("12.0");
    //        assertEquals("3",    time.getMilliSecond().toString());
  }
}
