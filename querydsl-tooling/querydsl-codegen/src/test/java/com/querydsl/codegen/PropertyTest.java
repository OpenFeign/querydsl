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
package com.querydsl.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import java.util.Collections;
import org.junit.Test;

public class PropertyTest {

  @Test
  public void equals_and_hashCode() {
    Type typeModel =
        new SimpleType(
            TypeCategory.ENTITY,
            "com.querydsl.DomainClass",
            "com.querydsl",
            "DomainClass",
            false,
            false);
    var type = new EntityType(typeModel);
    var p1 = new Property(type, "property", type, Collections.<String>emptyList());
    var p2 = new Property(type, "property", type, Collections.<String>emptyList());
    assertThat(p1).isEqualTo(p1);
    assertThat(p2).isEqualTo(p1);
    assertThat(p2.hashCode()).isEqualTo(p1.hashCode());
  }

  @Test
  public void escapedName() {
    Type typeModel =
        new SimpleType(
            TypeCategory.ENTITY,
            "com.querydsl.DomainClass",
            "com.querydsl",
            "DomainClass",
            false,
            false);
    var type = new EntityType(typeModel);
    var property = new Property(type, "boolean", type, Collections.<String>emptyList());
    assertThat(property.getEscapedName()).isEqualTo("boolean$");
  }
}
