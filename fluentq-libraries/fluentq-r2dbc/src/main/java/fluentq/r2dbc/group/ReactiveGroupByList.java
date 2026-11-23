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
package fluentq.r2dbc.group;

import fluentq.core.ReactiveFetchableQuery;
import fluentq.core.Tuple;
import fluentq.core.group.Group;
import fluentq.core.group.GroupExpression;
import fluentq.core.group.GroupImpl;
import fluentq.core.types.Expression;
import fluentq.core.types.FactoryExpression;
import fluentq.core.types.FactoryExpressionUtils;
import fluentq.core.types.Projections;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import reactor.core.publisher.Flux;

/**
 * Provides aggregated results as a list
 *
 * @param <K>
 * @param <V>
 * @author mc_fish
 */
public class ReactiveGroupByList<K, V> extends ReactiveAbstractGroupByTransformer<K, V> {

  ReactiveGroupByList(Expression<K> key, Expression<?>... expressions) {
    super(key, expressions);
  }

  @Override
  public Flux<V> transform(ReactiveFetchableQuery<?, ?> query) {
    // create groups
    FactoryExpression<Tuple> expr = FactoryExpressionUtils.wrap(Projections.tuple(expressions));
    var hasGroups = false;
    for (Expression<?> e : expr.getArgs()) {
      hasGroups |= e instanceof GroupExpression;
    }
    if (hasGroups) {
      expr = withoutGroupExpressions(expr);
    }
    final var result = query.select(expr).fetch();

    return result
        .collectList()
        .flatMapMany(
            tuples -> {
              List<V> list = new ArrayList<>();
              GroupImpl group = null;
              K groupId = null;

              for (Tuple tuple : tuples) {
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

              return Flux.fromIterable(list);
            });
  }

  @SuppressWarnings("unchecked")
  protected V transform(Group group) {
    return (V) group;
  }
}
