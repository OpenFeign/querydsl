package com.querydsl.core.dml.mutiny;

import com.querydsl.core.support.QueryBase;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.SubQueryExpression;
import org.reactivestreams.Publisher;

/**
 * {@code FetchableQueryBase} extends the {@link QueryBase} class to provide default implementations
 * of the methods of the {@link com.querydsl.core.dml.reactive.ReactiveFetchable} interface
 *
 * @param <T> result type
 * @param <Q> concrete subtype
 */
public abstract class MutinyFetchableQueryBase<T, Q extends MutinyFetchableQueryBase<T, Q>>
    extends QueryBase<Q> implements MutinyFetchable<T> {

  public MutinyFetchableQueryBase(QueryMixin<Q> queryMixin) {
    super(queryMixin);
  }

  public <T> Publisher<T> transform(MutinyResultTransformer<T> transformer) {
    return transformer.transform((MutinyFetchableQuery<?, ?>) this);
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
