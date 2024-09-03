package com.querydsl.core.dml.reactive;

import com.querydsl.core.FilteredClause;

/**
 * {@code ReactiveDeleteClause} defines a generic interface for Delete clauses
 *
 * @param <C> concrete subtype
 */
public interface ReactiveDeleteClause<C extends ReactiveDeleteClause<C>>
    extends ReactiveDMLClause<C>, FilteredClause<C> {}
