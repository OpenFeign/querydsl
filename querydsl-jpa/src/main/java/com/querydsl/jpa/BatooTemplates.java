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
package com.querydsl.jpa;

import com.querydsl.core.types.Ops;

/** {@code BatooTemplates} extends {@link JPQLTemplates} with Batoo specific extensions */
public class BatooTemplates extends JPQLTemplates {

  public static final BatooTemplates DEFAULT = new BatooTemplates();

  public BatooTemplates() {
    this(DEFAULT_ESCAPE);
  }

  public BatooTemplates(char escape) {
    super(escape);
    add(JPQLOps.CAST, "cast({0} as {1s})");
    add(Ops.STRING_CAST, "cast({0} as varchar)");
    add(Ops.NUMCAST, "cast({0} as {1s})");
  }

  @Override
  public boolean isPathInEntitiesSupported() {
    return false;
  }
}
