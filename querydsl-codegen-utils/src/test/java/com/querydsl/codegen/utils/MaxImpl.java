package com.querydsl.codegen.utils;

import jakarta.validation.Payload;
import jakarta.validation.constraints.*;
import java.lang.annotation.Annotation;

/**
 * @author tiwe
 */
@SuppressWarnings("all")
public class MaxImpl implements Max {

  private final long value;

  public MaxImpl(long value) {
    this.value = value;
  }

  @Override
  public Class<?>[] groups() {
    return new Class<?>[0];
  }

  @Override
  public String message() {
    return "{javax.validation.constraints.Max.message}";
  }

  @Override
  public Class<? extends Payload>[] payload() {
    return new Class[0];
  }

  @Override
  public long value() {
    return value;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return Max.class;
  }
}
