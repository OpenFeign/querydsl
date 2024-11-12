package com.querydsl.example.dao;

import com.querydsl.example.dto.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class SupplierDaoTest extends AbstractDaoTest {

  @Autowired SupplierDao supplierDao;

  @Test
  void findAll() {
    var setup = supplierDao.findAll();

    StepVerifier.create(setup).expectNextCount(2).verifyComplete();
  }

  @Test
  void findById() {
    var setup = supplierDao.findById(testDataService.supplier1);

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  void update() {
    Mono<Supplier> setup =
        supplierDao
            .findById(testDataService.supplier1)
            .flatMap(supplier -> supplierDao.save(supplier));

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  void delete() {
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
