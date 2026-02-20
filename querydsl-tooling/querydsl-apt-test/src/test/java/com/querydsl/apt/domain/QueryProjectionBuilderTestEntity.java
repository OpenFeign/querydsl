package com.querydsl.apt.domain;

import com.querydsl.core.annotations.QueryProjection;

public class QueryProjectionBuilderTestEntity {
  private String property;
  private int intProperty;
  private Test test;

  @QueryProjection(useBuilder = true, builderName = "Test1")
  public QueryProjectionBuilderTestEntity(String property) {
    this.property = property;
  }

  @QueryProjection(useBuilder = true, builderName = "Test2")
  public QueryProjectionBuilderTestEntity(String property, int intProperty) {
    this.property = property;
    this.intProperty = intProperty;
  }

  @QueryProjection(useBuilder = true, builderName = "Test3")
  public QueryProjectionBuilderTestEntity(String property, int intProperty, Test test) {
    this.property = property;
    this.intProperty = intProperty;
    this.test = test;
  }

  public static class Test {
    private String property;
    private int intProperty;
  }
}
