package com.querydsl.example.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.querydsl.example.dto.Customer;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerDaoTest extends AbstractDaoTest {

  @Autowired CustomerDao customerDao;

  @Test
  public void findAll() {
    List<Customer> customers = customerDao.findAll();
    assertFalse(customers.isEmpty());
  }

  @Test
  public void findById() {
    assertNotNull(customerDao.findById(1));
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
    assertNull(customerDao.findById(1));
  }
}
