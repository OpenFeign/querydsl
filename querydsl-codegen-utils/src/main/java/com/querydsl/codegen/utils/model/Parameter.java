/*
 * Copyright 2010, Mysema Ltd
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
package com.querydsl.codegen.utils.model;

/**
 * Parameter represents a parameter in a Constructor
 *
 * @author tiwe
 */
public final class Parameter {

  private final String name;

  private final Type type;

  public Parameter(String name, Type type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof Parameter) {
      Parameter t = (Parameter) o;
      return type.equals(t.type) && name.equals(t.name);
    } else {
      return false;
    }
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return type.hashCode();
  }

  @Override
  public String toString() {
    return type + " " + name;
  }
}
