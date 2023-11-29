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

import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

/**
 * Abstraction for different Hibernate Session signatures
 *
 * @author tiwe
 */
public interface SessionHolder {

  /**
   * Create a JPQL query for the given query string
   *
   * @param queryString JPQL query string
   * @return query
   */
  Query<?> createQuery(String queryString);

  /**
   * Create an SQL query for the given query string
   *
   * @param queryString JPQL query string
   * @return query
   */
  NativeQuery<?> createSQLQuery(String queryString);
}
