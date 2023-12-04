package com.querydsl.example.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.example.dto.Supplier;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SupplierDaoTest extends AbstractDaoTest {

  @Autowired SupplierDao supplierDao;

  @Test
  public void findAll() {
    List<Supplier> suppliers = supplierDao.findAll();
    assertThat(suppliers).isNotEmpty();
  }

  @Test
  public void findById() {
    assertThat(supplierDao.findById(1)).isNotNull();
  }

  @Test
  public void update() {
    Supplier supplier = supplierDao.findById(1);
    supplierDao.save(supplier);
  }

  @Test
  public void delete() {
    Supplier supplier = new Supplier();
    supplierDao.save(supplier);
    assertThat(supplier.getId()).isNotNull();
    supplierDao.delete(supplier);
    assertThat(supplierDao.findById(supplier.getId())).isNull();
  }
}
