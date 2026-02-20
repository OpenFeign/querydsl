package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.joda.money.QMoney;
import org.junit.Test;

public class JodaMoneyTest {

  @Test
  public void test() {
    var sum = QMoney.money.sum();
    assertThat(sum).isNotNull();
  }
}
