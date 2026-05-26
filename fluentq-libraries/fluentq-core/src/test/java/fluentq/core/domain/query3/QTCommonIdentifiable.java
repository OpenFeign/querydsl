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
package fluentq.core.domain.query3;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.domain.CommonIdentifiable;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.BeanPath;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.SimplePath;
import java.io.Serial;
import java.io.Serializable;

/** QCommonIdentifiable is a FluentQ query type for CommonIdentifiable */
public class QTCommonIdentifiable
    extends EntityPathBase<CommonIdentifiable<? extends Serializable>> {

  @Serial private static final long serialVersionUID = 1818647030;

  public static final QTCommonIdentifiable commonIdentifiable =
      new QTCommonIdentifiable("commonIdentifiable");

  public final QTCommonPersistence _super = new QTCommonPersistence(this);

  public final SimplePath<java.io.Serializable> id = createSimple("id", java.io.Serializable.class);

  // inherited
  public final NumberPath<Long> version = _super.version;

  @SuppressWarnings("unchecked")
  public QTCommonIdentifiable(String variable) {
    super((Class) CommonIdentifiable.class, forVariable(variable));
  }

  public QTCommonIdentifiable(
      BeanPath<? extends CommonIdentifiable<? extends java.io.Serializable>> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  @SuppressWarnings("unchecked")
  public QTCommonIdentifiable(PathMetadata metadata) {
    super((Class) CommonIdentifiable.class, metadata);
  }
}
