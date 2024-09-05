package com.querydsl.core.dml.mutiny;

import com.querydsl.core.FilteredClause;
import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Path;
import java.util.List;

/**
 * {@code ReactiveUpdateClause} defines a generic extensible interface for Update clauses
 *
 * @param <C> concrete subtype
 */
public interface MutinyUpdateClause<C extends MutinyUpdateClause<C>>
    extends StoreClause<C>, FilteredClause<C>, MutinyDMLClause<C> {

  /**
   * Set the paths to be updated
   *
   * @param paths paths to be updated
   * @param values values to be set
   * @return the current object
   */
  C set(List<? extends Path<?>> paths, List<?> values);
}
