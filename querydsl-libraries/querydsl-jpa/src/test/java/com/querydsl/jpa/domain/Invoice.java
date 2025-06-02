package com.querydsl.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Comparator;
import java.util.UUID;

@Entity
public class Invoice {

  @Id private UUID id;

  @Column(nullable = false)
  private String category;

  @Column(nullable = false, precision = 30, scale = 10)
  @Convert(converter = MoneyConverter.class)
  private Money amount;

  public Invoice() {
    // JPA 스펙용
  }

  public Invoice(String uuid, String category, Money amount) {
    this.id = UUID.fromString(uuid);
    this.category = category;
    this.amount = amount;
  }

  public static Comparator<Invoice> comparator() {
    return Comparator.comparing(Invoice::getId)
        .thenComparing(Invoice::getCategory)
        .thenComparing(Invoice::getAmount);
  }

  @Override
  public String toString() {
    return "Invoice{" + "id=" + id + ", category='" + category + '\'' + ", amount=" + amount + '}';
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Money getAmount() {
    return amount;
  }

  public void setAmount(Money amount) {
    this.amount = amount;
  }
}
