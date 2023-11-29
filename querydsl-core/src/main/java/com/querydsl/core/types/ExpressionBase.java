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

import org.jetbrains.annotations.Nullable;

/**
 * {@code ExpressionBase} is the base class for immutable {@link Expression} implementations
 *
 * @author tiwe
 * @param <T> expression type
 */
public abstract class ExpressionBase<T> implements Expression<T> {

  private static final long serialVersionUID = -8862014178653364345L;

  private final Class<? extends T> type;

  @Nullable private transient volatile String toString;

  @Nullable private transient volatile Integer hashCode;

  public ExpressionBase(Class<? extends T> type) {
    this.type = type;
  }

  @Override
  public final Class<? extends T> getType() {
    return type;
  }

  @Override
  public final int hashCode() {
    if (hashCode == null) {
      hashCode = accept(HashCodeVisitor.DEFAULT, null);
    }
    return hashCode;
  }

  @Override
  public final String toString() {
    if (toString == null) {
      toString = accept(ToStringVisitor.DEFAULT, Templates.DEFAULT);
    }
    return toString;
  }
}
