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
package com.querydsl.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class ReflectionUtilsTest {

  String property;

  @Test
  public void getAnnotatedElement() {
    AnnotatedElement annotatedElement =
        ReflectionUtils.getAnnotatedElement(ReflectionUtilsTest.class, "property", String.class);
    assertNotNull(annotatedElement);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void getImplementedInterfaces() {
    Set<Class<?>> ifaces = ReflectionUtils.getImplementedInterfaces(SimpleExpression.class);
    assertEquals(
        new HashSet<Class<?>>(Arrays.asList(Serializable.class, Expression.class)), ifaces);
  }
}
