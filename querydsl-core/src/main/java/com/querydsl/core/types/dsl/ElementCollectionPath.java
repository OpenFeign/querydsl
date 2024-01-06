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

/**
 * {@code ElementCollectionPath} represents collection paths annotated with {@link
 * javax.persistence.ElementCollection}
 *
 * @param <E> component type
 * @param <Q> component query type
 */
public class ElementCollectionPath<E, Q extends SimpleExpression<? super E>>
    extends CollectionPath<E, Q> {

  public ElementCollectionPath(
      Class<? super E> type, Class<Q> queryType, Path<?> parent, String property) {
    super(type, queryType, parent, property);
  }

  public ElementCollectionPath(
      Class<? super E> type, Class<Q> queryType, PathMetadata metadata, PathInits inits) {
    super(type, queryType, metadata, inits);
  }

  public ElementCollectionPath(Class<? super E> type, Class<Q> queryType, PathMetadata metadata) {
    super(type, queryType, metadata);
  }

  public ElementCollectionPath(Class<? super E> type, Class<Q> queryType, String variable) {
    super(type, queryType, variable);
  }
}
