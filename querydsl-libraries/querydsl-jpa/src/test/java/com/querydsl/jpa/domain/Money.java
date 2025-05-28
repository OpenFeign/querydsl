package com.querydsl.jpa.domain;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Objects;

public class Money extends Number implements Comparable<Money> {

  @Serial private static final long serialVersionUID = 1L;

  private final BigDecimal value;

  public Money(BigDecimal value) {
    this.value = value;
  }

  public Money(String value) {
    this.value = new BigDecimal(value);
  }

  public BigDecimal getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value.toPlainString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Money)) return false;
    Money that = (Money) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public long longValue() {
    return value.longValue();
  }

  @Override
  public int intValue() {
    return value.intValue();
  }

  @Override
  public float floatValue() {
    return value.floatValue();
  }

  @Override
  public double doubleValue() {
    return value.doubleValue();
  }

  @Override
  public int compareTo(Money o) {
    return this.value.compareTo(o.value);
  }
}
