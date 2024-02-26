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
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.*;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * {@code ListPath} represents list paths
 *
 * @author tiwe
 * @param <E> component type
 * @param <Q> result type for {@code get(int)} and {@code any()} results
 */
public class ListPath<E, Q extends SimpleExpression<? super E>>
    extends CollectionPathBase<List<E>, E, Q> implements ListExpression<E, Q> {

  private static final long serialVersionUID = 3302301599074388860L;

  private final Map<Integer, Q> cache = new HashMap<Integer, Q>();

  private final Class<E> elementType;

  private final PathImpl<List<E>> pathMixin;

  private final Class<Q> queryType;

  @Nullable private transient Q any;

  protected ListPath(Class<? super E> elementType, Class<Q> queryType, String variable) {
    this(elementType, queryType, PathMetadataFactory.forVariable(variable));
  }

  protected ListPath(
      Class<? super E> elementType, Class<Q> queryType, Path<?> parent, String property) {
    this(elementType, queryType, PathMetadataFactory.forProperty(parent, property));
  }

  protected ListPath(Class<? super E> elementType, Class<Q> queryType, PathMetadata metadata) {
    this(elementType, queryType, metadata, PathInits.DIRECT);
  }

  @SuppressWarnings("unchecked")
  protected ListPath(
      Class<? super E> elementType, Class<Q> queryType, PathMetadata metadata, PathInits inits) {
    super(new ParameterizedPathImpl<List<E>>((Class) List.class, metadata, elementType), inits);
    this.elementType = (Class<E>) elementType;
    this.queryType = queryType;
    this.pathMixin = (PathImpl<List<E>>) mixin;
  }

  @Override
  public final <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(pathMixin, context);
  }

  @Override
  public Q any() {
    if (any == null) {
      any = newInstance(queryType, PathMetadataFactory.forCollectionAny(this));
    }
    return any;
  }

  protected PathMetadata forListAccess(int index) {
    return PathMetadataFactory.forListAccess(this, index);
  }

  protected PathMetadata forListAccess(Expression<Integer> index) {
    return PathMetadataFactory.forListAccess(this, index);
  }

  private Q create(int index) {
    PathMetadata md = forListAccess(index);
    return newInstance(queryType, md);
  }

  @Override
  public Q get(Expression<Integer> index) {
    PathMetadata md = forListAccess(index);
    return newInstance(queryType, md);
  }

  @Override
  public Q get(int index) {
    if (cache.containsKey(index)) {
      return cache.get(index);
    } else {
      Q rv = create(index);
      cache.put(index, rv);
      return rv;
    }
  }

  @Override
  public Class<E> getElementType() {
    return elementType;
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

  @Override
  public Class<?> getParameter(int index) {
    if (index == 0) {
      return elementType;
    } else {
      throw new IndexOutOfBoundsException(String.valueOf(index));
    }
  }
}
