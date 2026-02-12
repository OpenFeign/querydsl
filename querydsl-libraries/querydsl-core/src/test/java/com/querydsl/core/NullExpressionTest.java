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
package com.querydsl.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.querydsl.core.domain.Cat;
import com.querydsl.core.domain.QCat;
import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class NullExpressionTest {

  @Test
  void withConstructor() {
    var cat =
        Projections.constructor(
                Cat.class,
                Expressions.nullExpression(String.class),
                QCat.cat.id,
                QCat.cat.bodyWeight)
            .newInstance(null, 1, 2.5);
    assertThat(cat).isNotNull();
  }

  @Test
  void withoutConstructor() {
    assertThatExceptionOfType(ExpressionException.class)
        .isThrownBy(
            () ->
                Projections.constructor(
                        Cat.class,
                        Expressions.nullExpression(String.class),
                        QCat.cat.id,
                        QCat.cat.birthdate)
                    .newInstance(null, 1, LocalDate.now()));
  }
}
