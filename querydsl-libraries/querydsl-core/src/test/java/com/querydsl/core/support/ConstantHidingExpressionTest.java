package com.querydsl.core.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class ConstantHidingExpressionTest {

  @Test
  public void constants_hidden() {
    FactoryExpression<Tuple> tuple =
        Projections.tuple(
            Expressions.stringPath("str"),
            Expressions.TRUE,
            Expressions.FALSE.as("false"),
            Expressions.constant(1));
    FactoryExpression<Tuple> wrapped = new ConstantHidingExpression<>(tuple);
    assertThat(wrapped.getArgs()).hasSize(1);
    Tuple t = wrapped.newInstance("s");

    assertThat(t.get(Expressions.stringPath("str"))).isEqualTo("s");
    assertThat(t.get(Expressions.TRUE)).isEqualTo(Boolean.TRUE);
    assertThat(t.get(Expressions.FALSE.as("false"))).isEqualTo(Boolean.FALSE);
    assertThat(t.get(Expressions.constant(1))).isEqualTo(Integer.valueOf(1));
  }
}
