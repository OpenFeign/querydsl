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
package com.querydsl.jpa;

import com.querydsl.core.CloseableIterator;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import java.util.logging.Logger;
import org.hibernate.query.hql.internal.HqlParseTreeBuilder;
import org.jetbrains.annotations.Nullable;

class QueryHelper<T> extends JPAQueryBase<T, QueryHelper<T>> {

  private static final Logger logger = Logger.getLogger(QueryHelper.class.getName());

  QueryHelper(JPQLTemplates templates) {
    this(new DefaultQueryMetadata(), templates);
  }

  QueryHelper(QueryMetadata metadata, JPQLTemplates templates) {
    super(metadata, templates);
  }

  @Override
  protected JPQLSerializer createSerializer() {
    return new JPQLSerializer(getTemplates());
  }

  @Override
  protected void reset() {
    // do nothing
  }

  @Override
  public long fetchCount() {
    return 0;
  }

  @Override
  @Nullable
  public CloseableIterator<T> iterate() {
    throw new UnsupportedOperationException();
  }

  @Override
  @Nullable
  public QueryResults<T> fetchResults() {
    throw new UnsupportedOperationException();
  }

  /**
   * Printing of the AST has been removed with hibernate 6. TODO: maybe re-add if we find a way to
   * do it with hibernate 6.
   */
  public void parse() {
    //    String input = toString();
    //    logger.fine("input: " + input.replace('\n', ' '));
    //    HqlParser parser = HqlParser.getInstance(input);
    //    parser.setFilter(false);
    //    parser.statement();
    //    AST ast = parser.getAST();
    //    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //    parser.showAst(ast, new PrintStream(baos));
    //    assertEquals(
    //            "At least one error occurred during parsing " + input,
    //            0,
    //            parser.getParseErrorHandler().getErrorCount());

    var input = toString();
    logger.fine("input: " + input.replace('\n', ' '));
    var parser =
        HqlParseTreeBuilder.INSTANCE.buildHqlParser(
            input, HqlParseTreeBuilder.INSTANCE.buildHqlLexer(input));
    parser.statement();
  }

  @Override
  public T fetchOne() throws NonUniqueResultException {
    throw new UnsupportedOperationException();
  }

  @Override
  public QueryHelper<T> clone() {
    return new QueryHelper<>(getMetadata().clone(), getTemplates());
  }

  @Override
  public <U> QueryHelper<U> select(Expression<U> expr) {
    queryMixin.setProjection(expr);
    @SuppressWarnings("unchecked") // This is the new type
    var newType = (QueryHelper<U>) this;
    return newType;
  }

  @Override
  public QueryHelper<Tuple> select(Expression<?>... exprs) {
    queryMixin.setProjection(exprs);
    @SuppressWarnings("unchecked") // This is the new type
    var newType = (QueryHelper<Tuple>) this;
    return newType;
  }
}
