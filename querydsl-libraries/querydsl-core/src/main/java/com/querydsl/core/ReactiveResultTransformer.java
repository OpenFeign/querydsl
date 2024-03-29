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
package com.querydsl.core;

import org.reactivestreams.Publisher;

/**
 * Executes query on a {@link ReactiveFetchableQuery} and transforms results into T. This can be
 * used for example to group projected columns or to filter out duplicate results.
 *
 * @param <T> Transformations target type
 * @author sasa
 * @see com.querydsl.core.group.GroupBy
 */
public interface ReactiveResultTransformer<T> {

  /**
   * Execute the given query and transform the results
   *
   * @param query query to be used for execution
   * @return transformed results
   */
  Publisher<T> transform(ReactiveFetchableQuery<?, ?> query);
}
