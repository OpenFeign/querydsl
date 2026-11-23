package com.querydsl.core.dml;

/**
 * {@code ReactiveDeleteClause} defines a generic interface for Delete clauses
 *
 * @param <C> concrete subtype
 */
@Deprecated
public interface ReactiveDeleteClause<C extends ReactiveDeleteClause<C>>
    extends ReactiveDMLClause<C>, ReactiveFilteredClause<C> {}
