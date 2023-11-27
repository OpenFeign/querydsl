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
    DomainType domainType = Alias.alias(DomainType.class);
    Alias.alias(DomainType.class, $(domainType.getCollection()).any());
  }

  @Test
  public void comparableEntity() {
    ComparableEntity entity = Alias.alias(ComparableEntity.class);
    Path<ComparableEntity> path = $(entity);
    assertThat(path.getType()).isEqualTo(ComparableEntity.class);
  }

  @Test
  public void comparableEntity_property() {
    ComparableEntity entity = Alias.alias(ComparableEntity.class);
    Path<String> propertyPath = $(entity.getProperty());
    assertThat(propertyPath.getType()).isEqualTo(String.class);
    assertThat(propertyPath.getMetadata().getName()).isEqualTo("property");
  }

  @Test
  public void basicUsage() {
    DomainType domainType = Alias.alias(DomainType.class);
    assertThat($(domainType.getFirstName()).lower().toString())
        .isEqualTo("lower(domainType.firstName)");
    assertThat($(domainType.getAge()).toString()).isEqualTo("domainType.age");
    assertThat($(domainType.getMap().get("a")).toString()).isEqualTo("domainType.map.get(a)");
    assertThat($(domainType.getList().get(0)).toString()).isEqualTo("domainType.list.get(0)");

    assertThat($(domainType.getBigDecimal()).toString()).isEqualTo("domainType.bigDecimal");
    assertThat($(domainType.getBigInteger()).toString()).isEqualTo("domainType.bigInteger");
    assertThat($(domainType.getByte()).toString()).isEqualTo("domainType.byte");
    assertThat($(domainType.getCollection()).toString()).isEqualTo("domainType.collection");
    assertThat($(domainType.getDouble()).toString()).isEqualTo("domainType.double");
    assertThat($(domainType.getFloat()).toString()).isEqualTo("domainType.float");
    assertThat($(domainType.getDate()).toString()).isEqualTo("domainType.date");
    assertThat($(domainType.getDate2()).toString()).isEqualTo("domainType.date2");
    assertThat($(domainType.getSet()).toString()).isEqualTo("domainType.set");
    assertThat($(domainType.getShort()).toString()).isEqualTo("domainType.short");
    assertThat($(domainType.getTime()).toString()).isEqualTo("domainType.time");
    assertThat($(domainType.getTimestamp()).toString()).isEqualTo("domainType.timestamp");
    assertThat($(domainType.getGender()).toString()).isEqualTo("domainType.gender");
  }

  @Test
  public void getAny() {
    DomainType domainType = Alias.alias(DomainType.class);
    assertThat(Alias.getAny(domainType).getType()).isEqualTo(DomainType.class);
    assertThat(Alias.getAny(domainType.getFirstName()).getType()).isEqualTo(String.class);
  }

  @Test
  public void otherMethods() {
    DomainType domainType = Alias.alias(DomainType.class);
    assertThat(domainType.toString()).isEqualTo("domainType");
  }

  @Test
  public void var() {
    assertThat(Alias.var().toString()).isEqualTo("it");
    assertThat(Alias.var(1).toString()).isEqualTo("varInteger1");
    assertThat(Alias.var("X").toString()).isEqualTo("X");
    assertThat(Alias.var(Gender.MALE).toString()).isEqualTo("varMALE");
    assertThat(Alias.var(new AliasTest()).toString()).isEqualTo("varAliasTest_XXX");
  }

  @Override
  public String toString() {
    return "XXX";
  }
}
