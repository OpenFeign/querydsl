package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.core.types.dsl.NumberPath;
import java.util.Collections;
import org.junit.Test;

public class MathTest {

  private NumberPath<Double> num = Expressions.numberPath(Double.class, "num");

  @Test
  public void math() {
    Expression<Double> expr = num;

    assertThat(unique(MathExpressions.acos(expr))).isCloseTo(Math.acos(0.5), within(0.001));
    assertThat(unique(MathExpressions.asin(expr))).isCloseTo(Math.asin(0.5), within(0.001));
    assertThat(unique(MathExpressions.atan(expr))).isCloseTo(Math.atan(0.5), within(0.001));
    assertThat(unique(MathExpressions.cos(expr))).isCloseTo(Math.cos(0.5), within(0.001));
    assertThat(unique(MathExpressions.cosh(expr))).isCloseTo(Math.cosh(0.5), within(0.001));
    assertThat(unique(MathExpressions.cot(expr))).isCloseTo(cot(0.5), within(0.001));
    assertThat(unique(MathExpressions.coth(expr))).isCloseTo(coth(0.5), within(0.001));
    assertThat(unique(MathExpressions.degrees(expr))).isCloseTo(degrees(0.5), within(0.001));
    assertThat(unique(MathExpressions.exp(expr))).isCloseTo(Math.exp(0.5), within(0.001));
    assertThat(unique(MathExpressions.ln(expr))).isCloseTo(Math.log(0.5), within(0.001));
    assertThat(unique(MathExpressions.log(expr, 10))).isCloseTo(log(0.5, 10), within(0.001));
    assertThat(unique(MathExpressions.power(expr, 2))).isCloseTo(0.25, within(0.001));
    assertThat(unique(MathExpressions.radians(expr))).isCloseTo(radians(0.5), within(0.001));
    assertThat(unique(MathExpressions.sign(expr))).isEqualTo(Integer.valueOf(1));
    assertThat(unique(MathExpressions.sin(expr))).isCloseTo(Math.sin(0.5), within(0.001));
    assertThat(unique(MathExpressions.sinh(expr))).isCloseTo(Math.sinh(0.5), within(0.001));
    assertThat(unique(MathExpressions.tan(expr))).isCloseTo(Math.tan(0.5), within(0.001));
    assertThat(unique(MathExpressions.tanh(expr))).isCloseTo(Math.tanh(0.5), within(0.001));
  }

  private double cot(double x) {
    return Math.cos(x) / Math.sin(x);
  }

  private double coth(double x) {
    return Math.cosh(x) / Math.sinh(x);
  }

  private double degrees(double x) {
    return x * 180.0 / Math.PI;
  }

  private double radians(double x) {
    return x * Math.PI / 180.0;
  }

  private double log(double x, int y) {
    return Math.log(x) / Math.log(y);
  }

  private <T> T unique(Expression<T> expr) {
    // return query().fetchOne(expr);
    return CollQueryFactory.<Double>from(num, Collections.singletonList(0.5))
        .select(expr)
        .fetchOne();
  }
}
