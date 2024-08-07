/*
 * Copyright 2020, The Querydsl Team (http://www.querydsl.com/team)
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
package com.querydsl.core.group;

import static com.querydsl.core.util.TupleUtils.toTuple;

import com.querydsl.core.FetchableQuery;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionUtils;
import com.querydsl.core.types.Projections;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Provides aggregated results as a collection
 *
 * @param <K> Key type
 * @param <V> Value type
 * @param <RES> Concrete collection type
 * @author Jan-Willem Gmelig Meyling
 */
public class GroupByGenericCollection<K, V, RES extends Collection<V>>
    extends AbstractGroupByTransformer<K, RES> {

  private final Supplier<RES> resultFactory;

  GroupByGenericCollection(
      Supplier<RES> resultFactory, Expression<K> key, Expression<?>... expressions) {
    super(key, expressions);
    this.resultFactory = resultFactory;
  }

  @Override
  public RES transform(FetchableQuery<?, ?> query) {
    // create groups
    FactoryExpression<Tuple> expr = FactoryExpressionUtils.wrap(Projections.tuple(expressions));
    var hasGroups = false;
    for (Expression<?> e : expr.getArgs()) {
      hasGroups |= e instanceof GroupExpression;
    }
    if (hasGroups) {
      expr = withoutGroupExpressions(expr);
    }
    try (final var iter = query.select(expr).iterate(); ) {

      var list = resultFactory.get();
      GroupImpl group = null;
      K groupId = null;
      while (iter.hasNext()) {
        var tuple = toTuple(iter.next(), expressions);
        @SuppressWarnings("unchecked") // This type is mandated by the key type
        var row = (K[]) tuple.toArray();
        if (group == null) {
          group = new GroupImpl(groupExpressions, maps);
          groupId = row[0];
        } else if (!Objects.equals(groupId, row[0])) {
          list.add(transform(group));
          group = new GroupImpl(groupExpressions, maps);
          groupId = row[0];
        }
        group.add(row);
      }
      if (group != null) {
        list.add(transform(group));
      }
      return list;
    }
  }

  @SuppressWarnings("unchecked")
  protected V transform(Group group) {
    return (V) group;
  }
}
