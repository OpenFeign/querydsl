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
import org.geolatte.geom.LineString;

/**
 * {@code LineStringPath} extends {@link LineStringExpression} to implement the {@link Path}
 * interface
 *
 * @author tiwe
 * @param <T>
 */
public class LineStringPath<T extends LineString> extends LineStringExpression<T>
    implements Path<T> {

  private static final long serialVersionUID = 312776751843333543L;

  private final PathImpl<T> pathMixin;

  @SuppressWarnings("unchecked")
  public LineStringPath(Path<?> parent, String property) {
    this((Class<? extends T>) LineString.class, parent, property);
  }

  public LineStringPath(Class<? extends T> type, Path<?> parent, String property) {
    this(type, PathMetadataFactory.forProperty(parent, property));
  }

  @SuppressWarnings("unchecked")
  public LineStringPath(PathMetadata metadata) {
    this((Class<? extends T>) LineString.class, metadata);
  }

  public LineStringPath(Class<? extends T> type, PathMetadata metadata) {
    super(ExpressionUtils.path(type, metadata));
    this.pathMixin = (PathImpl<T>) mixin;
  }

  @SuppressWarnings("unchecked")
  public LineStringPath(String var) {
    this((Class<? extends T>) LineString.class, PathMetadataFactory.forVariable(var));
  }

  @Override
  public final <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(pathMixin, context);
  }

  public LineStringPath(Class<? extends T> type, String var) {
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
