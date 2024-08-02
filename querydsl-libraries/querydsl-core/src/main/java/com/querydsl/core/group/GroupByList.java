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
package com.querydsl.core.group;

import com.querydsl.core.FetchableQuery;
import com.querydsl.core.types.Expression;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides aggregated results as a list
 *
 * @author tiwe
 * @param <K>
 * @param <V>
 */
public class GroupByList<K, V> extends AbstractGroupByTransformer<K, List<V>> {

  private final GroupByMap<K, V> mapTransformer;

  GroupByList(Expression<K> key, Expression<?>... expressions) {
    super(key, expressions);
    mapTransformer =
        new GroupByMap<K, V>(key, expressions) {
          @Override
          protected Map<K, V> transform(Map<K, Group> groups) {
            var results = new LinkedHashMap<K, V>((int) Math.ceil(groups.size() / 0.75), 0.75f);
            for (var entry : groups.entrySet()) {
              results.put(entry.getKey(), GroupByList.this.transform(entry.getValue()));
            }
            return results;
          }
        };
  }

  @Override
  public List<V> transform(FetchableQuery<?, ?> query) {
    var result = mapTransformer.transform(query);
    return new ArrayList<V>(result.values());
  }

  @SuppressWarnings("unchecked")
  protected V transform(Group group) {
    return (V) group;
  }
}
