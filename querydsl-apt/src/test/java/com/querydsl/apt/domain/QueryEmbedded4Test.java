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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryInit;
import org.junit.Test;

public class QueryEmbedded4Test {

  @QueryEntity
  public static class User {

    @QueryEmbedded
    @QueryInit("city.name")
    Address address;

    @QueryEmbedded Complex<String> complex;
  }

  public static class Address {

    @QueryEmbedded City city;

    String name;
  }

  public static class City {

    String name;
  }

  public static class Complex<T extends Comparable<T>> implements Comparable<Complex<T>> {

    T a;

    @Override
    public int compareTo(Complex<T> arg0) {
      return 0;
    }

    public boolean equals(Object o) {
      return o == this;
    }
  }

  @Test
  public void user_address_city() {
    assertThat(QQueryEmbedded4Test_User.user.address.city).isNotNull();
  }

  @Test
  public void user_address_name() {
    assertThat(QQueryEmbedded4Test_User.user.address.name).isNotNull();
  }

  @Test
  public void user_address_city_name() {
    assertThat(QQueryEmbedded4Test_User.user.address.city.name).isNotNull();
  }

  @Test
  public void user_complex_a() {
    assertThat(QQueryEmbedded4Test_User.user.complex.a).isNotNull();
  }
}
