package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;

public class NumberTest {

  @Test
  public void sum() throws Exception {
    NumberPath<BigDecimal> num = Expressions.numberPath(BigDecimal.class, "num");
    CollQuery<BigDecimal> query =
        CollQueryFactory.<BigDecimal>from(
            num, Arrays.<BigDecimal>asList(new BigDecimal("1.6"), new BigDecimal("1.3")));

    assertThat(query.<BigDecimal>select(num.sumBigDecimal()).fetchOne())
        .isEqualTo(new BigDecimal("2.9"));
  }
}
