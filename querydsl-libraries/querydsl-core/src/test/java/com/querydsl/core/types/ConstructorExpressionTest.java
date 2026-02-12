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

import com.querydsl.core.testutil.Serialization;
import com.querydsl.core.testutil.ThreadSafety;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ConstructorExpressionTest {

  StringPath str1 = Expressions.stringPath("str1");
  StringPath str2 = Expressions.stringPath("str2");
  StringPath str3 = Expressions.stringPath("str3");
  Concatenation concat = new Concatenation(str1, str2);

  @Test
  void constructor() {
    Expression<Long> longVal = ConstantImpl.create(1L);
    Expression<String> stringVal = ConstantImpl.create("");
    var instance =
        new ConstructorExpression<>(
                ProjectionExample.class,
                new Class<?>[] {long.class, String.class},
                longVal,
                stringVal)
            .newInstance(0L, "");
    assertThat(instance).isNotNull();
    assertThat(instance.id).isEqualTo((Long) 0L);
    assertThat(instance.text).isEmpty();
  }

  @Test
  void create() {
    Expression<Long> longVal = ConstantImpl.create(1L);
    Expression<String> stringVal = ConstantImpl.create("");
    assertThat(
            Projections.constructor(ProjectionExample.class, longVal, stringVal)
                .newInstance(0L, ""))
        .isNotNull();
  }

  @Test
  void create2() {
    Expression<Long> longVal = ConstantImpl.create(1L);
    assertThat(Projections.constructor(ProjectionExample.class, longVal).newInstance(0L))
        .isNotNull();
  }

  @Test
  void create3() {
    assertThat(Projections.constructor(ProjectionExample.class).newInstance()).isNotNull();
  }

  @Test
  void create4() {
    Expression<String> stringVal = ConstantImpl.create("");
    assertThat(Projections.constructor(ProjectionExample.class, stringVal).newInstance(""))
        .isNotNull();
  }

  @Test
  void createNullPrimitive() {
    Expression<Boolean> booleanVal = ConstantImpl.create(false);
    Expression<Byte> byteVal = ConstantImpl.create((byte) 0);
    Expression<Character> charVal = ConstantImpl.create('\0');
    Expression<Short> shortVal = ConstantImpl.create((short) 0);
    Expression<Integer> intVal = ConstantImpl.create(0);
    Expression<Long> longVal = ConstantImpl.create(0L);
    Expression<Float> floatVal = ConstantImpl.create(0.0F);
    Expression<Double> doubleVal = ConstantImpl.create(0.0);
    var instance =
        Projections.constructor(
                ProjectionExample.class,
                booleanVal,
                byteVal,
                charVal,
                shortVal,
                intVal,
                longVal,
                floatVal,
                doubleVal)
            .newInstance(null, null, null, null, null, null, null, null);
    assertThat(instance).isNotNull();
  }

  @Test
  void factoryExpression_has_right_args() {
    FactoryExpression<ProjectionExample> constructor =
        Projections.constructor(ProjectionExample.class, concat);
    constructor = FactoryExpressionUtils.wrap(constructor);
    assertThat(constructor.getArgs()).containsExactlyElementsOf(Arrays.asList(str1, str2));
  }

  @Test
  void factoryExpression_newInstance() {
    FactoryExpression<ProjectionExample> constructor =
        Projections.constructor(ProjectionExample.class, concat);
    constructor = FactoryExpressionUtils.wrap(constructor);
    ProjectionExample projection = constructor.newInstance("12", "34");
    assertThat(projection.text).isEqualTo("1234");
  }

  @Test
  void serializability() {
    ConstructorExpression<String> expr =
        Serialization.serialize(Projections.constructor(String.class));
    assertThat(expr.newInstance()).isEmpty();
  }

  @Test
  void threadSafety() {
    final ConstructorExpression<String> expr = Projections.constructor(String.class);
    Runnable invoker = () -> expr.newInstance();
    ThreadSafety.check(invoker, invoker);
  }

  @Test
  void constructorArgsShouldBeSerialized() {
    var longArg = ConstantImpl.create(1L);
    var stringArg = ConstantImpl.create("");
    var projection =
        new ConstructorExpression<ProjectionExample>(
            ProjectionExample.class, new Class<?>[] {long.class, String.class}, longArg, stringArg);
    var deserializationResult = Serialization.serialize(projection);
    assertThat(deserializationResult).isEqualTo(projection);
  }
}
