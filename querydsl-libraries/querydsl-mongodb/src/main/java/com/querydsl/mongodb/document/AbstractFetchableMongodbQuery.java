/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.mongodb.document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.querydsl.core.CloseableIterator;
import com.querydsl.core.Fetchable;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

/**
 * {@link Fetchable} Mongodb query with a pluggable Document to Bean transformation.
 *
 * @param <K> result type
 * @param <Q> concrete subtype
 * @author Mark Paluch
 */
public abstract class AbstractFetchableMongodbQuery<
        K, Q extends AbstractFetchableMongodbQuery<K, Q>>
    extends AbstractMongodbQuery<Q> implements Fetchable<K> {

  private final Function<Document, K> transformer;

  private final MongoCollection<Document> collection;

  /**
   * Create a new MongodbQuery instance
   *
   * @param collection
   * @param transformer result transformer
   * @param serializer serializer
   */
  public AbstractFetchableMongodbQuery(
      MongoCollection<Document> collection,
      Function<Document, K> transformer,
      MongodbDocumentSerializer serializer) {
    super(serializer);
    this.transformer = transformer;
    this.collection = collection;
  }

  /**
   * Iterate with the specific fields
   *
   * @param paths fields to return
   * @return iterator
   */
  public CloseableIterator<K> iterate(Path<?>... paths) {
    getQueryMixin().setProjection(paths);
    return iterate();
  }

  @Override
  public CloseableIterator<K> iterate() {
    var cursor = createCursor();
    final var iterator = cursor.iterator();

    return new CloseableIterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public K next() {
        return transformer.apply(iterator.next());
      }

      @Override
      public void remove() {}

      @Override
      public void close() {
        iterator.close();
      }
    };
  }

  /**
   * Fetch with the specific fields
   *
   * @param paths fields to return
   * @return results
   */
  public List<K> fetch(Path<?>... paths) {
    getQueryMixin().setProjection(paths);
    return fetch();
  }

  @Override
  public List<K> fetch() {
    try {
      var cursor = createCursor();
      List<K> results = new ArrayList<>();
      for (Document document : cursor) {
        results.add(transformer.apply(document));
      }
      return results;
    } catch (NoResults ex) {
      return Collections.emptyList();
    }
  }

  /**
   * Fetch first with the specific fields
   *
   * @param paths fields to return
   * @return first result
   */
  public K fetchFirst(Path<?>... paths) {
    getQueryMixin().setProjection(paths);
    return fetchFirst();
  }

  @Override
  public K fetchFirst() {
    try {
      var c = createCursor().limit(1);
      var iterator = c.iterator();
      try {

        if (iterator.hasNext()) {
          return transformer.apply(iterator.next());
        } else {
          return null;
        }
      } finally {
        iterator.close();
      }
    } catch (NoResults ex) {
      return null;
    }
  }

  /**
   * Fetch one with the specific fields
   *
   * @param paths fields to return
   * @return first result
   */
  public K fetchOne(Path<?>... paths) {
    getQueryMixin().setProjection(paths);
    return fetchOne();
  }

  @Override
  public K fetchOne() {
    try {
      Long limit = getQueryMixin().getMetadata().getModifiers().getLimit();
      if (limit == null) {
        limit = 2L;
      }

      var c = createCursor().limit(limit.intValue());
      var iterator = c.iterator();
      try {

        if (iterator.hasNext()) {
          var rv = transformer.apply(iterator.next());
          if (iterator.hasNext()) {
            throw new NonUniqueResultException();
          }
          return rv;
        } else {
          return null;
        }
      } finally {
        iterator.close();
      }

    } catch (NoResults ex) {
      return null;
    }
  }

  /**
   * Fetch results with the specific fields
   *
   * @param paths fields to return
   * @return results
   */
  public QueryResults<K> fetchResults(Path<?>... paths) {
    getQueryMixin().setProjection(paths);
    return fetchResults();
  }

  @Override
  public QueryResults<K> fetchResults() {
    try {
      var total = fetchCount();
      if (total > 0L) {
        return new QueryResults<>(fetch(), getQueryMixin().getMetadata().getModifiers(), total);
      } else {
        return QueryResults.emptyResults();
      }
    } catch (NoResults ex) {
      return QueryResults.emptyResults();
    }
  }

  @Override
  public long fetchCount() {
    try {
      Predicate filter = createFilter(getQueryMixin().getMetadata());
      return collection.countDocuments(createQuery(filter));
    } catch (NoResults ex) {
      return 0L;
    }
  }

  protected FindIterable<Document> createCursor() {
    var metadata = getQueryMixin().getMetadata();
    Predicate filter = createFilter(metadata);
    return createCursor(
        collection,
        filter,
        metadata.getProjection(),
        metadata.getModifiers(),
        metadata.getOrderBy());
  }

  protected FindIterable<Document> createCursor(
      MongoCollection<Document> collection,
      @Nullable Predicate where,
      Expression<?> projection,
      QueryModifiers modifiers,
      List<OrderSpecifier<?>> orderBy) {

    var readPreference = getReadPreference();
    var collectionToUse =
        readPreference != null ? collection.withReadPreference(readPreference) : collection;
    var cursor = collectionToUse.find(createQuery(where)).projection(createProjection(projection));
    Integer limit = modifiers.getLimitAsInteger();
    Integer offset = modifiers.getOffsetAsInteger();

    if (limit != null) {
      cursor = cursor.limit(limit);
    }
    if (offset != null) {
      cursor = cursor.skip(offset);
    }
    if (orderBy.size() > 0) {
      cursor = cursor.sort(getSerializer().toSort(orderBy));
    }
    return cursor;
  }

  protected abstract MongoCollection<Document> getCollection(Class<?> type);

  @Override
  protected List<Object> getIds(Class<?> targetType, Predicate condition) {
    var collection = getCollection(targetType);
    // TODO : fetch only ids
    var cursor =
        createCursor(
            collection,
            condition,
            null,
            QueryModifiers.EMPTY,
            Collections.<OrderSpecifier<?>>emptyList());

    var iterator = cursor.iterator();
    try {

      if (iterator.hasNext()) {
        List<Object> ids = new ArrayList<>();
        for (Document obj : cursor) {
          ids.add(obj.get("_id"));
        }
        return ids;
      } else {
        return Collections.emptyList();
      }
    } finally {
      iterator.close();
    }
  }
}
