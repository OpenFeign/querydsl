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

import static org.junit.Assert.assertNotNull;

import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntity;
import java.util.List;
import org.junit.Test;

public class QueryEmbedded3Test {

  @QueryEntity
  public static class Parent {

    String parentProperty;

    @QueryEmbedded List<Child> children;

    @QueryEmbedded Child child;
  }

  public static class Child {

    String childProperty;
  }

  @Test
  public void test() {
    assertNotNull(QQueryEmbedded3Test_Parent.parent.child.childProperty);
    assertNotNull(QQueryEmbedded3Test_Parent.parent.children.any().childProperty);
  }
}
