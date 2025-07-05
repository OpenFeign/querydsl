package com.querydsl.core.alias;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

/** Functional-caching behaviour (non-concurrent) for AliasFactory. */
public class AliasFactoryCachingTest {

  private final AliasFactory factory =
      new AliasFactory(new DefaultPathFactory(), new DefaultTypeSystem());

  @Test
  public void sameExpression_returnsSameAliasInstance() {
    Expression<Object> expr = Expressions.path(Object.class, "obj");
    Object first  = factory.createAliasForExpr(Object.class, expr);
    Object second = factory.createAliasForExpr(Object.class, expr);

    assertThat(first).isSameAs(second);
  }

  @Test
  public void differentExpressions_returnDifferentAliasInstances() {
    Object first =
        factory.createAliasForExpr(Object.class, Expressions.path(Object.class, "obj1"));
    Object second =
        factory.createAliasForExpr(Object.class, Expressions.path(Object.class, "obj2"));

    assertThat(first).isNotSameAs(second);
  }
}
