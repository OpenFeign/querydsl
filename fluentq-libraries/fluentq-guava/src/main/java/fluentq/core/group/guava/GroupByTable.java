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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
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
import fluentq.core.util.ArrayUtils;

/**
 * Provides aggregated results as a table
 *
 * @author Jan-Willem Gmelig Meyling
 * @param <R> row type
 * @param <C> column type
 * @param <V> value type
 * @param <RES> table type
 */
public class GroupByTable<R, C, V, RES extends Table<R, C, V>>
    extends AbstractGroupByTransformer<R, RES> {

  GroupByTable(Expression<R> rowKey, Expression<C> columnKey, Expression<?>... expressions) {
    super(rowKey, ArrayUtils.combine(Expression.class, columnKey, expressions));
  }

  @Override
  public RES transform(FetchableQuery<?, ?> query) {
    // TODO Table<Object, Object, Group> after support for it in
    // https://github.fluentq/fluentq/issues/2644
    Table<R, Object, Group> groups = HashBasedTable.create();

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
        var row = iter.next().toArray();
        var groupId = (R) row[0];
        var rowId = row[1];
        var group = (GroupImpl) groups.get(groupId, rowId);
        if (group == null) {
          group = new GroupImpl(groupExpressions, maps);
          groups.put(groupId, rowId, group);
        }
        group.add(row);
      }
    } finally {
      iter.close();
    }

    // transform groups
    return transform(groups);
  }

  @SuppressWarnings("unchecked")
  protected RES transform(Table<R, ?, Group> groups) {
    return (RES) groups;
  }
}
