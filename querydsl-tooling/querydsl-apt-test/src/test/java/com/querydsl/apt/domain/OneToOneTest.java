package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import org.junit.Test;

public class OneToOneTest {

  public interface PhoneNumber {}

  @Entity
  public static class PhoneNumberImpl {}

  @Entity
  public static class Person {

    @OneToOne(targetEntity = PhoneNumberImpl.class)
    PhoneNumber phone;
  }

  @Test
  public void test() {
    assertThat(QOneToOneTest_Person.person.phone.getType()).isEqualTo(PhoneNumberImpl.class);
  }
}
