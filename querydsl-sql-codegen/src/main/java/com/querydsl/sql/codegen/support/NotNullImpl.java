/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql.codegen.support;

import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.Annotation;

/**
 * {@code NotNullImpl} is an implementation of the {@link NotNull} interface
 *
 * @author tiwe
 */
@SuppressWarnings("all")
public class NotNullImpl implements NotNull {

  @Override
  public Class<?>[] groups() {
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

  @Override
  public Class<? extends Payload>[] payload() {
    return new Class[0];
  }
}
