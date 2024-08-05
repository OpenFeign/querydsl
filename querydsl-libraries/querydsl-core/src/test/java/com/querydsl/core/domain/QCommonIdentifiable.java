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
package com.querydsl.core.domain;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimplePath;
import java.io.Serializable;

/** QCommonIdentifiable is a Querydsl query type for CommonIdentifiable */
public class QCommonIdentifiable
    extends EntityPathBase<CommonIdentifiable<? extends Serializable>> {

  private static final long serialVersionUID = 1818647030;

  public static final QCommonIdentifiable commonIdentifiable =
      new QCommonIdentifiable("commonIdentifiable");

  public final QCommonPersistence _super = new QCommonPersistence(this);

  public final SimplePath<Serializable> id = createSimple("id", java.io.Serializable.class);

  // inherited
  public final NumberPath<Long> version = _super.version;

  @SuppressWarnings("unchecked")
  public QCommonIdentifiable(String variable) {
    super((Class) CommonIdentifiable.class, forVariable(variable));
  }

  public QCommonIdentifiable(
      BeanPath<? extends CommonIdentifiable<? extends Serializable>> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  @SuppressWarnings("unchecked")
  public QCommonIdentifiable(PathMetadata metadata) {
    super((Class) CommonIdentifiable.class, metadata);
  }
}
