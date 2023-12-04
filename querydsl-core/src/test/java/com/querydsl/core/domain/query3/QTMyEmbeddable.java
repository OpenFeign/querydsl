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
package com.querydsl.core.domain.query3;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.domain.MyEmbeddable;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.NumberPath;
import jakarta.annotation.Generated;

/** QMyEmbeddable is a Querydsl query type for MyEmbeddable */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QTMyEmbeddable extends BeanPath<MyEmbeddable> {

  private static final long serialVersionUID = -968265626;

  public static final QTMyEmbeddable myEmbeddable = new QTMyEmbeddable("myEmbeddable");

  public final NumberPath<Integer> foo = createNumber("foo", Integer.class);

  public QTMyEmbeddable(String variable) {
    super(MyEmbeddable.class, forVariable(variable));
  }

  public QTMyEmbeddable(BeanPath<? extends MyEmbeddable> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QTMyEmbeddable(PathMetadata metadata) {
    super(MyEmbeddable.class, metadata);
  }
}
