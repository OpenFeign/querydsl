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
package com.querydsl.hibernate.search;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.querydsl.core.Fetchable;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.SimpleQuery;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.lucene5.LuceneSerializer;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.dsl.SearchQueryOptionsStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;
import org.hibernate.search.mapper.orm.session.SearchSession;

/**
 * Abstract base class for Hibernate Search query classes
 *
 * @param <T> result type
 * @param <Q> concrete subtype
 */
public abstract class AbstractSearchQuery<T, Q extends AbstractSearchQuery<T, Q>>
    implements SimpleQuery<Q>, Fetchable<T> {

  private final EntityPath<T> path;

  private final QueryMixin<Q> queryMixin;

  private final LuceneSerializer serializer;

  private final SearchSession session;

  @SuppressWarnings("unchecked")
  public AbstractSearchQuery(SearchSession session, EntityPath<T> path) {
    this.queryMixin = new QueryMixin<Q>((Q) this);
    this.session = session;
    this.path = path;
    this.serializer = SearchSerializer.DEFAULT;
    queryMixin.from(path);
  }

  public AbstractSearchQuery(Session session, EntityPath<T> path) {
    this(Search.session(session), path);
  }

  @Override
  public long fetchCount() {
    return createQuery(true).fetchTotalHitCount();
  }

  private SearchQuery<T> createQuery(boolean forCount) {
    QueryMetadata metadata = queryMixin.getMetadata();
    Class<T> type = (Class<T>) path.getType();
    //        org.apache.lucene.search.Query query;
    //        if (metadata.getWhere() != null) {
    //            query = serializer.toQuery(metadata.getWhere(), metadata);
    //        } else {
    //            query = new MatchAllDocsQuery();
    //        }

    // TODO: implement where clause

    SearchQueryOptionsStep<?, T, SearchLoadingOptionsStep, ?, ?> queryStep =
        session.search(type).where(SearchPredicateFactory::matchAll);

    // TODO: add sorting
    //        List<OrderSpecifier<?>> orderBy = metadata.getOrderBy();
    //        if (!orderBy.isEmpty() && !forCount) {
    //            fullTextQuery.setSort(serializer.toSort(metadata.getOrderBy()));
    //        }
    return queryStep.toQuery();
  }

  @Override
  public Q distinct() {
    return queryMixin.distinct();
  }

  @Override
  @SuppressWarnings("unchecked")
  public CloseableIterator<T> iterate() {
    return new IteratorAdapter<T>(fetchAll(createQuery(false)).iterator());
  }

  @Override
  public Q limit(long limit) {
    return queryMixin.limit(limit);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<T> fetch() {
    return fetchAll(createQuery(false));
  }

  @SuppressWarnings("unchecked")
  @Override
  public QueryResults<T> fetchResults() {
    SearchQuery<T> query = createQuery(false);
    return new QueryResults<T>(
        fetchAll(query), queryMixin.getMetadata().getModifiers(), query.fetchTotalHitCount());
  }

  private List<T> fetchAll(SearchQuery<T> query) {
    return query.fetchHits(
        queryMixin.getMetadata().getModifiers().getOffsetAsInteger(),
        queryMixin.getMetadata().getModifiers().getLimitAsInteger());
  }

  @Override
  public Q offset(long offset) {
    return queryMixin.offset(offset);
  }

  @Override
  public Q orderBy(OrderSpecifier<?>... o) {
    return queryMixin.orderBy(o);
  }

  @Override
  public Q restrict(QueryModifiers modifiers) {
    return queryMixin.restrict(modifiers);
  }

  @Override
  public <P> Q set(ParamExpression<P> param, P value) {
    return queryMixin.set(param, value);
  }

  @Override
  public T fetchFirst() {
    return limit(1).fetchFirst();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T fetchOne() throws NonUniqueResultException {
    try {
      return (T) createQuery(false).fetchSingleHit();
    } catch (org.hibernate.NonUniqueResultException e) {
      throw new NonUniqueResultException(e);
    }
  }

  @Override
  public Q where(Predicate... e) {
    return queryMixin.where(e);
  }
}
