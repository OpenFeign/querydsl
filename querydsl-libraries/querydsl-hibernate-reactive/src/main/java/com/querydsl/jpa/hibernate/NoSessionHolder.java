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
package com.querydsl.jpa.hibernate;

import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.MutationQuery;

/**
 * {@code NoSessionHolder} is a session holder for detached {@link HibernateQuery} usage
 *
 * @author tiwe
 */
public final class NoSessionHolder implements SessionHolder {

  public static final SessionHolder DEFAULT = new NoSessionHolder();

  private NoSessionHolder() {}

  @Override
  public Mutiny.Query<?> createSQLQuery(String queryString) {
    throw new UnsupportedOperationException("No session in detached Query available");
  }

  @Override
  public <R> Mutiny.SelectionQuery<R> createQuery(String queryString, Class<R> resultType) {
    throw new UnsupportedOperationException("No session in detached Query available");
  }

  @Override
  public MutationQuery createMutationQuery(String queryString) {
    throw new UnsupportedOperationException("No session in detached Query available");
  }
}
