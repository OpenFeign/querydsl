package com.querydsl.core.dml.reactive;

import com.querydsl.core.support.QueryBase;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.SubQueryExpression;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

/**
 * {@code FetchableQueryBase} extends the {@link QueryBase} class to provide default implementations
 * of the methods of the {@link com.querydsl.core.dml.reactive.ReactiveFetchable} interface
 *
 * @param <T> result type
 * @param <Q> concrete subtype
 */
public abstract class ReactiveFetchableQueryBase<T, Q extends ReactiveFetchableQueryBase<T, Q>>
    extends QueryBase<Q> implements ReactiveFetchable<T> {

  public ReactiveFetchableQueryBase(QueryMixin<Q> queryMixin) {
    super(queryMixin);
  }

  @Override
  public final Mono<T> fetchFirst() {
    return fetch().take(1).singleOrEmpty();
  }

  @Override
  public Mono<T> fetchOne() {
    return fetch().singleOrEmpty();
  }

  public <T> Publisher<T> transform(ReactiveResultTransformer<T> transformer) {
    return transformer.transform((ReactiveFetchableQuery<?, ?>) this);
  }

  @Override
  public final boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof SubQueryExpression<?> s) {
      return s.getMetadata().equals(queryMixin.getMetadata());
    } else {
      return false;
    }
  }
}
