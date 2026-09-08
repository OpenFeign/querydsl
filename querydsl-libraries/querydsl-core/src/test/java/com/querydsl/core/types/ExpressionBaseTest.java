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

import com.querydsl.core.types.dsl.Expressions;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ExpressionBaseTest {

  /**
   * Counts visitor dispatches so the memoization in {@link ExpressionBase#hashCode()} is
   * observable.
   */
  private static final class CountingConstant extends ExpressionBase<Object>
      implements Constant<Object> {

    @Serial private static final long serialVersionUID = 1L;

    private final Object constant;
    private final AtomicInteger visits = new AtomicInteger();

    CountingConstant(Object constant) {
      super(Object.class);
      this.constant = constant;
    }

    @Override
    public Object getConstant() {
      return constant;
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
      visits.incrementAndGet();
      return v.visit(this, context);
    }
  }

  @Test
  void hashCodeIsMemoized() {
    var expr = new CountingConstant("x");

    assertThat(expr.hashCode()).isEqualTo("x".hashCode());
    assertThat(expr.hashCode()).isEqualTo("x".hashCode());
    assertThat(expr.hashCode()).isEqualTo("x".hashCode());
    assertThat(expr.visits).hasValue(1);
  }

  /**
   * A computed hash of zero is indistinguishable from the "not yet computed" sentinel, so it is
   * recomputed on every call. That is the deliberate trade-off for keeping the memo a primitive;
   * closing it would cost a second field on every expression node.
   */
  @Test
  void zeroHashIsRecomputedOnEveryCall() {
    var expr = new CountingConstant(0);

    for (var i = 0; i < 5; i++) {
      assertThat(expr.hashCode()).isZero();
    }
    assertThat(expr.visits).hasValue(5);
  }

  @Test
  void ordinaryConstantsCanHashToZero() {
    assertThat(Expressions.constant(0).hashCode()).isZero();
    assertThat(Expressions.constant(0L).hashCode()).isZero();
    assertThat(Expressions.constant("").hashCode()).isZero();
  }

  /**
   * The memo is {@code transient}, so it comes back as the zero default. Zero has to mean "not yet
   * computed" for that to be safe: any other sentinel would make a deserialized expression report a
   * hash of zero forever.
   */
  @Test
  void deserializedExpressionRecomputesItsHash() throws Exception {
    var original = Expressions.constant("x");
    var expected = original.hashCode();

    var bytes = new ByteArrayOutputStream();
    try (var out = new ObjectOutputStream(bytes)) {
      out.writeObject(original);
    }
    Object copy;
    try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
      copy = in.readObject();
    }

    assertThat(copy).isEqualTo(original);
    assertThat(copy.hashCode()).isEqualTo(expected);
  }
}
