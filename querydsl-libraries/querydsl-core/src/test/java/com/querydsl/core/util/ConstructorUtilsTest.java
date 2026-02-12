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

import static com.querydsl.core.util.ArrayUtils.isEmpty;
import static com.querydsl.core.util.ConstructorUtils.getConstructorParameters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.querydsl.core.types.ProjectionExample;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 * @author Shredder121
 */
class ConstructorUtilsTest {

  @Test
  void getDefaultConstructor() {
    Class<?>[] args = {};
    Constructor<?> emptyDefaultConstructor = getConstructor(ProjectionExample.class, args);
    Constructor<?> nullDefaultConstructor = getConstructor(ProjectionExample.class, null);
    assertThat(emptyDefaultConstructor).isNotNull();
    assertThat(nullDefaultConstructor).isNotNull();
    assertThat(
            isEmpty(emptyDefaultConstructor.getParameterTypes())
                && isEmpty(nullDefaultConstructor.getParameterTypes()))
        .isTrue();
  }

  @Test
  void getSimpleConstructor() {
    Class<?>[] args = {Long.class};
    Constructor<?> constructor = getConstructor(ProjectionExample.class, args);
    assertThat(constructor).isNotNull();
    assertThat(constructor.getParameterTypes()).containsExactly(args);
  }

  @Test
  void getDefaultConstructorParameters() {
    Class<?>[] args = {Long.class, String.class};
    Class<?>[] expected = {Long.TYPE, String.class};
    var constructorParameters = getConstructorParameters(ProjectionExample.class, args);
    assertThat(constructorParameters)
        .as("Constructorparameters not equal")
        .containsExactly(expected);
  }

  private <C> Constructor<C> getConstructor(Class<C> type, Class<?>[] givenTypes) {
    Constructor<C> rv = null;
    try {
      rv = ConstructorUtils.getConstructor(type, givenTypes);
    } catch (NoSuchMethodException _) {
      fail(
          "",
          "No constructor found for "
              + type.toString()
              + " with parameters: "
              + Arrays.toString(givenTypes));
    }
    return rv;
  }
}
