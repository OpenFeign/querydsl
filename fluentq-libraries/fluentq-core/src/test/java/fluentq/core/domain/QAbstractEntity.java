/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.core.domain;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.BeanPath;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.NumberPath;
import java.io.Serial;

/** QAnimal is a FluentQ query type for Animal */
@SuppressWarnings("unchecked")
public class QAbstractEntity extends EntityPathBase<AbstractEntity> {

  @Serial private static final long serialVersionUID = 781156670;

  public static final QAbstractEntity animal = new QAbstractEntity("abstractEntity");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public QAbstractEntity(String variable) {
    super(AbstractEntity.class, forVariable(variable));
  }

  public QAbstractEntity(BeanPath<? extends AbstractEntity<?>> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QAbstractEntity(PathMetadata metadata) {
    super(AbstractEntity.class, metadata);
  }
}
