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
 * {@code DefaultSessionHolder} is the default implementation of the {@link SessionHolder} interface
 *
 * @author tiwe
 */
public class DefaultSessionHolder implements SessionHolder {

  private final Mutiny.Session session;

  public DefaultSessionHolder(Mutiny.Session session) {
    this.session = session;
  }

  @Override
  public <R> Mutiny.SelectionQuery<R> createQuery(String queryString, Class<R> resultType) {
    return session.createQuery(queryString, resultType);
  }

  @Override
  public MutationQuery createMutationQuery(String queryString) {
    return session.createMutationQuery(queryString);
  }

  @Override
  public Mutiny.Query<?> createSQLQuery(String queryString) {
    return session.createNativeQuery(queryString);
  }
}
