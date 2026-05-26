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
package fluentq.core.domain.query;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.domain.MyEmbeddable;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.BeanPath;
import fluentq.core.types.dsl.NumberPath;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QMyEmbeddable is a FluentQ query type for MyEmbeddable */
@Generated("fluentq.codegen.EmbeddableSerializer")
public class QMyEmbeddable extends BeanPath<MyEmbeddable> {

  @Serial private static final long serialVersionUID = -968265626;

  public static final QMyEmbeddable myEmbeddable = new QMyEmbeddable("myEmbeddable");

  public final NumberPath<Integer> foo = createNumber("foo", Integer.class);

  public QMyEmbeddable(String variable) {
    super(MyEmbeddable.class, forVariable(variable));
  }

  public QMyEmbeddable(BeanPath<? extends MyEmbeddable> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QMyEmbeddable(PathMetadata metadata) {
    super(MyEmbeddable.class, metadata);
  }
}
