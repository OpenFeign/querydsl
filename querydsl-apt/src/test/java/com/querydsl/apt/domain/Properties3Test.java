package com.querydsl.apt.domain;

import com.querydsl.core.types.dsl.DateTimePath;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.junit.Test;

public class Properties3Test extends AbstractTest {

  @Entity
  public static class Order {

    @Column(name = "order_date")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date orderDate;

    public LocalDateTime getOrderDate() {
      return orderDate != null
          ? LocalDateTime.ofInstant(orderDate.toInstant(), ZoneId.systemDefault())
          : null;
    }
  }

  @Test
  public void propertyType() throws IllegalAccessException, NoSuchFieldException {
    start(QProperties3Test_Order.class, QProperties3Test_Order.order);
    match(DateTimePath.class, "orderDate");
    matchType(java.util.Date.class, "orderDate");
  }
}
