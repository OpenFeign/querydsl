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
package com.querydsl.r2dbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLOps;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.Test;

public class SQLTemplatesTest {

  private static final String DATETIME =
      "\\(timestamp '\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}'\\)";
  private static final String TIME = "\\(time '\\d{2}:\\d{2}:\\d{2}'\\)";
  private static final String DATE = "\\(date '\\d{4}-\\d{2}-\\d{2}'\\)";

  private static void assertMatches(String regex, String str) {
    assertTrue(str, str.matches(regex));
  }

  @Test
  public void test() {
    Template template = TemplateFactory.DEFAULT.create("fetch first {0s} rows only");
    assertTrue(template.getElements().get(1) instanceof Template.AsString);

    SQLSerializer serializer = new SQLSerializer(new Configuration(new H2Templates()));
    serializer.handle(Expressions.template(Object.class, template, ConstantImpl.create(5)));
    assertEquals("fetch first 5 rows only", serializer.toString());
  }

  @Test
  public void asLiteral() {
    SQLTemplates templates = SQLTemplates.DEFAULT;
    Configuration conf = new Configuration(templates);
    assertMatches(DATE, conf.asLiteral(new Date(0)));
    assertMatches(TIME, conf.asLiteral(new Time(0)));
    assertMatches(DATETIME, conf.asLiteral(new Timestamp(0)));
  }

  @Test
  public void asLiteral_jodaTime() {
    SQLTemplates templates = SQLTemplates.DEFAULT;
    Configuration conf = new Configuration(templates);
    assertMatches(DATE, conf.asLiteral(LocalDate.of(0, 1, 1)));
    assertMatches(TIME, conf.asLiteral(LocalTime.of(0, 1, 0)));
    assertMatches(DATETIME, conf.asLiteral(LocalDateTime.of(0, 1, 1, 0, 0, 0)));
  }

  @Test
  public void quote() {
    SQLTemplates templates = SQLTemplates.DEFAULT;
    // non quoted
    assertEquals("employee", templates.quoteIdentifier("employee"));
    assertEquals("Employee", templates.quoteIdentifier("Employee"));
    assertEquals("employee1", templates.quoteIdentifier("employee1"));
    assertEquals("employee_", templates.quoteIdentifier("employee_"));
    // quoted
    assertEquals("\"e e\"", templates.quoteIdentifier("e e"));
    assertEquals("\"1phoenix2\"", templates.quoteIdentifier("1phoenix2"));
  }

  @Test
  public void quoting_performance() {
    // 385 -> 63
    SQLTemplates templates = new H2Templates();
    long start = System.currentTimeMillis();
    int iterations = 1000000;
    for (int i = 0; i < iterations; i++) {
      templates.quoteIdentifier("companies");
    }
    System.err.println(System.currentTimeMillis() - start);
  }

  @Test
  public void nextVal() {
    Operation<String> nextval =
        ExpressionUtils.operation(String.class, SQLOps.NEXTVAL, ConstantImpl.create("myseq"));
    assertEquals(
        "nextval('myseq')",
        new SQLSerializer(new Configuration(SQLTemplates.DEFAULT)).handle(nextval).toString());
    // Derby OK
    // H2 OK
    // HSQLDB OK
    // MSSQL OK
    // MySQL
    // Oracle OK
    // PostgreSQL OK

  }

  @Test
  public void numeric_operations() {
    NumberPath<Integer> intPath = Expressions.numberPath(Integer.class, "intPath");
    NumberPath<Integer> intPath2 = Expressions.numberPath(Integer.class, "intPath2");
    SQLSerializer serializer = new SQLSerializer(new Configuration(SQLTemplates.DEFAULT));
    serializer.handle(intPath.subtract(intPath2.add(2)));
    assertEquals("intPath - (intPath2 + ?)", serializer.toString());
  }
}
