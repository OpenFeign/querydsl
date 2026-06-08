package com.querydsl.apt.domain;

import com.querydsl.apt.domain.custom.CustomNumber;
import com.querydsl.core.annotations.QueryEntity;
import org.junit.jupiter.api.Disabled;

@Disabled
public class NumberTest {

  @QueryEntity
  public static class Entity {

    CustomNumber customNumber;
  }
}
