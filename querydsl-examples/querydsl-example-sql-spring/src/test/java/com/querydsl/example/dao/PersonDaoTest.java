package com.querydsl.example.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.example.dto.Person;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonDaoTest extends AbstractDaoTest {

  @Autowired PersonDao personDao;

  @Test
  public void findAll() {
    List<Person> persons = personDao.findAll();
    assertThat(persons).isNotEmpty();
  }

  @Test
  public void findById() {
    assertThat(personDao.findById(1)).isNotNull();
  }

  @Test
  public void update() {
    Person person = personDao.findById(1);
    personDao.save(person);
  }

  @Test
  public void delete() {
    Person person = new Person();
    person.setEmail("john@acme.com");
    personDao.save(person);
    assertThat(person.getId()).isNotNull();
    personDao.delete(person);
    assertThat(personDao.findById(person.getId())).isNull();
  }
}
