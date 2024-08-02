package com.querydsl.example.dao;

import com.querydsl.example.dto.Supplier;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class SupplierDaoTest extends AbstractDaoTest {

  @Autowired SupplierDao supplierDao;

  @Test
  public void findAll() {
    var setup = supplierDao.findAll();

    StepVerifier.create(setup).expectNextCount(2).verifyComplete();
  }

  @Test
  public void findById() {
    var setup = supplierDao.findById(testDataService.supplier1);

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  public void update() {
    Mono<Supplier> setup =
        supplierDao
            .findById(testDataService.supplier1)
            .flatMap(supplier -> supplierDao.save(supplier));

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  public void delete() {
    var supplier = new Supplier();
    Mono<Supplier> setup =
        supplierDao
            .save(supplier)
            .flatMap(
                s ->
                    supplierDao
                        .delete(supplier)
                        .flatMap(__ -> supplierDao.findById(supplier.getId())));

    StepVerifier.create(setup).verifyComplete();
  }
}
