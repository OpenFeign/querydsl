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
package com.querydsl.core.alias;

import static com.querydsl.core.alias.Alias.$;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Path;
import org.junit.Test;

public class AliasTest {

  @Test
  public void alias() {
    var domainType = Alias.alias(DomainType.class);
    Alias.alias(DomainType.class, $(domainType.getCollection()).any());
  }

  @Test
  public void comparableEntity() {
    var entity = Alias.alias(ComparableEntity.class);
    Path<ComparableEntity> path = $(entity);
    assertThat(path.getType()).isEqualTo(ComparableEntity.class);
  }

  @Test
  public void comparableEntity_property() {
    var entity = Alias.alias(ComparableEntity.class);
    Path<String> propertyPath = $(entity.getProperty());
    assertThat(propertyPath.getType()).isEqualTo(String.class);
    assertThat(propertyPath.getMetadata().getName()).isEqualTo("property");
  }

  @Test
  public void basicUsage() {
    var domainType = Alias.alias(DomainType.class);
    assertThat($(domainType.getFirstName()).lower().toString())
        .isEqualTo("lower(domainType.firstName)");
    assertThat($(domainType.getAge())).hasToString("domainType.age");
    assertThat($(domainType.getMap().get("a"))).hasToString("domainType.map.get(a)");
    assertThat($(domainType.getList().get(3))).hasToString("domainType.list.get(3)");
    assertThat($(domainType.getList().getFirst())).hasToString("domainType.list.getFirst()");

    assertThat($(domainType.getBigDecimal())).hasToString("domainType.bigDecimal");
    assertThat($(domainType.getBigInteger())).hasToString("domainType.bigInteger");
    assertThat($(domainType.getByte())).hasToString("domainType.byte");
    assertThat($(domainType.getCollection())).hasToString("domainType.collection");
    assertThat($(domainType.getDouble())).hasToString("domainType.double");
    assertThat($(domainType.getFloat())).hasToString("domainType.float");
    assertThat($(domainType.getDate())).hasToString("domainType.date");
    assertThat($(domainType.getDate2())).hasToString("domainType.date2");
    assertThat($(domainType.getSet())).hasToString("domainType.set");
    assertThat($(domainType.getShort())).hasToString("domainType.short");
    assertThat($(domainType.getTime())).hasToString("domainType.time");
    assertThat($(domainType.getTimestamp())).hasToString("domainType.timestamp");
    assertThat($(domainType.getGender())).hasToString("domainType.gender");
  }

  @Test
  public void getAny() {
    var domainType = Alias.alias(DomainType.class);
    assertThat(Alias.getAny(domainType).getType()).isEqualTo(DomainType.class);
    assertThat(Alias.getAny(domainType.getFirstName()).getType()).isEqualTo(String.class);
  }

  @Test
  public void otherMethods() {
    var domainType = Alias.alias(DomainType.class);
    assertThat(domainType).hasToString("domainType");
  }

  @Test
  public void var() {
    assertThat(Alias.var()).hasToString("it");
    assertThat(Alias.var(1)).hasToString("varInteger1");
    assertThat(Alias.var("X")).hasToString("X");
    assertThat(Alias.var(Gender.MALE)).hasToString("varMALE");
    assertThat(Alias.var(new AliasTest())).hasToString("varAliasTest_XXX");
  }

  @Override
  public String toString() {
    return "XXX";
  }
}
