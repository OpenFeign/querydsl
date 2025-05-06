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
package com.querydsl.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.ReadPreference;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.Fetchable;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.SimpleQuery;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CollectionPathBase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.Nullable;

/**
 * {@code AbstractMongodbQuery} provides a base class for general Querydsl query implementation with
 * a pluggable DBObject to Bean transformation
 *
 * @author laimw
 * @param <K> result type
 * @param <Q> concrete subtype
 */
public abstract class AbstractMongodbQuery<K, Q extends AbstractMongodbQuery<K, Q>>
    implements SimpleQuery<Q>, Fetchable<K> {

  @SuppressWarnings("serial")
  private static class NoResults extends RuntimeException {}

  private final MongodbSerializer serializer;

  private final QueryMixin<Q> queryMixin;

  private final MongoCollection collection;

  private final Function<K, Object> transformer;

  private ReadPreference readPreference;

  /**
   * Create a new MongodbQuery instance
   *
   * @param collection collection
   * @param transformer id transformer
   * @param serializer serializer
   */
  @SuppressWarnings("unchecked")
  public AbstractMongodbQuery(
      MongoCollection collection, Function<K, Object> transformer, MongodbSerializer serializer) {
    @SuppressWarnings("unchecked") // Q is this plus subclass
    var query = (Q) this;
    this.queryMixin = new QueryMixin<>(query, new DefaultQueryMetadata(), false);
    this.transformer = transformer;
    this.collection = collection;
    this.serializer = serializer;
  }

  /**
   * Define a join
   *
   * @param ref reference
   * @param target join target
   * @return join builder
   */
  public <T> JoinBuilder<Q, K, T> join(Path<T> ref, Path<T> target) {
    return new JoinBuilder<>(queryMixin, ref, target);
  }

  /**
   * Define a join
   *
   * @param ref reference
   * @param target join target
   * @return join builder
   */
  public <T> JoinBuilder<Q, K, T> join(CollectionPathBase<?, T, ?> ref, Path<T> target) {
    return new JoinBuilder<>(queryMixin, ref, target);
  }

  /**
   * Define a constraint for an embedded object
   *
   * @param collection collection
   * @param target target
   * @return builder
   */
  public <T> AnyEmbeddedBuilder<Q, K> anyEmbedded(
      Path<? extends Collection<T>> collection, Path<T> target) {
    return new AnyEmbeddedBuilder<>(queryMixin, collection);
  }

  protected abstract MongoCollection getCollection(Class<?> type);

  @Nullable
  protected Predicate createFilter(QueryMetadata metadata) {
    Predicate filter;
    if (!metadata.getJoins().isEmpty()) {
      filter = ExpressionUtils.allOf(metadata.getWhere(), createJoinFilter(metadata));
    } else {
      filter = metadata.getWhere();
    }
    return filter;
  }

  @SuppressWarnings("unchecked")
  @Nullable
  protected Predicate createJoinFilter(QueryMetadata metadata) {
    Map<Expression<?>, Predicate> predicates = new HashMap<>();
    List<JoinExpression> joins = metadata.getJoins();
    for (var i = joins.size() - 1; i >= 0; i--) {
      var join = joins.get(i);
      Path<?> source = (Path) ((Operation<?>) join.getTarget()).getArg(0);
      Path<?> target = (Path) ((Operation<?>) join.getTarget()).getArg(1);

      final var extraFilters = predicates.get(target.getRoot());
      Predicate filter = ExpressionUtils.allOf(join.getCondition(), extraFilters);
      List<? extends Object> ids = getIds(target.getType(), filter);
      if (ids.isEmpty()) {
        throw new NoResults();
      }
      Path<?> path = ExpressionUtils.path(String.class, source, "$id");
      predicates.merge(
          source.getRoot(), ExpressionUtils.in((Path<Object>) path, ids), ExpressionUtils::and);
    }
    Path<?> source = (Path) ((Operation) joins.get(0).getTarget()).getArg(0);
    return predicates.get(source.getRoot());
  }

  protected List<Object> getIds(Class<?> targetType, Predicate condition) {
    var collection = getCollection(targetType);
    // TODO : fetch only ids
    var cursor =
        createCursor(
                collection,
                condition,
                null,
                QueryModifiers.EMPTY,
                Collections.<OrderSpecifier<?>>emptyList())
            .cursor();
    if (cursor.hasNext()) {
      List<Object> ids = new ArrayList<>();
      cursor.forEachRemaining(document -> ids.add(transformer.apply(document)));
      return ids;
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public Q distinct() {
    return queryMixin.distinct();
  }

  public Q where(Predicate e) {
    return queryMixin.where(e);
  }

  @Override
  public Q where(Predicate... e) {
    return queryMixin.where(e);
  }

  @Override
  public Q limit(long limit) {
    return queryMixin.limit(limit);
  }

  @Override
  public Q offset(long offset) {
    return queryMixin.offset(offset);
  }

  @Override
  public Q restrict(QueryModifiers modifiers) {
    return queryMixin.restrict(modifiers);
  }

  public Q orderBy(OrderSpecifier<?> o) {
    return queryMixin.orderBy(o);
  }

  @Override
  public Q orderBy(OrderSpecifier<?>... o) {
    return queryMixin.orderBy(o);
  }

  @Override
  public <T> Q set(ParamExpression<T> param, T value) {
    return queryMixin.set(param, value);
  }

  /**
   * Iterate with the specific fields
   *
   * @param paths fields to return
   * @return iterator
   */
  public CloseableIterator<K> iterate(Path<?>... paths) {
    queryMixin.setProjection(paths);
    return iterate();
  }

  @Override
  public CloseableIterator<K> iterate() {
    final var cursor = createCursor().cursor();
    return new CloseableIterator<>() {
      @Override
      public boolean hasNext() {
        return cursor.hasNext();
      }

      @Override
      public K next() {
        return cursor.next();
      }

      @Override
      public void remove() {}

      @Override
      public void close() {}
    };
  }

  /**
   * Fetch with the specific fields
   *
   * @param paths fields to return
   * @return results
   */
  public List<K> fetch(Path<?>... paths) {
    queryMixin.setProjection(paths);
    return fetch();
  }

  @Override
  public List<K> fetch() {
    try {
      var cursor = createCursor();
      List<K> results = new ArrayList<>();
      for (K dbObject : cursor) {
        results.add(dbObject);
      }
      return results;
    } catch (NoResults ex) {
      return Collections.emptyList();
    }
  }

  protected FindIterable<K> createCursor() {
    var metadata = queryMixin.getMetadata();
    Predicate filter = createFilter(metadata);
    return createCursor(
        collection,
        filter,
        metadata.getProjection(),
        metadata.getModifiers(),
        metadata.getOrderBy());
  }

  protected FindIterable<K> createCursor(
      MongoCollection collection,
      @Nullable Predicate where,
      Expression<?> projection,
      QueryModifiers modifiers,
      List<OrderSpecifier<?>> orderBy) {
    var cursor =
        readPreference != null
            ? collection
                .withReadPreference(readPreference)
                .find(createQuery(where))
                .projection(createProjection(projection))
            : collection.find(createQuery(where)).projection(createProjection(projection));
    Integer limit = modifiers.getLimitAsInteger();
    Integer offset = modifiers.getOffsetAsInteger();
    if (limit != null) {
      cursor.limit(limit);
    }
    if (offset != null) {
      cursor.skip(offset);
    }
    if (orderBy.size() > 0) {
      cursor.sort(serializer.toSort(orderBy));
    }
    return cursor;
  }

  private Bson createProjection(Expression<?> projection) {
    if (projection instanceof FactoryExpression) {
      var obj = new BasicDBObject();
      for (Object expr : ((FactoryExpression) projection).getArgs()) {
        if (expr instanceof Expression) {
          obj.put((String) serializer.handle((Expression) expr), 1);
        }
      }
      return obj;
    }
    return null;
  }

  /**
   * Fetch first with the specific fields
   *
   * @param paths fields to return
   * @return first result
   */
  public K fetchFirst(Path<?>... paths) {
    queryMixin.setProjection(paths);
    return fetchFirst();
  }

  @Override
  public K fetchFirst() {
    try {
      var c = createCursor().limit(1).cursor();
      if (c.hasNext()) {
        return c.next();
      } else {
        return null;
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
    queryMixin.setProjection(paths);
    return fetchOne();
  }

  @Override
  public K fetchOne() throws NonUniqueResultException {
    try {
      Long limit = queryMixin.getMetadata().getModifiers().getLimit();
      if (limit == null) {
        limit = 2L;
      }
      var c = createCursor().limit(limit.intValue()).cursor();
      if (c.hasNext()) {
        var rv = c.next();
        if (c.hasNext()) {
          throw new NonUniqueResultException();
        }
        return rv;
      } else {
        return null;
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
    queryMixin.setProjection(paths);
    return fetchResults();
  }

  @Override
  public QueryResults<K> fetchResults() {
    try {
      var total = fetchCount();
      if (total > 0L) {
        return new QueryResults<>(fetch(), queryMixin.getMetadata().getModifiers(), total);
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
      Predicate filter = createFilter(queryMixin.getMetadata());
      return collection.countDocuments(createQuery(filter));
    } catch (NoResults ex) {
      return 0L;
    }
  }

  private Bson createQuery(@Nullable Predicate predicate) {
    if (predicate != null) {
      return (Bson) serializer.handle(predicate);
    } else {
      return new BasicDBObject();
    }
  }

  /**
   * Sets the read preference for this query
   *
   * @param readPreference read preference
   */
  public void setReadPreference(ReadPreference readPreference) {
    this.readPreference = readPreference;
  }

  /**
   * Get the where definition as a DBObject instance
   *
   * @return
   */
  public Bson asDBObject() {
    return createQuery(queryMixin.getMetadata().getWhere());
  }

  @Override
  public String toString() {
    return asDBObject().toString();
  }
}
