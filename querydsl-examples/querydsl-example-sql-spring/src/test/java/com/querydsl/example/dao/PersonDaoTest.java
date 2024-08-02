package com.querydsl.example.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.example.dto.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonDaoTest extends AbstractDaoTest {

  @Autowired PersonDao personDao;

  @Test
  public void findAll() {
    var persons = personDao.findAll();
    assertThat(persons).isNotEmpty();
  }

  @Test
  public void findById() {
    assertThat(personDao.findById(1)).isNotNull();
  }

  @Test
  public void update() {
    var person = personDao.findById(1);
    personDao.save(person);
  }

  @Test
  public void delete() {
    var person = new Person();
    person.setEmail("john@acme.com");
    personDao.save(person);
    assertThat(person.getId()).isNotNull();
    personDao.delete(person);
    assertThat(personDao.findById(person.getId())).isNull();
  }
}
