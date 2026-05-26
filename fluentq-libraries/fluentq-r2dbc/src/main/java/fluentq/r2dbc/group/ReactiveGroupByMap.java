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
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import reactor.core.publisher.Mono;

/**
 * Provides aggregated results as a map
 *
 * @param <K>
 * @param <V>
 * @author mc_fish
 */
@IgnoreJRERequirement
public class ReactiveGroupByMap<K, V> extends ReactiveAbstractGroupByTransformer<K, Map<K, V>> {

  ReactiveGroupByMap(Expression<K> key, Expression<?>... expressions) {
    super(key, expressions);
  }

  @Override
  public Mono<Map<K, V>> transform(ReactiveFetchableQuery<?, ?> query) {
    final Map<K, Group> groups = new LinkedHashMap<>();

    // create groups
    FactoryExpression<Tuple> expr = FactoryExpressionUtils.wrap(Projections.tuple(expressions));
    var hasGroups = false;
    for (Expression<?> e : expr.getArgs()) {
      hasGroups |= e instanceof GroupExpression;
    }
    if (hasGroups) {
      expr = withoutGroupExpressions(expr);
    }
    var result = query.select(expr).fetch();

    return result
        .collectList()
        .map(
            tupels -> {
              tupels.forEach(
                  tuple -> {
                    var row = (K[]) tuple.toArray();
                    var groupId = row[0];
                    var group = (GroupImpl) groups.get(groupId);
                    if (group == null) {
                      group = new GroupImpl(groupExpressions, maps);
                      groups.put(groupId, group);
                    }
                    group.add(row);
                  });

              return transform(groups);
            });
  }

  @SuppressWarnings("unchecked")
  protected Map<K, V> transform(Map<K, Group> groups) {
    return (Map<K, V>) groups;
  }
}
