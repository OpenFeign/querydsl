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
package com.querydsl.spatial;

import com.querydsl.core.types.*;
import java.lang.reflect.AnnotatedElement;
import org.geolatte.geom.Point;

/**
 * {@code PointPath} extends {@link PointExpression} to implement the {@link Path} interface
 *
 * @author tiwe
 * @param <T>
 */
public class PointPath<T extends Point> extends PointExpression<T> implements Path<T> {

  private static final long serialVersionUID = 312776751843333543L;

  private final PathImpl<T> pathMixin;

  @SuppressWarnings("unchecked")
  public PointPath(Path<?> parent, String property) {
    this((Class<? extends T>) Point.class, parent, property);
  }

  public PointPath(Class<? extends T> type, Path<?> parent, String property) {
    this(type, PathMetadataFactory.forProperty(parent, property));
  }

  @SuppressWarnings("unchecked")
  public PointPath(PathMetadata metadata) {
    this((Class<? extends T>) Point.class, metadata);
  }

  public PointPath(Class<? extends T> type, PathMetadata metadata) {
    super(ExpressionUtils.path(type, metadata));
    this.pathMixin = (PathImpl<T>) mixin;
  }

  @SuppressWarnings("unchecked")
  public PointPath(String var) {
    this((Class<? extends T>) Point.class, PathMetadataFactory.forVariable(var));
  }

  @Override
  public final <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(pathMixin, context);
  }

  public PointPath(Class<? extends T> type, String var) {
    this(type, PathMetadataFactory.forVariable(var));
  }

  @Override
  public PathMetadata getMetadata() {
    return pathMixin.getMetadata();
  }

  @Override
  public Path<?> getRoot() {
    return pathMixin.getRoot();
  }

  @Override
  public AnnotatedElement getAnnotatedElement() {
    return pathMixin.getAnnotatedElement();
  }
}
