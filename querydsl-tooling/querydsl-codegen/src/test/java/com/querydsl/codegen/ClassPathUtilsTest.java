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

import com.SomeClass;
import java.io.IOException;
import org.junit.Test;

public class ClassPathUtilsTest {

  @Test
  public void scanPackage() throws IOException {
    var classLoader = Thread.currentThread().getContextClassLoader();
    var classes = ClassPathUtils.scanPackage(classLoader, SomeClass.class.getPackage());
    assertThat(classes).isNotEmpty();
  }

  @Test
  public void scanPackage_check_initialized() throws IOException {
    var classLoader = Thread.currentThread().getContextClassLoader();
    var classes = ClassPathUtils.scanPackage(classLoader, getClass().getPackage());
    assertThat(classes).isNotEmpty();
    assertThat(SomeOtherClass2.property).isEqualTo("XXX");
  }

  @Test
  public void safeClassForName() {
    assertThat(safeForName("com.sun.nio.file.ExtendedOpenOption")).isNull();
    assertThat(safeForName("com.suntanning.ShouldBeLoaded")).isNotNull();
    assertThat(safeForName("com.applejuice.ShouldBeLoaded")).isNotNull();
  }

  private Class<?> safeForName(String className) {
    return ClassPathUtils.safeClassForName(ClassPathUtilsTest.class.getClassLoader(), className);
  }
}
