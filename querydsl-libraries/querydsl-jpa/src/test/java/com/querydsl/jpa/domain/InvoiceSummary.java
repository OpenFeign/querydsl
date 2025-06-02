package com.querydsl.jpa.domain;

import com.querydsl.core.annotations.QueryProjection;
import java.util.Objects;

public class InvoiceSummary {

  private final String category;
  private final Money totalAmount;

  @QueryProjection
  public InvoiceSummary(String category, Money totalAmount) {
    this.category = category;
    this.totalAmount = totalAmount;
  }

  public String getCategory() {
    return category;
  }

  public Money getTotalAmount() {
    return totalAmount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof InvoiceSummary)) return false;
    InvoiceSummary that = (InvoiceSummary) o;
    return Objects.equals(category, that.category) && Objects.equals(totalAmount, that.totalAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(category, totalAmount);
  }

  @Override
  public String toString() {
    return "com.querydsl.jpa.domain.InvoiceSummary{"
        + "category='"
        + category
        + '\''
        + ", totalAmount="
        + totalAmount
        + '}';
  }
}
