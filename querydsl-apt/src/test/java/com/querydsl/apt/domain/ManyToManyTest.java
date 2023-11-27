package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import org.junit.Test;

public class ManyToManyTest {

  public interface PhoneNumber {}

  @Entity
  public static class PhoneNumberImpl {}

  @Entity
  public static class Person {

    @ManyToMany(targetEntity = PhoneNumberImpl.class)
    Set<PhoneNumber> phones;
  }

  @Test
  public void test() {
    assertThat(QManyToManyTest_Person.person.phones.getElementType())
        .isEqualTo(PhoneNumberImpl.class);
  }
}
