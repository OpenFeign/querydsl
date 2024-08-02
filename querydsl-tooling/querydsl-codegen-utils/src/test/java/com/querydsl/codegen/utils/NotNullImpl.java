package com.querydsl.codegen.utils;

import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Annotation;

/**
 * @author tiwe
 */
@SuppressWarnings("all")
public class NotNullImpl implements NotNull {

  @Override
  public Class<?>[] groups() {
    return new Class<?>[0];
  }

  @Override
  public Class<? extends Payload>[] payload() {
    return new Class[0];
  }

  @Override
  public String message() {
    return "{javax.validation.constraints.NotNull.message}";
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return NotNull.class;
  }
}
