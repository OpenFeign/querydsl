package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TemplateExpressionImplTest {

  @Test
  void equals() {
    Expression<?> expr1 = ExpressionUtils.template(String.class, "abc", "abc");
    Expression<?> expr2 = ExpressionUtils.template(String.class, "abc", "def");
    assertThat(expr1).isNotEqualTo(expr2);
  }
}
