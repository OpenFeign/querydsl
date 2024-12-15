package com.querydsl.core.dml.mutiny;

import io.smallrye.mutiny.Uni;

/**
 * Parent interface for DML clauses
 *
 * @param <C> concrete subtype
 */
public interface MutinyDMLClause<C extends MutinyDMLClause<C>> {

  /**
   * Execute the clause and return the amount of affected rows
   *
   * @return amount of affected rows or empty if not available
   */
  Uni<Integer> execute();
}
