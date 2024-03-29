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
package com.querydsl.r2dbc;

import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.r2dbc.dml.R2DBCDeleteClause;
import com.querydsl.r2dbc.dml.R2DBCInsertClause;
import com.querydsl.r2dbc.dml.R2DBCUpdateClause;
import com.querydsl.sql.RelationalPath;

/**
 * Factory interface for query and clause creation.
 *
 * <p>The default implementation is {@link R2DBCQueryFactory} and should be used for general query
 * creation. Type specific variants are available if database specific queries need to be created.
 *
 * @param <Q> query type
 * @param <D> delete clause type
 * @param <U> update clause type
 * @param <I> insert clause type
 * @author mc_fish
 */
public interface R2DBCCommonQueryFactory<
        Q extends R2DBCCommonQuery<?>, // extends AbstractSQLQuery<?>
        D extends R2DBCDeleteClause,
        U extends R2DBCUpdateClause,
        I extends R2DBCInsertClause
        /*M extends R2DBCMergeClause*/ >
    extends QueryFactory<Q> {

  /**
   * Create a new DELETE clause
   *
   * @param path table to delete from
   * @return delete clause
   */
  D delete(RelationalPath<?> path);

  /**
   * Create a new SELECT query
   *
   * @param from query source
   * @return query
   */
  Q from(Expression<?> from);

  /**
   * Create a new SELECT query
   *
   * @param from query sources
   * @return query
   */
  Q from(Expression<?>... from);

  /**
   * Create a new SELECT query
   *
   * @param subQuery query source
   * @param alias alias
   * @return query
   */
  Q from(SubQueryExpression<?> subQuery, Path<?> alias);

  /**
   * Create a new INSERT INTO clause
   *
   * @param path table to insert to
   * @return insert clause
   */
  I insert(RelationalPath<?> path);

  //    /**
  //     * Create a new MERGE clause
  //     *
  //     * @param path table to merge into
  //     * @return merge clause
  //     */
  //    M merge(RelationalPath<?> path);

  /**
   * Create a new UPDATE clause
   *
   * @param path table to update
   * @return update clause
   */
  U update(RelationalPath<?> path);

  /* (non-Javadoc)
   * @see com.querydsl.core.QueryFactory#query()
   */
  @Override
  Q query();
}
