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

/**
 * {@code BooleanPath} represents boolean path expressions
 *
 * @author tiwe
 * @see java.lang.Boolean
 */
public class BooleanPath extends BooleanExpression implements Path<Boolean> {

  private static final long serialVersionUID = 6590516706769430565L;

  private final PathImpl<Boolean> pathMixin;

  protected BooleanPath(PathImpl<Boolean> mixin) {
    super(mixin);
    this.pathMixin = mixin;
  }

  protected BooleanPath(Path<?> parent, String property) {
    this(PathMetadataFactory.forProperty(parent, property));
  }

  protected BooleanPath(PathMetadata metadata) {
    super(ExpressionUtils.path(Boolean.class, metadata));
    this.pathMixin = (PathImpl<Boolean>) mixin;
  }

  protected BooleanPath(String var) {
    this(PathMetadataFactory.forVariable(var));
  }

  @Override
  public final <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(this, context);
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
