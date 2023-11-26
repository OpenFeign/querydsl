package com.querydsl.apt.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.querydsl.core.types.dsl.NumberExpression;
import java.math.BigDecimal;
import org.joda.money.QMoney;
import org.junit.Test;

public class JodaMoneyTest {

  @Test
  public void test() {
    NumberExpression<BigDecimal> sum = QMoney.money.sum();
    assertNotNull(sum);
  }
}
