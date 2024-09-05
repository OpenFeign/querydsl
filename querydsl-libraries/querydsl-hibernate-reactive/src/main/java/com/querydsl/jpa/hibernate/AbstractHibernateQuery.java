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

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Path;
import com.querydsl.jpa.HQLTemplates;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;
import io.smallrye.mutiny.Uni;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.SelectionQuery;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for Hibernate API based implementations of the JPQL interface
 *
 * @param <T> result type
 * @param <Q> concrete subtype
 * @author tiwe
 */
public abstract class AbstractHibernateQuery<T, Q extends AbstractHibernateQuery<T, Q>>
    extends JPAQueryBase<T, Q> {

  private static final Logger logger = Logger.getLogger(HibernateQuery.class.getName());

  @Nullable protected Boolean cacheable, readOnly;
  @Nullable protected String cacheRegion, comment;

  protected int fetchSize = 0;

  protected final Map<Path<?>, LockMode> lockModes = new HashMap<>();

  @Nullable protected FlushMode flushMode;

  private final SessionHolder session;

  protected int timeout = 0;

  public AbstractHibernateQuery(Mutiny.Session session) {
    this(new DefaultSessionHolder(session), HQLTemplates.DEFAULT, new DefaultQueryMetadata());
  }

  public AbstractHibernateQuery(
      SessionHolder session, JPQLTemplates patterns, QueryMetadata metadata) {
    super(metadata, patterns);
    this.session = session;
  }

  /**
   * Expose the original Hibernate query for the given projection
   *
   * @return query
   */
  public Mutiny.SelectionQuery<T> createQuery() {
    return (SelectionQuery<T>)
        createQuery(getMetadata().getModifiers(), false, getMetadata().getProjection().getType());
  }

  private <R> Mutiny.SelectionQuery<R> createQuery(
      @Nullable QueryModifiers modifiers, boolean forCount, Class<R> resultType) {
    var serializer = serialize(forCount);
    var queryString = serializer.toString();
    logQuery(queryString);
    var query = session.createQuery(queryString, resultType);
    HibernateUtil.setConstants(query, serializer.getConstants(), getMetadata().getParams());
    if (fetchSize > 0) {
      query.setMaxResults(fetchSize);
    }
    //   FIXME if (timeout > 0) {
    //      query.setTimeout(timeout);
    //    }
    if (cacheable != null) {
      query.setCacheable(cacheable);
    }
    if (cacheRegion != null) {
      query.setCacheRegion(cacheRegion);
    }
    if (comment != null) {
      query.setComment(comment);
    }
    if (readOnly != null) {
      query.setReadOnly(readOnly);
    }
    for (Map.Entry<Path<?>, LockMode> entry : lockModes.entrySet()) {
      query.setLockMode(entry.getKey().toString(), entry.getValue());
    }
    if (flushMode != null) {
      query.setFlushMode(flushMode);
    }

    if (modifiers != null && modifiers.isRestricting()) {
      Integer limit = modifiers.getLimitAsInteger();
      Integer offset = modifiers.getOffsetAsInteger();
      if (limit != null) {
        query.setMaxResults(limit);
      }
      if (offset != null) {
        query.setFirstResult(offset);
      }
    }

    // set transformer, if necessary
    //   FIXME Expression<?> projection = getMetadata().getProjection();
    //    if (!forCount && projection instanceof FactoryExpression) {
    //      query.setResultTransformer(
    //          new FactoryExpressionTransformer((FactoryExpression<?>) projection));
    //    }
    return query;
  }

  @Override
  public Uni<List<T>> fetch() {
    return createQuery().getResultList();
  }

  @Override
  public final Uni<T> fetchFirst() {
    return createQuery().setMaxResults(1).getSingleResultOrNull();
  }

  @Override
  public Uni<T> fetchOne() {
    return createQuery().getSingleResult();
  }

  @Override
  public Uni<Long> fetchCount() {
    var query = createQuery();
    var rv = query.getResultCount();
    if (rv != null) {
      return rv;
    } else {
      throw new QueryException("Query returned null");
    }
  }

  //
  //  @Override
  //  public QueryResults<T> fetchResults() {
  //    try {
  //      var countQuery = createQuery(null, true);
  //      long total = (Long) countQuery.uniqueResult();
  //
  //      if (total > 0) {
  //        var modifiers = getMetadata().getModifiers();
  //        var query = createQuery(modifiers, false);
  //        @SuppressWarnings("unchecked")
  //        List<T> list = query.list();
  //        return new QueryResults<>(list, modifiers, total);
  //      } else {
  //        return QueryResults.emptyResults();
  //      }
  //    } finally {
  //      reset();
  //    }
  //  }

  protected void logQuery(String queryString) {
    if (logger.isLoggable(Level.FINE)) {
      var normalizedQuery = queryString.replace('\n', ' ');
      logger.fine(normalizedQuery);
    }
  }

  /**
   * Enable caching of this query result set.
   *
   * @param cacheable Should the query results be cacheable?
   */
  @SuppressWarnings("unchecked")
  public Q setCacheable(boolean cacheable) {
    this.cacheable = cacheable;
    return (Q) this;
  }

  /**
   * Set the name of the cache region.
   *
   * @param cacheRegion the name of a query cache region, or {@code null} for the default query
   *     cache
   */
  @SuppressWarnings("unchecked")
  public Q setCacheRegion(String cacheRegion) {
    this.cacheRegion = cacheRegion;
    return (Q) this;
  }

  /**
   * Add a comment to the generated SQL.
   *
   * @param comment comment
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public Q setComment(String comment) {
    this.comment = comment;
    return (Q) this;
  }

  /**
   * Set a fetchJoin size for the underlying JDBC query.
   *
   * @param fetchSize the fetchJoin size
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public Q setFetchSize(int fetchSize) {
    this.fetchSize = fetchSize;
    return (Q) this;
  }

  /**
   * Set the lock mode for the given path.
   *
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public Q setLockMode(Path<?> path, LockMode lockMode) {
    lockModes.put(path, lockMode);
    return (Q) this;
  }

  /**
   * Override the current session flush mode, just for this query.
   *
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public Q setFlushMode(FlushMode flushMode) {
    this.flushMode = flushMode;
    return (Q) this;
  }

  /**
   * Entities retrieved by this query will be loaded in a read-only mode where Hibernate will never
   * dirty-check them or make changes persistent.
   *
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public Q setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    return (Q) this;
  }

  /**
   * Set a timeout for the underlying JDBC query.
   *
   * @param timeout the timeout in seconds
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public Q setTimeout(int timeout) {
    this.timeout = timeout;
    return (Q) this;
  }

  @Override
  protected JPQLSerializer createSerializer() {
    return new JPQLSerializer(getTemplates());
  }

  protected void clone(Q query) {
    cacheable = query.cacheable;
    cacheRegion = query.cacheRegion;
    fetchSize = query.fetchSize;
    flushMode = query.flushMode;
    lockModes.putAll(query.lockModes);
    readOnly = query.readOnly;
    timeout = query.timeout;
  }

  protected abstract Q clone(SessionHolder sessionHolder);

  /**
   * Clone the state of this query to a new instance with the given Session
   *
   * @param session session
   * @return cloned query
   */
  public Q clone(Mutiny.Session session) {
    return this.clone(new DefaultSessionHolder(session));
  }

  /**
   * Clone the state of this query to a new instance with the given StatelessSession
   *
   * @param session session
   * @return cloned query
   */
  public Q clone(Mutiny.StatelessSession session) {
    return this.clone(new StatelessSessionHolder(session));
  }

  /**
   * Clone the state of this query to a new instance
   *
   * @return closed query
   */
  @Override
  public Q clone() {
    return this.clone(this.session);
  }
}
