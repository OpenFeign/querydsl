/*
 * Copyright 2020, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.core.group.guava;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fluentq.core.FetchableQuery;
import fluentq.core.Tuple;
import fluentq.core.group.AbstractGroupByTransformer;
import fluentq.core.group.Group;
import fluentq.core.group.GroupExpression;
import fluentq.core.group.GroupImpl;
import fluentq.core.types.Expression;
import fluentq.core.types.FactoryExpression;
import fluentq.core.types.FactoryExpressionUtils;
import fluentq.core.types.Projections;

/**
 * Provides aggregated results as a map
 *
 * @author Jan-Willem Gmelig Meyling
 * @param <K> multi map key type
 * @param <V> multi map value type
 * @param <R> multi map type
 */
public class GroupByMultimap<K, V, R extends Multimap<K, V>>
    extends AbstractGroupByTransformer<K, R> {

  GroupByMultimap(Expression<K> key, Expression<?>... expressions) {
    super(key, expressions);
  }

  @Override
  public R transform(FetchableQuery<?, ?> query) {
    Multimap<K, Group> groups = LinkedHashMultimap.create();

    // create groups
    FactoryExpression<Tuple> expr = FactoryExpressionUtils.wrap(Projections.tuple(expressions));
    var hasGroups = false;
    for (Expression<?> e : expr.getArgs()) {
      hasGroups |= e instanceof GroupExpression;
    }
    if (hasGroups) {
      expr = withoutGroupExpressions(expr);
    }
    var iter = query.select(expr).iterate();
    try {
      while (iter.hasNext()) {
        @SuppressWarnings("unchecked") // This type is mandated by the key type
        var row = (K[]) iter.next().toArray();
        var groupId = row[0];
        var group = new GroupImpl(groupExpressions, maps);
        groups.put(groupId, group);
        group.add(row);
      }
    } finally {
      iter.close();
    }

    // transform groups
    return transform(groups);
  }

  @SuppressWarnings("unchecked")
  protected R transform(Multimap<K, Group> groups) {
    return (R) groups;
  }
}
