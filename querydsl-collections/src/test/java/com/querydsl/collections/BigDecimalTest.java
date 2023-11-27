package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;

public class BigDecimalTest {

  @Test
  public void arithmetic() {
    NumberPath<BigDecimal> num = Expressions.numberPath(BigDecimal.class, "num");
    CollQuery<BigDecimal> query =
        CollQueryFactory.<BigDecimal>from(
            num, Arrays.<BigDecimal>asList(BigDecimal.ONE, BigDecimal.valueOf(2)));
    assertThat(query.<BigDecimal>select(num.add(BigDecimal.TEN)).fetch())
        .isEqualTo(Arrays.asList(BigDecimal.valueOf(11), BigDecimal.valueOf(12)));
    assertThat(query.<BigDecimal>select(num.subtract(BigDecimal.TEN)).fetch())
        .isEqualTo(Arrays.asList(BigDecimal.valueOf(-9), BigDecimal.valueOf(-8)));
    assertThat(query.<BigDecimal>select(num.multiply(BigDecimal.TEN)).fetch())
        .isEqualTo(Arrays.asList(BigDecimal.valueOf(10), BigDecimal.valueOf(20)));
    assertThat(query.<BigDecimal>select(num.divide(BigDecimal.TEN)).fetch())
        .isEqualTo(Arrays.asList(new BigDecimal("0.1"), new BigDecimal("0.2")));
  }
}
