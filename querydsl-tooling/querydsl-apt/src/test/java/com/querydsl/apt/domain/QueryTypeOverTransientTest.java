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

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryTransient;
import com.querydsl.core.annotations.QueryType;
import org.junit.Test;

public class QueryTypeOverTransientTest {

  @QueryEntity
  public static class Entity {

    @QueryType(PropertyType.ENTITY)
    @QueryTransient
    Entity reference;
  }

  @QueryEntity
  public abstract static class Entity2 {

    @QueryType(PropertyType.ENTITY)
    @QueryTransient
    public abstract Entity getReference();
  }

  @Test
  public void entity_reference_is_available() {
    assertThat(QQueryTypeOverTransientTest_Entity.entity.reference).isNotNull();
  }

  @Test
  public void entity2_reference_is_available() {
    assertThat(QQueryTypeOverTransientTest_Entity2.entity2.reference).isNotNull();
  }
}
