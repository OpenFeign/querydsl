/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fluentq.mongodb.document;

import fluentq.core.support.QueryMixin;
import fluentq.core.types.ExpressionUtils;
import fluentq.core.types.Path;
import fluentq.core.types.Predicate;
import fluentq.mongodb.MongodbOps;
import java.util.Collection;

/**
 * {@code AnyEmbeddedBuilder} is a builder for constraints on embedded objects
 *
 * @param <Q> query type
 * @author Mark Paluch
 */
public class AnyEmbeddedBuilder<Q extends AbstractMongodbQuery<Q>> {

  private final QueryMixin<Q> queryMixin;

  private final Path<? extends Collection<?>> collection;

  public AnyEmbeddedBuilder(QueryMixin<Q> queryMixin, Path<? extends Collection<?>> collection) {
    this.queryMixin = queryMixin;
    this.collection = collection;
  }

  public Q on(Predicate... conditions) {
    return queryMixin.where(
        ExpressionUtils.predicate(
            MongodbOps.ELEM_MATCH, collection, ExpressionUtils.allOf(conditions)));
  }
}
