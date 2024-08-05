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
package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.Template;
import com.querydsl.core.util.BeanUtils;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExpressionsTest {

  private static final StringPath str = new StringPath("str");

  private static final BooleanExpression a = new BooleanPath("a"), b = new BooleanPath("b");

  private enum testEnum {
    TEST,
    TEST_2
  }

  private TimeZone timeZone = null;

  @Before
  public void setUp() {
    this.timeZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @After
  public void tearDown() {
    TimeZone.setDefault(this.timeZone);
  }

  @Test
  public void Signature() throws NoSuchMethodException {
    List<String> types =
        Arrays.asList(
            "boolean",
            "comparable",
            "date",
            "dsl",
            "dateTime",
            "enum",
            "number",
            "simple",
            "string",
            "time");
    for (String type : types) {
      if (type.equals("boolean") || type.equals("string")) {
        assertReturnType(Expressions.class.getMethod(type + "Path", String.class));
        assertReturnType(Expressions.class.getMethod(type + "Path", Path.class, String.class));
        assertReturnType(Expressions.class.getMethod(type + "Path", PathMetadata.class));
        assertReturnType(
            Expressions.class.getMethod(type + "Operation", Operator.class, Expression[].class));
        assertReturnType(
            Expressions.class.getMethod(type + "Template", String.class, Object[].class));
        assertReturnType(Expressions.class.getMethod(type + "Template", String.class, List.class));
        assertReturnType(
            Expressions.class.getMethod(type + "Template", Template.class, Object[].class));
        assertReturnType(
            Expressions.class.getMethod(type + "Template", Template.class, List.class));
      } else {
        assertReturnType(Expressions.class.getMethod(type + "Path", Class.class, String.class));
        assertReturnType(
            Expressions.class.getMethod(type + "Path", Class.class, Path.class, String.class));
        assertReturnType(
            Expressions.class.getMethod(type + "Path", Class.class, PathMetadata.class));
        assertReturnType(
            Expressions.class.getMethod(
                type + "Operation", Class.class, Operator.class, Expression[].class));
        assertReturnType(
            Expressions.class.getMethod(
                type + "Template", Class.class, String.class, Object[].class));
        assertReturnType(
            Expressions.class.getMethod(type + "Template", Class.class, String.class, List.class));
        assertReturnType(
            Expressions.class.getMethod(
                type + "Template", Class.class, Template.class, Object[].class));
        assertReturnType(
            Expressions.class.getMethod(
                type + "Template", Class.class, Template.class, List.class));
      }
    }

    // arrays
    assertReturnType(Expressions.class.getMethod("arrayPath", Class.class, String.class));
    assertReturnType(
        Expressions.class.getMethod("arrayPath", Class.class, Path.class, String.class));
    assertReturnType(Expressions.class.getMethod("arrayPath", Class.class, PathMetadata.class));
  }

  private void assertReturnType(Method method) {
    assertThat(method.getReturnType().getSimpleName())
        .isEqualTo(BeanUtils.capitalize(method.getName()));
  }

  @Test
  public void as() {
    assertThat(Expressions.as(null, str)).hasToString("null as str");
    assertThat(Expressions.as(new StringPath("s"), str)).hasToString("s as str");
  }

  @Test
  public void allOf() {
    assertThat(Expressions.allOf(a, b)).hasToString("a && b");
  }

  @Test
  public void allOf_with_nulls() {
    assertThat(Expressions.allOf(a, b, null)).hasToString("a && b");
    assertThat(Expressions.allOf(a, null)).hasToString("a");
    assertThat(Expressions.allOf(null, a)).hasToString("a");
  }

  @Test
  public void anyOf() {
    assertThat(Expressions.anyOf(a, b)).hasToString("a || b");
  }

  @Test
  public void anyOf_with_nulls() {
    assertThat(Expressions.anyOf(a, b, null)).hasToString("a || b");
    assertThat(Expressions.anyOf(a, null)).hasToString("a");
    assertThat(Expressions.anyOf(null, a)).hasToString("a");
  }

  @Test
  public void constant() {
    assertThat(Expressions.constant("X")).hasToString("X");
  }

  @Test
  public void constant_as() {
    assertThat(Expressions.constantAs("str", str)).hasToString("str as str");
  }

  @Test
  public void template() {
    assertThat(Expressions.template(Object.class, "{0} && {1}", a, b).toString())
        .isEqualTo("a && b");
  }

  @Test
  public void comparableTemplate() {
    assertThat(Expressions.comparableTemplate(Boolean.class, "{0} && {1}", a, b).toString())
        .isEqualTo("a && b");
  }

  @Test
  public void numberTemplate() {
    assertThat(Expressions.numberTemplate(Integer.class, "1")).hasToString("1");
  }

  @Test
  public void stringTemplate() {
    assertThat(Expressions.stringTemplate("X")).hasToString("X");
  }

  @Test
  public void booleanTemplate() {
    assertThat(Expressions.booleanTemplate("{0} && {1}", a, b)).hasToString("a && b");
  }

  @Test
  public void subQuery() {
    // TODO
  }

  @Test
  public void operation() {
    assertThat(Expressions.operation(Boolean.class, Ops.AND, a, b)).hasToString("a && b");
  }

  @Test
  public void predicate() {
    assertThat(Expressions.predicate(Ops.AND, a, b)).hasToString("a && b");
  }

  @Test
  public void pathClassOfTString() {
    assertThat(Expressions.path(String.class, "variable")).hasToString("variable");
  }

  @Test
  public void pathClassOfTPathOfQString() {
    assertThat(
            Expressions.path(String.class, Expressions.path(Object.class, "variable"), "property")
                .toString())
        .isEqualTo("variable.property");
  }

  @Test
  public void comparablePathClassOfTString() {
    assertThat(Expressions.comparablePath(String.class, "variable").toString())
        .isEqualTo("variable");
  }

  @Test
  public void comparablePathClassOfTPathOfQString() {
    assertThat(
            Expressions.comparablePath(
                    String.class, Expressions.path(Object.class, "variable"), "property")
                .toString())
        .isEqualTo("variable.property");
  }

  @Test
  public void datePathClassOfTString() {
    assertThat(Expressions.datePath(Date.class, "variable")).hasToString("variable");
  }

  @Test
  public void datePathClassOfTPathOfQString() {
    assertThat(
            Expressions.datePath(Date.class, Expressions.path(Object.class, "variable"), "property")
                .toString())
        .isEqualTo("variable.property");
  }

  @Test
  public void dateTimePathClassOfTString() {
    assertThat(Expressions.dateTimePath(Date.class, "variable")).hasToString("variable");
  }

  @Test
  public void dateTimePathClassOfTPathOfQString() {
    assertThat(
            Expressions.dateTimePath(
                    Date.class, Expressions.path(Object.class, "variable"), "property")
                .toString())
        .isEqualTo("variable.property");
  }

  @Test
  public void timePathClassOfTString() {
    assertThat(Expressions.timePath(Date.class, "variable")).hasToString("variable");
  }

  @Test
  public void timePathClassOfTPathOfQString() {
    assertThat(
            Expressions.timePath(Date.class, Expressions.path(Object.class, "variable"), "property")
                .toString())
        .isEqualTo("variable.property");
  }

  @Test
  public void numberPathClassOfTString() {
    assertThat(Expressions.numberPath(Integer.class, "variable")).hasToString("variable");
  }

  @Test
  public void numberPathClassOfTPathOfQString() {
    assertThat(
            Expressions.numberPath(
                    Integer.class, Expressions.path(Object.class, "variable"), "property")
                .toString())
        .isEqualTo("variable.property");
  }

  @Test
  public void stringPathString() {
    assertThat(Expressions.stringPath("variable")).hasToString("variable");
  }

  @Test
  public void stringPathPathOfQString() {
    assertThat(
            Expressions.stringPath(Expressions.path(Object.class, "variable"), "property")
                .toString())
        .isEqualTo("variable.property");
  }

  @Test
  public void stringOperation() {
    assertThat(Expressions.stringOperation(Ops.SUBSTR_1ARG, str, ConstantImpl.create(2)).toString())
        .isEqualTo("substring(str,2)");
  }

  @Test
  public void booleanPathString() {
    assertThat(Expressions.booleanPath("variable")).hasToString("variable");
  }

  @Test
  public void booleanPathPathOfQString() {
    assertThat(
            Expressions.booleanPath(Expressions.path(Object.class, "variable"), "property")
                .toString())
        .isEqualTo("variable.property");
  }

  @Test
  public void booleanOperation() {
    assertThat(Expressions.booleanOperation(Ops.AND, a, b)).hasToString("a && b");
  }

  @Test
  public void comparableOperation() {
    assertThat(Expressions.comparableOperation(Boolean.class, Ops.AND, a, b).toString())
        .isEqualTo("a && b");
  }

  @Test
  public void dateOperation() {
    assertThat(Expressions.dateOperation(Date.class, Ops.DateTimeOps.CURRENT_DATE).toString())
        .isEqualTo("current_date()");
  }

  @Test
  public void dateTimeOperation() {
    assertThat(
            Expressions.dateTimeOperation(Date.class, Ops.DateTimeOps.CURRENT_TIMESTAMP).toString())
        .isEqualTo("current_timestamp()");
  }

  @Test
  public void timeOperation() {
    assertThat(Expressions.timeOperation(Time.class, Ops.DateTimeOps.CURRENT_TIME).toString())
        .isEqualTo("current_time()");
  }

  @Test
  public void cases() {
    // TODO
  }

  @Test
  public void asBoolean_returns_a_corresponding_BooleanExpression_for_a_given_Expression() {
    assertThat(Expressions.asBoolean(Expressions.constant(true)).isTrue().toString())
        .isEqualTo("true = true");
  }

  @Test
  public void asBoolean_returns_a_corresponding_BooleanExpression_for_a_given_Constant() {
    assertThat(Expressions.asBoolean(true).isTrue()).hasToString("true = true");
  }

  @Test
  public void asBoolean_equals_works_for_returned_values() {
    assertThat(Expressions.asBoolean(true)).isEqualTo(Expressions.asBoolean(true));
    assertThat(Expressions.asBoolean(false)).isNotEqualTo(Expressions.asBoolean(true));
  }

  @Test
  public void asComparable_returns_a_corresponding_ComparableExpression_for_a_given_Expression() {
    assertThat(
            Expressions.asComparable(Expressions.constant(1L))
                .eq(Expressions.constant(1L))
                .toString())
        .isEqualTo("1 = 1");
  }

  @Test
  public void asComparable_returns_a_corresponding_ComparableExpression_for_a_given_Constant() {
    assertThat(Expressions.asComparable(1L).eq(1L)).hasToString("1 = 1");
  }

  @Test
  public void asComparable_equals_works_for_returned_values() {
    assertThat(Expressions.asComparable(1L)).isEqualTo(Expressions.asComparable(1L));
    assertThat(Expressions.asComparable(2L)).isNotEqualTo(Expressions.asComparable(1L));
  }

  @Test
  public void asDate_returns_a_corresponding_DateExpression_for_a_given_Expression() {
    assertThat(Expressions.asDate(Expressions.constant(new Date(1L))).year().toString())
        .isEqualTo("year(Thu Jan 01 00:00:00 UTC 1970)");
  }

  @Test
  public void asDate_returns_a_corresponding_DateExpression_for_a_given_Constant() {
    assertThat(Expressions.asDate(new Date(1L)).year().toString())
        .isEqualTo("year(Thu Jan 01 00:00:00 UTC 1970)");
  }

  @Test
  public void asDate_equals_works_for_returned_values() {
    assertThat(Expressions.asDate(new Date(1L)).year())
        .isEqualTo(Expressions.asDate(new Date(1L)).year());
    assertThat(Expressions.asDate(new Date(2L)).year())
        .isNotEqualTo(Expressions.asDate(new Date(1L)).year());
  }

  @Test
  public void asDateTime_returns_a_corresponding_DateTimeExpression_for_a_given_Expression() {
    assertThat(Expressions.asDateTime(Expressions.constant(new Date(1L))).min().toString())
        .isEqualTo("min(Thu Jan 01 00:00:00 UTC 1970)");
  }

  @Test
  public void asDateTime_returns_a_corresponding_DateTimeExpression_for_a_given_Constant() {
    assertThat(Expressions.asDateTime(new Date(1L)).min().toString())
        .isEqualTo("min(Thu Jan 01 00:00:00 UTC 1970)");
  }

  @Test
  public void asDateTime_equals_works_for_returned_values() {
    assertThat(Expressions.asDateTime(new Date(1L)).min())
        .isEqualTo(Expressions.asDateTime(new Date(1L)).min());
    assertThat(Expressions.asDateTime(new Date(2L)).min())
        .isNotEqualTo(Expressions.asDateTime(new Date(1L)).min());
  }

  @Test
  public void asTime_returns_a_corresponding_TimeExpression_for_a_given_Expression() {
    assertThat(Expressions.asTime(Expressions.constant(new Date(1L))).hour().toString())
        .isEqualTo("hour(Thu Jan 01 00:00:00 UTC 1970)");
  }

  @Test
  public void asTime_returns_a_corresponding_TimeExpression_for_a_given_Constant() {
    assertThat(Expressions.asTime(new Date(1L)).hour().toString())
        .isEqualTo("hour(Thu Jan 01 00:00:00 UTC 1970)");
  }

  @Test
  public void asTime_equals_works_for_returned_values() {
    assertThat(Expressions.asTime(new Date(1L)).hour())
        .isEqualTo(Expressions.asTime(new Date(1L)).hour());
    assertThat(Expressions.asTime(new Date(2L)).hour())
        .isNotEqualTo(Expressions.asTime(new Date(1L)).hour());
  }

  @Test
  public void asEnum_returns_a_corresponding_EnumExpression_for_a_given_Expression() {
    assertThat(Expressions.asEnum(Expressions.constant(testEnum.TEST)).ordinal().toString())
        .isEqualTo("ordinal(TEST)");
  }

  @Test
  public void asEnum_returns_a_corresponding_EnumExpression_for_a_given_Constant() {
    assertThat(Expressions.asEnum(testEnum.TEST).ordinal()).hasToString("ordinal(TEST)");
  }

  @Test
  public void asEnum_equals_works_for_returned_values() {
    assertThat(Expressions.asEnum(testEnum.TEST)).isEqualTo(Expressions.asEnum(testEnum.TEST));
    assertThat(Expressions.asEnum(testEnum.TEST_2)).isNotEqualTo(Expressions.asEnum(testEnum.TEST));
  }

  @Test
  public void asNumber_returns_a_corresponding_NumberExpression_for_a_given_Expression() {
    assertThat(
            Expressions.asNumber(Expressions.constant(1L)).add(Expressions.constant(1L)).toString())
        .isEqualTo("1 + 1");
  }

  @Test
  public void asNumber_returns_a_corresponding_NumberExpression_for_a_given_Constant() {
    assertThat(Expressions.asNumber(1L).add(Expressions.constant(1L)).toString())
        .isEqualTo("1 + 1");
  }

  @Test
  public void asNumber_equals_works_for_returned_values() {
    assertThat(Expressions.asNumber(42L)).isEqualTo(Expressions.asNumber(42L));
    assertThat(Expressions.asNumber(256L)).isNotEqualTo(Expressions.asNumber(42L));
  }

  @Test
  public void asString_returns_a_corresponding_StringExpression_for_a_given_Expression() {
    assertThat(
            Expressions.asString(Expressions.constant("left"))
                .append(Expressions.constant("right"))
                .toString())
        .isEqualTo("left + right");
  }

  @Test
  public void asString_returns_a_corresponding_StringExpression_for_a_given_Constant() {
    assertThat(Expressions.asString("left").append(Expressions.constant("right")).toString())
        .isEqualTo("left + right");
  }

  @Test
  public void asString_equals_works_for_returned_values() {
    assertThat(Expressions.asString("foo")).isEqualTo(Expressions.asString("foo"));
    assertThat(Expressions.asString("bar")).isNotEqualTo(Expressions.asString("foo"));
  }
}
