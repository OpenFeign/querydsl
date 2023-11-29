package com.querydsl.apt.domain;

import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class Array2Test {

  public static class Example {

    byte[] imageData;

    @QueryProjection
    public Example(byte[] param0) {
      this.imageData = param0;
    }
  }

  @Test
  public void test() {
    new QArray2Test_Example(Expressions.path(byte[].class, "bytes")).newInstance(new byte[0]);
  }
}
