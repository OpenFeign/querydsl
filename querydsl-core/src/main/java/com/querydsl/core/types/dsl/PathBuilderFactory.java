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
package com.querydsl.core.types.dsl;

import com.querydsl.core.util.StringUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code PathBuilderFactory} is a factory class for PathBuilder creation
 *
 * @author tiwe
 */
public final class PathBuilderFactory {

  private final Map<Class<?>, PathBuilder<?>> paths = new ConcurrentHashMap<>();

  private final String suffix;

  public PathBuilderFactory() {
    this("");
  }

  public PathBuilderFactory(String suffix) {
    this.suffix = suffix;
  }

  /**
   * Create a new PathBuilder instance for the given type
   *
   * @param type type of expression
   * @return new PathBuilder instance
   */
  @SuppressWarnings("unchecked")
  public <T> PathBuilder<T> create(Class<T> type) {
    PathBuilder<T> rv = (PathBuilder<T>) paths.get(type);
    if (rv == null) {
      rv = new PathBuilder<T>(type, variableName(type));
      paths.put(type, rv);
    }
    return rv;
  }

  private String variableName(Class<?> type) {
    return StringUtils.uncapitalize(type.getSimpleName()) + suffix;
  }
}
