package com.querydsl.example.dao;

import static org.assertj.core.api.Assertions.assertThat;
import com.querydsl.example.dto.Customer;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerDaoTest extends AbstractDaoTest {

  @Autowired CustomerDao customerDao;

  @Test
  public void findAll() {
    List<Customer> customers = customerDao.findAll();
    assertThat(customers).isNotEmpty();
  }

  @Test
  public void findById() {
    assertThat(customerDao.findById(1)).isNotNull();
  }

  @Test
  public void update() {
    Customer customer = customerDao.findById(1);
    customerDao.save(customer);
  }

  @Test
  public void delete() {
    Customer customer = customerDao.findById(1);
    customerDao.delete(customer);
    assertThat(customerDao.findById(1)).isNull();
  }
}
