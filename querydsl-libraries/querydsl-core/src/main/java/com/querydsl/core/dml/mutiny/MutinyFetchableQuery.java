package com.querydsl.core.dml.mutiny;

import com.querydsl.core.ResultTransformer;
import com.querydsl.core.SimpleQuery;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import org.reactivestreams.Publisher;

/**
 * {@code FetchableQuery} extends {@link MutinyFetchable} and {@link SimpleQuery} with projection
 * changing methods and result aggregation functionality using {@link ResultTransformer} instances.
 *
 * @param <T> element type
 * @param <Q> concrete subtype
 */
public interface MutinyFetchableQuery<T, Q extends MutinyFetchableQuery<T, Q>>
    extends SimpleQuery<Q>, MutinyFetchable<T> {

  /**
   * Change the projection of this query
   *
   * @param <U>
   * @param expr new projection
   * @return the current object
   */
  <U> MutinyFetchableQuery<U, ?> select(Expression<U> expr);

  /**
   * Change the projection of this query
   *
   * @param exprs new projection
   * @return the current object
   */
  MutinyFetchableQuery<Tuple, ?> select(Expression<?>... exprs);

  /**
   * Apply the given transformer to this {@code ReactiveFetchableQuery} instance and return the
   * results
   *
   * @param <S>
   * @param transformer result transformer
   * @return transformed result
   */
  <S> Publisher<S> transform(MutinyResultTransformer<S> transformer);
}
