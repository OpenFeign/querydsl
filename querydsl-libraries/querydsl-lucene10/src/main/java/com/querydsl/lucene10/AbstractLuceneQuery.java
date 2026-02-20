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
package com.querydsl.lucene10;

import com.querydsl.core.CloseableIterator;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.Fetchable;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.SimpleQuery;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TotalHitCountCollectorManager;
import org.jetbrains.annotations.Nullable;

/**
 * AbstractLuceneQuery is an abstract super class for Lucene query implementations
 *
 * @author tiwe
 * @param <T> projection type
 * @param <Q> concrete subtype of querydsl
 */
public abstract class AbstractLuceneQuery<T, Q extends AbstractLuceneQuery<T, Q>>
    implements SimpleQuery<Q>, Fetchable<T> {

  private static final String JAVA_ISO_CONTROL = "[\\p{Cntrl}&&[^\r\n\t]]";

  private final QueryMixin<Q> queryMixin;

  private final IndexSearcher searcher;

  private final LuceneSerializer serializer;

  private final Function<Document, T> transformer;

  @Nullable private Set<String> fieldsToLoad;

  private List<Query> filters = Collections.emptyList();

  @Nullable private Query filter;

  @Nullable private Sort querySort;

  @SuppressWarnings("unchecked")
  public AbstractLuceneQuery(
      LuceneSerializer serializer, IndexSearcher searcher, Function<Document, T> transformer) {
    queryMixin = new QueryMixin<Q>((Q) this, new DefaultQueryMetadata());
    this.serializer = serializer;
    this.searcher = searcher;
    this.transformer = transformer;
  }

  public AbstractLuceneQuery(IndexSearcher searcher, Function<Document, T> transformer) {
    this(LuceneSerializer.DEFAULT, searcher, transformer);
  }

  private long innerCount() {
    try {
      final int maxDoc = searcher.getIndexReader().maxDoc();
      if (maxDoc == 0) {
        return 0;
      }
      return searcher.search(
          createQuery(), new TotalHitCountCollectorManager(searcher.getSlices()));
    } catch (IOException | IllegalArgumentException e) {
      throw new QueryException(e);
    }
  }

  @Override
  public long fetchCount() {
    return innerCount();
  }

  protected Query createQuery() {
    Query originalQuery;
    if (queryMixin.getMetadata().getWhere() == null) {
      originalQuery = new MatchAllDocsQuery();
    } else {
      originalQuery =
          serializer.toQuery(queryMixin.getMetadata().getWhere(), queryMixin.getMetadata());
    }
    Query filter = getFilter();
    if (filter != null) {
      BooleanQuery.Builder builder = new BooleanQuery.Builder();
      builder.add(originalQuery, Occur.MUST);
      builder.add(filter, Occur.FILTER);
      return builder.build();
    }
    return originalQuery;
  }

  @Override
  public Q distinct() {
    throw new UnsupportedOperationException("use distinct(path) instead");
  }

  /**
   * Apply the given Lucene Query as a filter to the search results
   *
   * @param filter filter query
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public Q filter(Query filter) {
    if (filters.isEmpty()) {
      this.filter = filter;
      filters = Collections.singletonList(filter);
    } else {
      this.filter = null;
      if (filters.size() == 1) {
        filters = new ArrayList<>();
      }
      filters.add(filter);
    }
    return (Q) this;
  }

  private Query getFilter() {
    if (filter == null && !filters.isEmpty()) {
      BooleanQuery.Builder builder = new BooleanQuery.Builder();
      for (Query f : filters) {
        builder.add(f, Occur.SHOULD);
      }
      filter = builder.build();
    }
    return filter;
  }

  @Override
  public Q limit(long limit) {
    return queryMixin.limit(limit);
  }

  @Override
  public CloseableIterator<T> iterate() {
    final QueryMetadata metadata = queryMixin.getMetadata();
    final List<OrderSpecifier<?>> orderBys = metadata.getOrderBy();
    final Integer queryLimit = metadata.getModifiers().getLimitAsInteger();
    final Integer queryOffset = metadata.getModifiers().getOffsetAsInteger();
    Sort sort = querySort;
    int limit;
    final int offset = queryOffset != null ? queryOffset : 0;
    try {
      limit = maxDoc();
      if (limit == 0) {
        return CloseableIterator.of(Collections.emptyIterator());
      }
    } catch (IOException | IllegalArgumentException e) {
      throw new QueryException(e);
    }
    if (queryLimit != null && queryLimit < limit) {
      limit = queryLimit;
    }
    if (sort == null && !orderBys.isEmpty()) {
      sort = serializer.toSort(orderBys);
    }

    try {
      ScoreDoc[] scoreDocs;
      int sumOfLimitAndOffset = limit + offset;
      if (sumOfLimitAndOffset < 1) {
        throw new QueryException(
            "The given limit ("
                + limit
                + ") and offset ("
                + offset
                + ") cause an integer overflow.");
      }
      if (sort != null) {
        scoreDocs = searcher.search(createQuery(), sumOfLimitAndOffset, sort).scoreDocs;
      } else {
        scoreDocs = searcher.search(createQuery(), sumOfLimitAndOffset, Sort.INDEXORDER).scoreDocs;
      }
      if (offset < scoreDocs.length) {
        return new ResultIterator<T>(scoreDocs, offset, searcher, fieldsToLoad, transformer);
      }
      return CloseableIterator.of(Collections.emptyIterator());
    } catch (final IOException e) {
      throw new QueryException(e);
    }
  }

  private List<T> innerList() {
    return CloseableIterator.asList(iterate());
  }

  @Override
  public List<T> fetch() {
    return innerList();
  }

  /**
   * Set the given fields to load
   *
   * @param fieldsToLoad fields to load
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public Q load(Set<String> fieldsToLoad) {
    this.fieldsToLoad = fieldsToLoad;
    return (Q) this;
  }

  /**
   * Load only the fields of the given paths
   *
   * @param paths fields to load
   * @return the current object
   */
  @SuppressWarnings("unchecked")
  public Q load(Path<?>... paths) {
    Set<String> fields = new HashSet<String>();
    for (Path<?> path : paths) {
      fields.add(serializer.toField(path));
    }
    this.fieldsToLoad = fields;
    return (Q) this;
  }

  @Override
  public QueryResults<T> fetchResults() {
    List<T> documents = innerList();
    return new QueryResults<T>(documents, queryMixin.getMetadata().getModifiers(), innerCount());
  }

  @Override
  public Q offset(long offset) {
    return queryMixin.offset(offset);
  }

  public Q orderBy(OrderSpecifier<?> o) {
    return queryMixin.orderBy(o);
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

  @SuppressWarnings("unchecked")
  public Q sort(Sort sort) {
    this.querySort = sort;
    return (Q) this;
  }

  @Nullable
  private T oneResult(boolean unique) {
    try {
      int maxDoc = maxDoc();
      if (maxDoc == 0) {
        return null;
      }
      final ScoreDoc[] scoreDocs =
          searcher.search(createQuery(), maxDoc, Sort.INDEXORDER).scoreDocs;
      int index = 0;
      QueryModifiers modifiers = queryMixin.getMetadata().getModifiers();
      Long offset = modifiers.getOffset();
      if (offset != null) {
        index = offset.intValue();
      }
      Long limit = modifiers.getLimit();
      if (unique
          && (limit == null ? scoreDocs.length - index > 1 : limit > 1 && scoreDocs.length > 1)) {
        throw new NonUniqueResultException(
            "Unique result requested, but " + scoreDocs.length + " found.");
      } else if (scoreDocs.length > index) {
        Document document;
        if (fieldsToLoad != null) {
          document = searcher.storedFields().document(scoreDocs[index].doc, fieldsToLoad);
        } else {
          document = searcher.storedFields().document(scoreDocs[index].doc);
        }
        return transformer.apply(document);
      } else {
        return null;
      }
    } catch (IOException | IllegalArgumentException e) {
      throw new QueryException(e);
    }
  }

  @Override
  public T fetchFirst() {
    return oneResult(false);
  }

  @Override
  public T fetchOne() throws NonUniqueResultException {
    return oneResult(true);
  }

  public Q where(Predicate e) {
    return queryMixin.where(e);
  }

  @Override
  public Q where(Predicate... e) {
    return queryMixin.where(e);
  }

  @Override
  public String toString() {
    return createQuery().toString().replaceAll(JAVA_ISO_CONTROL, "_");
  }

  private int maxDoc() throws IOException {
    return searcher.getIndexReader().maxDoc();
  }
}
