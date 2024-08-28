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
package com.querydsl.core.domain.query;

import com.querydsl.core.domain.CommonPersistence;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.NumberPath;

/** QCommonPersistence is a Querydsl query type for CommonPersistence */
public class QCommonPersistence extends BeanPath<CommonPersistence>
    implements EntityPath<CommonPersistence> {

  private static final long serialVersionUID = -1494672641;

  public final NumberPath<Long> version = createNumber("version", Long.class);

  public QCommonPersistence(BeanPath<? extends CommonPersistence> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QCommonPersistence(PathMetadata metadata) {
    super(CommonPersistence.class, metadata);
  }

  @Override
  public Object getMetadata(Path<?> property) {
    return null;
  }
}
