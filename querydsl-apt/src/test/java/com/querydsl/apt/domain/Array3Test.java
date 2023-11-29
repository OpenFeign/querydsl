package com.querydsl.apt.domain;

import static org.junit.Assert.assertEquals;

import com.querydsl.core.annotations.QueryEntity;
import org.junit.Assert;
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
    Assert.assertEquals(byte[].class, QArray3Test_Domain.domain.bytes.getType());
    assertEquals(Byte[].class, QArray3Test_Domain.domain.bytes2.getType());
  }

  @Test
  public void domain2() {
    Assert.assertEquals(byte[].class, QArray3Test_Domain2.domain2.bytes.getType());
  }

  @Test
  public void domain3() {
    Assert.assertEquals(Byte[].class, QArray3Test_Domain3.domain3.bytes.getType());
  }
}
