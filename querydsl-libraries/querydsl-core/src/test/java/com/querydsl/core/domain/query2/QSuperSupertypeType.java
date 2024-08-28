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
package com.querydsl.core.domain.query2;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.domain.SuperSupertype;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;

/** QSuperSupertype is a Querydsl query type for SuperSupertype */
public class QSuperSupertypeType extends EntityPathBase<SuperSupertype> {

  private static final long serialVersionUID = 518341775;

  public static final QSuperSupertypeType superSupertype =
      new QSuperSupertypeType("superSupertype");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final NumberPath<Long> version = createNumber("version", Long.class);

  public QSuperSupertypeType(String variable) {
    super(SuperSupertype.class, forVariable(variable));
  }

  public QSuperSupertypeType(BeanPath<? extends SuperSupertype> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QSuperSupertypeType(PathMetadata metadata) {
    super(SuperSupertype.class, metadata);
  }
}
