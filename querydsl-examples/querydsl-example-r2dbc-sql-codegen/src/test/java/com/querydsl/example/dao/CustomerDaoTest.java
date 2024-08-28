package com.querydsl.example.dao;

import com.querydsl.example.dto.Customer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class CustomerDaoTest extends AbstractDaoTest {

  @Autowired CustomerDao customerDao;

  @Test
  public void findAll() {
    var setup = customerDao.findAll();

    StepVerifier.create(setup).expectNextCount(2).verifyComplete();
  }

  @Test
  public void findById() {
    var setup = customerDao.findById(testDataService.customer1);

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  public void update() {
    Mono<Customer> setup =
        customerDao
            .findById(testDataService.customer1)
            .flatMap(customer -> customerDao.save(customer));

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  public void delete() {
    Mono<Customer> setup =
        customerDao
            .findById(1)
            .flatMap(
                customer ->
                    customerDao
                        .delete(customer)
                        .flatMap(__ -> customerDao.findById(testDataService.customer1)));

    StepVerifier.create(setup).verifyComplete();
  }
}
