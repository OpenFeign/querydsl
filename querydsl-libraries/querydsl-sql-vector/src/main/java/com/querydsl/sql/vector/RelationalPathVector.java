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
package com.querydsl.sql.vector;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.vector.VectorPath;
import com.querydsl.vector.VectorPaths;

/**
 * {@code RelationalPathVector} extends {@link RelationalPathBase} to provide factory methods for
 * vector path creation
 *
 * @param <T> entity type
 */
public class RelationalPathVector<T> extends RelationalPathBase<T> implements VectorPaths {

  private static final long serialVersionUID = 5328405932498765211L;

  public RelationalPathVector(
      Class<? extends T> type, String variable, String schema, String table) {
    this(type, PathMetadataFactory.forVariable(variable), schema, table);
  }

  public RelationalPathVector(
      Class<? extends T> type, PathMetadata metadata, String schema, String table) {
    super(type, metadata, schema, table);
  }

  @Override
  public <A> VectorPath<A> createVector(String property, Class<? extends A> type) {
    return add(new VectorPath<A>(type, forProperty(property)));
  }
}
