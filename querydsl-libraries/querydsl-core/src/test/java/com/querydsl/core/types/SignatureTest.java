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
package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.core.types.dsl.DslExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class SignatureTest {

  private List<Class<?>> classes = new ArrayList<>();

  @Before
  public void setUp() throws ClassNotFoundException {
    for (String folder : Collections.singletonList("com/querydsl/core/types/dsl")) {
      for (String file : new File("src/main/java", folder).list()) {
        if (file.endsWith(".java") && !file.equals("package-info.java")) {
          var className = (folder + "." + file.substring(0, file.length() - 5)).replace('/', '.');
          classes.add(Class.forName(className));
        }
      }
    }
  }

  @Test
  public void returnType_extends_simpleExpression() {
    assertThat(classes).isNotEmpty();
    Set<String> skippedMethods = new HashSet<>(Arrays.asList("getArg", "getRoot", "not"));
    List<String> errors = new ArrayList<>();
    for (Class<?> cl : classes) {
      if (cl.equals(Expressions.class)) {
        continue;
      }
      for (Method m : cl.getDeclaredMethods()) {
        if (!skippedMethods.contains(m.getName())
            && Modifier.isPublic(m.getModifiers())
            && Expression.class.isAssignableFrom(m.getReturnType())
            && !Coalesce.class.isAssignableFrom(m.getReturnType())
            && !DslExpression.class.isAssignableFrom(m.getReturnType())
            && !SimpleExpression.class.isAssignableFrom(m.getReturnType())) {
          errors.add(cl.getSimpleName() + "." + m.getName() + " has illegal return type");
        }
      }
    }
    for (String error : errors) {
      System.err.println(error);
    }
    if (!errors.isEmpty()) {
      fail("Got " + errors.size() + " errors");
    }
  }
}
