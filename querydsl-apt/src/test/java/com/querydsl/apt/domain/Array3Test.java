package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEntity;
import org.junit.Test;

public class Array3Test {

  @QueryEntity
  public static class Domain {

    byte[] bytes;

    Byte[] bytes2;
  }

  @QueryEntity
  public static class Domain2 {

    byte[] bytes;
  }

  @QueryEntity
  public static class Domain3 {

    Byte[] bytes;
  }

  @Test
  public void domain() {
    assertThat(QArray3Test_Domain.domain.bytes.getType()).isEqualTo(byte[].class);
    assertThat(QArray3Test_Domain.domain.bytes2.getType()).isEqualTo(Byte[].class);
  }

  @Test
  public void domain2() {
    assertThat(QArray3Test_Domain2.domain2.bytes.getType()).isEqualTo(byte[].class);
  }

  @Test
  public void domain3() {
    assertThat(QArray3Test_Domain3.domain3.bytes.getType()).isEqualTo(Byte[].class);
  }
}
