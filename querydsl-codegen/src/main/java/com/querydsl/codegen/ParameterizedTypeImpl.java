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
package com.querydsl.codegen;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * {@code ParameterizedTypeImpl} provides an implementation of the {@link ParameterizedType}
 * interface
 *
 * @author tiwe
 */
public class ParameterizedTypeImpl implements ParameterizedType {

  private final Type rawType;

  private final Type[] arguments;

  public ParameterizedTypeImpl(Type rawType, Type[] arguments) {
    this.rawType = rawType;
    this.arguments = arguments;
  }

  @Override
  public Type[] getActualTypeArguments() {
    return arguments;
  }

  @Override
  public Type getRawType() {
    return rawType;
  }

  @Override
  public Type getOwnerType() {
    return rawType;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof ParameterizedTypeImpl) {
      ParameterizedTypeImpl other = (ParameterizedTypeImpl) o;
      return other.rawType.equals(rawType) && Arrays.equals(other.arguments, arguments);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return rawType.hashCode();
  }
}
