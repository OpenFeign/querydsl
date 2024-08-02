package com.querydsl.example.dao;

import com.querydsl.example.dto.CustomerPaymentMethod;
import com.querydsl.example.dto.Order;
import com.querydsl.example.dto.OrderProduct;
import java.util.Set;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class OrderDaoTest extends AbstractDaoTest {

  @Autowired OrderDao orderDao;

  @Test
  public void findAll() {
    var setup = orderDao.findAll();

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  public void findById() {
    var setup = orderDao.findById(testDataService.order1);

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  public void update() {
    Mono<Order> setup =
        orderDao.findById(testDataService.order1).flatMap(order -> orderDao.save(order));

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  public void delete() {
    var orderProduct = new OrderProduct();
    orderProduct.setProductId(testDataService.product1);
    orderProduct.setQuantity(1);

    // FIXME
    var paymentMethod = new CustomerPaymentMethod();

    var order = new Order();
    order.setCustomerPaymentMethod(paymentMethod);
    order.setOrderProducts(Set.of(orderProduct));

    Mono<Order> setup =
        orderDao
            .save(order)
            .flatMap(s -> orderDao.delete(order).flatMap(__ -> orderDao.findById(order.getId())));

    StepVerifier.create(setup).verifyComplete();
  }
}
