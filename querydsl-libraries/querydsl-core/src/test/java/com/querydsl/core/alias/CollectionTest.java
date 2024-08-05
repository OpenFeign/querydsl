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

import com.querydsl.core.types.EntityPath;
import org.junit.Test;

public class CollectionTest {

  @Test
  public void collectionUsage() {
    var domainType = Alias.alias(DomainType.class);
    assertThat($(domainType.getCollection()).any().eq(domainType).toString())
        .isEqualTo("any(domainType.collection) = domainType");
    assertThat($(domainType.getSet()).any().eq(domainType).toString())
        .isEqualTo("any(domainType.set) = domainType");
    assertThat($(domainType.getList()).any().eq(domainType).toString())
        .isEqualTo("any(domainType.list) = domainType");
    assertThat($(domainType.getList().getFirst()).eq(domainType).toString())
        .isEqualTo("domainType.list.getFirst() = domainType");
    assertThat($(domainType.getList().get(3)).eq(domainType).toString())
        .isEqualTo("domainType.list.get(3) = domainType");
    assertThat($(domainType.getList()).get(0).eq(domainType).toString())
        .isEqualTo("domainType.list.get(0) = domainType");
    assertThat($(domainType.getMap()).get("key").eq(domainType).toString())
        .isEqualTo("domainType.map.get(key) = domainType");

    EntityPath<DomainType> domainTypePath = $(domainType);
    assertThat($(domainType.getCollection()).contains(domainTypePath).toString())
        .isEqualTo("domainType in domainType.collection");
  }

  @Test
  public void collectionUsage_types() {
    var domainType = Alias.alias(DomainType.class);
    assertThat($(domainType.getCollection()).any().getType()).isEqualTo(DomainType.class);
    assertThat($(domainType.getSet()).any().getType()).isEqualTo(DomainType.class);
    assertThat($(domainType.getList()).any().getType()).isEqualTo(DomainType.class);
    assertThat($(domainType.getMap()).get("key").getType()).isEqualTo(DomainType.class);
  }
}
