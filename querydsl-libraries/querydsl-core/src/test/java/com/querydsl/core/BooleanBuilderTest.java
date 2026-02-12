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

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.ToStringVisitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BooleanBuilderTest {

  private final BooleanExpression first = BooleanConstant.TRUE;

  private final BooleanExpression second = BooleanConstant.FALSE;

  @Test
  void null_in_constructor() {
    assertThat(new BooleanBuilder(null).getValue()).isNull();
  }

  @Test
  void and_empty() {
    var builder = new BooleanBuilder();
    builder.and(new BooleanBuilder());
    assertThat(ExpressionUtils.extract(builder)).isNull();
  }

  @Test
  void and_any_of() {
    var builder = new BooleanBuilder();
    builder.andAnyOf(first, null);
    assertThat(builder.getValue()).isEqualTo(first);
  }

  @Test
  void and_any_of2() {
    var builder = new BooleanBuilder();
    builder.andAnyOf(null, first);
    assertThat(builder.getValue()).isEqualTo(first);
  }

  @Test
  void or_all_of() {
    var builder = new BooleanBuilder();
    builder.orAllOf(first, null);
    assertThat(builder.getValue()).isEqualTo(first);
  }

  @Test
  void or_all_of2() {
    var builder = new BooleanBuilder();
    builder.orAllOf(null, first);
    assertThat(builder.getValue()).isEqualTo(first);
  }

  @Test
  @Disabled
  void wrapped_booleanBuilder() {
    assertThatExceptionOfType(QueryException.class)
        .isThrownBy(
            () -> {
              new BooleanBuilder(new BooleanBuilder());
            });
  }

  @Test
  void basic() {
    //        new BooleanBuilder().and(first).or(second);
    assertThat(new BooleanBuilder().and(first).or(second)).hasToString(first.or(second).toString());
  }

  @Test
  void advanced() {
    var builder = new BooleanBuilder();
    builder.andAnyOf(first, second, first);
    builder.orAllOf(first, second, first);
    assertThat(builder).hasToString("true || false || true || true && false && true");
  }

  @Test
  void if_then_else() {
    var builder = new BooleanBuilder();
    builder.and(null);
    builder.or(null);
    builder.and(second);
    assertThat(builder.getValue()).isEqualTo(second);
  }

  @Test
  void and_null_supported() {
    assertThat(first.and(null)).isEqualTo(first);
  }

  @Test
  void or_null_supported() {
    assertThat(first.or(null)).isEqualTo(first);
  }

  @Test
  void and_not() {
    var builder = new BooleanBuilder();
    builder.and(first).andNot(second);
    assertThat(builder.getValue()).isEqualTo(first.and(second.not()));
  }

  @Test
  void or_not() {
    var builder = new BooleanBuilder();
    builder.and(first).orNot(second);
    assertThat(builder.getValue()).isEqualTo(first.or(second.not()));
  }

  @Test
  void not() {
    var builder = new BooleanBuilder();
    builder.and(first).not();
    assertThat(builder.getValue()).isEqualTo(first.not());
  }

  @Test
  void booleanBuilder_equals_booleanBuilder() {
    assertThat(new BooleanBuilder(first)).isEqualTo(new BooleanBuilder(first));
  }

  @Test
  void constant_equals_booleanBuilder() {
    assertThat(first).isNotEqualTo(new BooleanBuilder(first));
  }

  @Test
  void booleanBuilder_equals_constant() {
    assertThat(new BooleanBuilder(first)).isNotEqualTo(first);
  }

  @Test
  void hashCode_() {
    assertThat(new BooleanBuilder(first)).hasSameHashCodeAs(new BooleanBuilder(first));
    assertThat(new BooleanBuilder()).hasSameHashCodeAs(new BooleanBuilder());
  }

  @Test
  void toString_() {
    var builder = new BooleanBuilder().and(first);
    assertThat(builder).hasToString("true");
    builder.or(Expressions.booleanPath("condition"));
    assertThat(builder).hasToString("true || condition");
  }

  //    @Test
  //    public void getArg() {
  //        BooleanBuilder builder = new BooleanBuilder().and(first);
  //        assertEquals(first, builder.getArg(0));
  //    }
  //
  //    @Test
  //    public void getArgs() {
  //        BooleanBuilder builder = new BooleanBuilder().and(first);
  //        assertEquals(Arrays.asList(first), builder.getArgs());
  //    }

  @Test
  void accept() {
    var builder = new BooleanBuilder();
    builder.and(first);
    builder.or(Expressions.booleanPath("condition"));
    assertThat(builder.accept(ToStringVisitor.DEFAULT, Templates.DEFAULT))
        .isEqualTo("true || condition");
  }
}
