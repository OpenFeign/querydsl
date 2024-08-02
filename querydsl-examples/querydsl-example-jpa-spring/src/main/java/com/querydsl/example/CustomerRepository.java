package com.querydsl.example;

import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepository;
import java.util.List;

public interface CustomerRepository extends QuerydslJpaRepository<Customer, Long> {

  QCustomer C = new QCustomer("c");

  default List<Customer> findByLastName(String lastName) {
    return select(C).from(C).where(C.lastName.eq(lastName)).fetch();
  }

  default Customer findById(long id) {
    return select(C).from(C).where(C.id.eq(id)).fetchOne();
  }

  @Override
  default List<Customer> findAll() {
    return select(C).from(C).fetch();
  }
}
