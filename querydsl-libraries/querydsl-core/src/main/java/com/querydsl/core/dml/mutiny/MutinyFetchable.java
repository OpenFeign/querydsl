package com.querydsl.core.dml.mutiny;

import com.querydsl.core.Query;
import io.smallrye.mutiny.Uni;
import java.util.List;

/**
 * {@code MutinyFetchable} defines default projection methods for {@link Query} implementations. All
 * Querydsl query implementations should implement this interface.
 *
 * @param <T> result type
 */
public interface MutinyFetchable<T> {

  /**
   * Get the projection as a typed Flux.
   *
   * @return result
   */
  Uni<List<T>> fetch();

  /**
   * Get the first result of the projection.
   *
   * @return first result
   */
  Uni<T> fetchFirst();

  /**
   * Get the projection as a unique result.
   *
   * @return first result
   */
  Uni<T> fetchOne();

  /**
   * Get the count of matched elements
   *
   * @return row count
   */
  Uni<Long> fetchCount();
}
