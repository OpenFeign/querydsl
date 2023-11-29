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

import com.querydsl.core.annotations.Immutable;
import com.querydsl.core.util.ReflectionUtils;
import java.lang.reflect.AnnotatedElement;
import org.jetbrains.annotations.Nullable;

/**
 * {@code PathImpl} defines a default implementation of the {@link Path} interface
 *
 * @author tiwe
 * @param <T>
 */
@Immutable
public class PathImpl<T> extends ExpressionBase<T> implements Path<T> {

  private static final long serialVersionUID = -2498447742798348162L;

  private final PathMetadata metadata;

  private final Path<?> root;

  @Nullable private transient AnnotatedElement annotatedElement;

  protected PathImpl(Class<? extends T> type, String variable) {
    this(type, PathMetadataFactory.forVariable(variable));
  }

  protected PathImpl(Class<? extends T> type, PathMetadata metadata) {
    super(type);
    this.metadata = metadata;
    this.root = metadata.getRootPath() != null ? metadata.getRootPath() : this;
  }

  protected PathImpl(Class<? extends T> type, Path<?> parent, String property) {
    this(type, PathMetadataFactory.forProperty(parent, property));
  }

  @Override
  public final boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof Path<?>) {
      return ((Path<?>) o).getMetadata().equals(metadata);
    } else {
      return false;
    }
  }

  @Override
  public final PathMetadata getMetadata() {
    return metadata;
  }

  @Override
  public final Path<?> getRoot() {
    return root;
  }

  @Override
  public final AnnotatedElement getAnnotatedElement() {
    if (annotatedElement == null) {
      if (metadata.getPathType() == PathType.PROPERTY) {
        Class<?> beanClass = metadata.getParent().getType();
        String propertyName = metadata.getName();
        annotatedElement = ReflectionUtils.getAnnotatedElement(beanClass, propertyName, getType());

      } else {
        annotatedElement = getType();
      }
    }
    return annotatedElement;
  }

  @Override
  public final <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(this, context);
  }
}
