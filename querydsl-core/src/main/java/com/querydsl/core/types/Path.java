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

import java.lang.reflect.AnnotatedElement;

/**
 * {@code Path} represents a path expression. Paths refer to variables, properties and collection
 * members access.
 *
 * @author tiwe
 * @param <T> expression type
 */
public interface Path<T> extends Expression<T> {

  /**
   * Get the metadata for this path
   *
   * @return path metadata
   */
  PathMetadata getMetadata();

  /**
   * Get the root for this path
   *
   * @return root of path
   */
  Path<?> getRoot();

  /**
   * Return the annotated element related to the given path
   *
   * <p>For property paths the annotated element contains the annotations of the related field
   * and/or getter method and for all others paths the annotated element is the expression type.
   *
   * @return annotated element
   */
  AnnotatedElement getAnnotatedElement();
}
