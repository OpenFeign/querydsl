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
package com.querydsl.apt;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import java.lang.annotation.Annotation;

/**
 * Implementation of the {@link QueryType} annotation
 *
 * @author tiwe
 */
@SuppressWarnings("all")
public class QueryTypeImpl implements QueryType {

  private final PropertyType value;

  public QueryTypeImpl(PropertyType value) {
    this.value = value;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return QueryType.class;
  }

  @Override
  public PropertyType value() {
    return value;
  }
}
