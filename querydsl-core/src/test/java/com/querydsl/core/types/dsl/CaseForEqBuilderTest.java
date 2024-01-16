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

import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Time;
import org.junit.Test;

public class CaseForEqBuilderTest {

  public enum EnumExample {
    A,
    B
  }

  public static class Customer {
    private long annualSpending;

    public long getAnnualSpending() {
      return annualSpending;
    }
  }

  @Test
  public void numberTyped() {
    Customer c = alias(Customer.class, "customer");

    NumberExpression<Integer> cases =
        $(c.getAnnualSpending())
            .when(1000L)
            .then(1)
            .when(2000L)
            .then(2)
            .when(5000L)
            .then(3)
            .otherwise(4);

    assertThat(cases.toString())
        .isEqualTo(
            """
            case customer.annualSpending \
            when 1000 then 1 \
            when 2000 then 2 \
            when 5000 then 3 \
            else 4 \
            end\
            """);
  }

  @Test
  public void stringTyped() {
    Customer c = alias(Customer.class, "customer");

    StringExpression cases =
        $(c.getAnnualSpending())
            .when(1000L)
            .then("bronze")
            .when(2000L)
            .then("silver")
            .when(5000L)
            .then("gold")
            .otherwise("platinum");

    assertThat(cases).isNotNull();
  }

  @Test
  public void booleanTyped() {
    Customer c = alias(Customer.class, "customer");

    BooleanExpression cases = $(c.getAnnualSpending()).when(1000L).then(true).otherwise(false);

    assertThat(cases).isNotNull();
  }

  @Test
  public void dateType() {
    Customer c = alias(Customer.class, "customer");

    DateExpression<java.sql.Date> cases =
        $(c.getAnnualSpending())
            .when(1000L)
            .then(new java.sql.Date(0))
            .otherwise(new java.sql.Date(0));

    assertThat(cases).isNotNull();
  }

  @Test
  public void dateTimeType() {
    Customer c = alias(Customer.class, "customer");

    DateTimeExpression<java.util.Date> cases =
        $(c.getAnnualSpending())
            .when(1000L)
            .then(new java.util.Date(0))
            .otherwise(new java.util.Date(0));

    assertThat(cases).isNotNull();
  }

  @Test
  public void timeType() {
    Customer c = alias(Customer.class, "customer");

    TimeExpression<Time> cases =
        $(c.getAnnualSpending()).when(1000L).then(new Time(0)).otherwise(new Time(0));

    assertThat(cases).isNotNull();
  }

  @Test
  public void enumType() {
    Customer c = alias(Customer.class, "customer");

    EnumExpression<EnumExample> cases =
        $(c.getAnnualSpending()).when(1000L).then(EnumExample.A).otherwise(EnumExample.B);

    assertThat(cases).isNotNull();
  }
}
