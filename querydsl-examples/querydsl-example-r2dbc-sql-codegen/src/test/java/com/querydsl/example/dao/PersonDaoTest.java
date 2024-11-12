package com.querydsl.example.dao;

import com.querydsl.example.dto.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class PersonDaoTest extends AbstractDaoTest {

  @Autowired PersonDao personDao;

  @Test
  void findAll() {
    var setup = personDao.findAll();

    StepVerifier.create(setup).expectNextCount(2).verifyComplete();
  }

  @Test
  void findById() {
    var setup = personDao.findById(testDataService.person1);

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  void update() {
    Mono<Person> setup =
        personDao.findById(testDataService.person1).flatMap(p -> personDao.save(p));

    StepVerifier.create(setup).expectNextCount(1).verifyComplete();
  }

  @Test
  void delete() {
    var person = new Person();
    person.setEmail("john@acme.com");

    Mono<Person> setup =
        personDao
            .save(person)
            .flatMap(
                s -> personDao.delete(person).flatMap(__ -> personDao.findById(person.getId())));

    StepVerifier.create(setup).verifyComplete();
  }
}
