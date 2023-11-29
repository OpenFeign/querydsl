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
package com.querydsl.jpa;

import static com.querydsl.jpa.Constants.*;

import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.TimeExpression;
import java.util.Date;
import org.junit.Test;

public class DateTimeTest extends AbstractQueryTest {

  @Test
  public void currentDate() {
    assertToString("current_date", DateExpression.currentDate());
  }

  @Test
  public void currentDate2() {
    assertToString("current_date", DateTimeExpression.currentDate());
  }

  @Test
  public void currentTime() {
    assertToString("current_time", TimeExpression.currentTime());
  }

  @Test
  public void currentTimestamp() {
    assertToString("current_timestamp", DateTimeExpression.currentTimestamp());
  }

  @Test
  public void dayOfMonth() {
    assertToString("day(date)", Expressions.datePath(Date.class, "date").dayOfMonth());
  }

  @Test
  public void dayOfMonth2() {
    assertToString("day(date)", Expressions.dateTimePath(Date.class, "date").dayOfMonth());
  }

  @Test
  public void dateOperations2() {
    //        catalog.effectiveDate.second();
    //        catalog.effectiveDate.minute();
    //        catalog.effectiveDate.hour();
    catalog.effectiveDate.dayOfMonth();
    catalog.effectiveDate.month();
    catalog.effectiveDate.year();
  }
}
