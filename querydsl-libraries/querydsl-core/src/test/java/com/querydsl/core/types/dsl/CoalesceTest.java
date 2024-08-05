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

import org.junit.Test;

public class CoalesceTest {

  private final StringPath firstname = new StringPath("firstname");

  private final StringPath lastname = new StringPath("lastname");

  @Test
  public void mutable() {
    var c = new Coalesce<String>(firstname, lastname).add("xxx");
    assertThat(c).hasToString("coalesce(firstname, lastname, xxx)");
    assertThat(c.add("yyy")).hasToString("coalesce(firstname, lastname, xxx, yyy)");
  }

  @Test
  public void withList() {
    var c = new Coalesce<String>(firstname, lastname).add("xxx");
    assertThat(c).hasToString("coalesce(firstname, lastname, xxx)");
  }

  @Test
  public void withSingleArg() {
    var c = new Coalesce<String>().add("xxx");
    assertThat(c).hasToString("coalesce(xxx)");
  }

  @Test
  public void asComparable() {
    var c = new Coalesce<>(firstname, lastname);
    c.getValue().asc();
  }

  @Test
  public void asString() {
    var c = new Coalesce<>(firstname, lastname);
    c.asString().lower();
  }

  @Test
  public void withoutWarnings() {
    var c = new Coalesce<>(String.class).add(firstname).add(lastname);
    assertThat(c).hasToString("coalesce(firstname, lastname)");
  }

  @Test
  public void dsl() {
    assertThat(firstname.coalesce(lastname)).hasToString("coalesce(firstname, lastname)");
  }

  @Test
  public void dsl2() {
    assertThat(new Coalesce<String>().add(firstname).add(lastname).add("xxx").toString())
        .isEqualTo("coalesce(firstname, lastname, xxx)");
  }

  @Test
  public void dsl3() {
    assertThat(firstname.coalesce("xxx")).hasToString("coalesce(firstname, xxx)");
  }

  @Test
  public void asc() {
    assertThat(firstname.coalesce("xxx").asc().toString())
        .isEqualTo("coalesce(firstname, xxx) ASC");
  }

  @Test
  public void desc() {
    assertThat(firstname.coalesce("xxx").desc().toString())
        .isEqualTo("coalesce(firstname, xxx) DESC");
  }
}
