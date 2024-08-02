package com.querydsl.example.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.example.dto.Product;
import com.querydsl.example.dto.ProductL10n;
import java.util.Collections;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductDaoTest extends AbstractDaoTest {

  @Autowired SupplierDao supplierDao;

  @Autowired ProductDao productDao;

  @Test
  public void findAll() {
    var products = productDao.findAll();
    assertThat(products).isNotEmpty();
  }

  @Test
  public void findById() {
    assertThat(productDao.findById(1)).isNotNull();
  }

  @Test
  public void update() {
    var product = productDao.findById(1);
    productDao.save(product);
  }

  @Test
  public void delete() {
    var product = new Product();
    product.setSupplier(supplierDao.findById(1));
    product.setName("ProductX");
    product.setLocalizations(Collections.singleton(new ProductL10n()));
    productDao.save(product);
    assertThat(productDao.findById(product.getId())).isNotNull();
    productDao.delete(product);
    assertThat(productDao.findById(product.getId())).isNull();
  }
}
