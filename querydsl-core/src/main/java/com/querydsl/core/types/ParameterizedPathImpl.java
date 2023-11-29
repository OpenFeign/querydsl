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
package com.querydsl.core.types;

/**
 * {@code ParameterizedPathImpl} represents {@link Path} instances with a parameterized generic type
 *
 * @param <T>
 */
public class ParameterizedPathImpl<T> extends PathImpl<T> implements ParameterizedExpression<T> {

  private static final long serialVersionUID = -498707460985111265L;

  private final Class<?>[] parameterTypes;

  public ParameterizedPathImpl(
      Class<? extends T> type, PathMetadata metadata, Class<?>... parameterTypes) {
    super(type, metadata);
    this.parameterTypes = parameterTypes;
  }

  @Override
  public Class<?> getParameter(int index) {
    return parameterTypes[index];
  }
}
