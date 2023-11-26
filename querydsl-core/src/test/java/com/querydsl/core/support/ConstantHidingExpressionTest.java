package com.querydsl.core.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    FactoryExpression<Tuple> wrapped = new ConstantHidingExpression<Tuple>(tuple);
    assertEquals(1, wrapped.getArgs().size());
    Tuple t = wrapped.newInstance("s");

    assertEquals("s", t.get(Expressions.stringPath("str")));
    assertEquals(Boolean.TRUE, t.get(Expressions.TRUE));
    assertEquals(Boolean.FALSE, t.get(Expressions.FALSE.as("false")));
    assertEquals(Integer.valueOf(1), t.get(Expressions.constant(1)));
  }
}
