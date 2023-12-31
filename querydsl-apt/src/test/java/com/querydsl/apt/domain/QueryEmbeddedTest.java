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
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class QueryEmbeddedTest {

  @QueryEntity
  public static class Parent {

    String parentProperty;

    @QueryEmbedded Child child;
  }

  @QueryEntity
  public static class Parent2 {

    String parentProperty;

    @QueryEmbedded List<Child> children;

    @QueryEmbedded Map<String, Child> children2;
  }

  public static class Child {

    String childProperty;
  }

  @Test
  public void parent_child_childProperty() {
    assertThat(QQueryEmbeddedTest_Parent.parent.child.childProperty).isNotNull();
  }

  @Test
  public void parent_children_any_childProperty() {
    assertThat(QQueryEmbeddedTest_Parent2.parent2.children.any().childProperty).isNotNull();
  }

  @Test
  public void parent_children2_mapAccess() {
    assertThat(QQueryEmbeddedTest_Parent2.parent2.children2.containsKey("XXX")).isNotNull();
    assertThat(QQueryEmbeddedTest_Parent2.parent2.children2.get("XXX").childProperty).isNotNull();
  }
}
