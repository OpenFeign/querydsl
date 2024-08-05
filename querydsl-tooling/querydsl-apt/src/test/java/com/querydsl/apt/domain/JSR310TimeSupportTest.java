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
package com.querydsl.apt.domain;

import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.TimePath;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import org.junit.Test;

public class JSR310TimeSupportTest extends AbstractTest {

  @QueryEntity
  public static class JSR310TimeSupport {

    ZonedDateTime dateMidnight;

    OffsetDateTime dateTime;

    Instant instant;

    LocalDate localDate;

    LocalDateTime localDateTime;

    LocalTime localTime;
  }

  @Test
  public void test() throws IllegalAccessException, NoSuchFieldException {
    start(
        QJSR310TimeSupportTest_JSR310TimeSupport.class,
        QJSR310TimeSupportTest_JSR310TimeSupport.jSR310TimeSupport);
    match(DateTimePath.class, "dateMidnight");
    matchType(ZonedDateTime.class, "dateMidnight");
    match(DateTimePath.class, "dateTime");
    matchType(OffsetDateTime.class, "dateTime");
    match(DateTimePath.class, "instant");
    matchType(Instant.class, "instant");
    match(DatePath.class, "localDate");
    matchType(LocalDate.class, "localDate");
    match(DateTimePath.class, "localDateTime");
    matchType(LocalDateTime.class, "localDateTime");
    match(TimePath.class, "localTime");
    matchType(LocalTime.class, "localTime");
  }
}
