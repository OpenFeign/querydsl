package com.querydsl.example.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.example.dto.CustomerPaymentMethod;
import com.querydsl.example.dto.Order;
import com.querydsl.example.dto.OrderProduct;
import java.util.Collections;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderDaoTest extends AbstractDaoTest {

  @Autowired OrderDao orderDao;

  @Test
  public void findAll() {
    var orders = orderDao.findAll();
    assertThat(orders).isNotEmpty();
  }

  @Test
  public void findById() {
    assertThat(orderDao.findById(1)).isNotNull();
  }

  @Test
  public void update() {
    var order = orderDao.findById(1);
    orderDao.save(order);
  }

  @Test
  public void delete() {
    var orderProduct = new OrderProduct();
    orderProduct.setProductId(1L);
    orderProduct.setQuantity(1);

    // FIXME
    var paymentMethod = new CustomerPaymentMethod();

    var order = new Order();
    order.setCustomerPaymentMethod(paymentMethod);
    order.setOrderProducts(Collections.singleton(orderProduct));
    orderDao.save(order);
    assertThat(order.getId()).isNotNull();
    orderDao.delete(order);
    assertThat(orderDao.findById(order.getId())).isNull();
  }
}
