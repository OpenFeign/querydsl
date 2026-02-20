package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.junit.Test;

public class InitTest {

  @Entity
  public static class User {

    @ManyToOne(fetch = FetchType.EAGER)
    private Address address;
  }

  @Entity
  public static class Address extends AddressBase {}

  @MappedSuperclass
  public abstract static class AddressBase {

    @Id private long idAddress;

    @Id private int numVersion;

    @ManyToOne private City city;
  }

  @Entity
  public static class City {}

  @Test
  public void test() {
    assertThat(QInitTest_User.user.address.city).isNotNull();
  }
}
