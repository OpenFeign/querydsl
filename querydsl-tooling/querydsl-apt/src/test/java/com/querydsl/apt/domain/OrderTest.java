package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;
import org.junit.Test;

public class OrderTest {

  @Entity
  public static class Order {
    @OneToMany(targetEntity = OrderItemImpl.class)
    List<OrderItem> orderItems;
  }

  @Entity
  public interface OrderItem {}

  @Entity
  public static class OrderItemImpl implements OrderItem {}

  @Test
  public void test() {
    assertThat(QOrderTest_Order.order.orderItems.any().getClass())
        .isEqualTo(QOrderTest_OrderItemImpl.class);
  }
}
