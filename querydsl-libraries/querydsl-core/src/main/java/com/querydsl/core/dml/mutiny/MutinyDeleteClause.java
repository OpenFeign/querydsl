package com.querydsl.core.dml.mutiny;

import com.querydsl.core.FilteredClause;

/**
 * {@code ReactiveDeleteClause} defines a generic interface for Delete clauses
 *
 * @param <C> concrete subtype
 */
public interface MutinyDeleteClause<C extends MutinyDeleteClause<C>>
    extends MutinyDMLClause<C>, FilteredClause<C> {}
