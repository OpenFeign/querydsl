package com.querydsl.example.dao;

import com.querydsl.example.dto.Product;
import com.querydsl.example.dto.ProductL10n;
import java.util.Set;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ProductDaoTest extends AbstractDaoTest {

  @Autowired SupplierDao supplierDao;

  @Autowired ProductDao productDao;

  @Test
  public void findAll() {
    Flux<Product> setup = productDao.findAll();

    StepVerifier.create(setup).expectNextCount(2).verifyComplete();
  }

  @Test
  public void findById() {
    Mono<Product> setup = productDao.findById(testDataService.product1);

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  public void update() {
    Mono<Product> setup =
        productDao.findById(testDataService.product1).flatMap(p -> productDao.save(p));

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  public void delete() {
    Product product = new Product();
    product.setName("ProductX");
    product.setLocalizations(Set.of(new ProductL10n()));

    Mono<Product> setup =
        supplierDao
            .findById(1)
            .flatMap(
                supplier -> {
                  product.setSupplier(supplier);
                  return productDao
                      .save(product)
                      .flatMap(
                          s ->
                              productDao
                                  .delete(product)
                                  .flatMap(__ -> productDao.findById(product.getId())));
                });

    StepVerifier.create(setup).verifyComplete();
  }
}
