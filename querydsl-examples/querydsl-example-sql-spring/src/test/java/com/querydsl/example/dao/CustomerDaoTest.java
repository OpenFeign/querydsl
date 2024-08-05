package com.querydsl.example.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerDaoTest extends AbstractDaoTest {

  @Autowired CustomerDao customerDao;

  @Test
  public void findAll() {
    var customers = customerDao.findAll();
    assertThat(customers).isNotEmpty();
  }

  @Test
  public void findById() {
    assertThat(customerDao.findById(1)).isNotNull();
  }

  @Test
  public void update() {
    var customer = customerDao.findById(1);
    customerDao.save(customer);
  }

  @Test
  public void delete() {
    var customer = customerDao.findById(1);
    customerDao.delete(customer);
    assertThat(customerDao.findById(1)).isNull();
  }
}
