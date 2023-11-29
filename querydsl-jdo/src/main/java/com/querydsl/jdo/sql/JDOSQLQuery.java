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
package com.querydsl.jdo.sql;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLTemplates;
import javax.jdo.PersistenceManager;
import org.jetbrains.annotations.Nullable;

/**
 * {@code JDOSQLQuery} is a {@link SQLQuery} implementation that uses JDO's SQL query functionality
 * to execute queries
 *
 * @author tiwe
 * @param <T> result type
 */
public final class JDOSQLQuery<T> extends AbstractSQLQuery<T, JDOSQLQuery<T>> {

  public JDOSQLQuery(@Nullable PersistenceManager persistenceManager, SQLTemplates templates) {
    this(persistenceManager, new Configuration(templates), new DefaultQueryMetadata(), false);
  }

  public JDOSQLQuery(@Nullable PersistenceManager persistenceManager, Configuration configuration) {
    this(persistenceManager, configuration, new DefaultQueryMetadata(), false);
  }

  public JDOSQLQuery(
      @Nullable PersistenceManager persistenceManager,
      Configuration configuration,
      QueryMetadata metadata,
      boolean detach) {
    super(metadata, configuration, persistenceManager, detach);
  }

  @Override
  public JDOSQLQuery<T> clone() {
    JDOSQLQuery<T> query =
        new JDOSQLQuery<T>(persistenceManager, configuration, getMetadata().clone(), detach);
    query.clone(this);
    return query;
  }

  @Override
  protected SQLSerializer createSerializer() {
    return new SQLSerializer(configuration);
  }

  @Override
  public <U> JDOSQLQuery<U> select(Expression<U> expr) {
    queryMixin.setProjection(expr);
    @SuppressWarnings("unchecked") // This is the new type
    JDOSQLQuery<U> newType = (JDOSQLQuery<U>) this;
    return newType;
  }

  @Override
  public JDOSQLQuery<Tuple> select(Expression<?>... exprs) {
    queryMixin.setProjection(exprs);
    @SuppressWarnings("unchecked") // This is the new type
    JDOSQLQuery<Tuple> newType = (JDOSQLQuery<Tuple>) this;
    return newType;
  }
}
