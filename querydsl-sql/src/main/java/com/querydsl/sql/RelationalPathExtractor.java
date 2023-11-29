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
package com.querydsl.sql;

import static com.querydsl.core.util.CollectionUtils.add;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.*;
import java.util.Collections;
import java.util.Set;

/**
 * {@code RelationalPathExtractor} extracts {@link RelationalPath} instances from expressions and
 * queries
 *
 * @author tiwe
 */
public final class RelationalPathExtractor
    implements Visitor<Set<RelationalPath<?>>, Set<RelationalPath<?>>> {

  public static final RelationalPathExtractor DEFAULT = new RelationalPathExtractor();

  public static Set<RelationalPath<?>> extract(QueryMetadata md) {
    Set<RelationalPath<?>> known = Collections.emptySet();
    known = DEFAULT.visitJoins(md.getJoins(), known);
    if (md.getProjection() != null) {
      known = md.getProjection().accept(DEFAULT, known);
    }
    for (OrderSpecifier<?> o : md.getOrderBy()) {
      known = o.getTarget().accept(DEFAULT, known);
    }
    for (Expression<?> g : md.getGroupBy()) {
      known = g.accept(DEFAULT, known);
    }
    if (md.getHaving() != null) {
      known = md.getHaving().accept(DEFAULT, known);
    }
    if (md.getWhere() != null) {
      known = md.getWhere().accept(DEFAULT, known);
    }
    return known;
  }

  public static Set<RelationalPath<?>> extract(Expression<?> expr) {
    return expr.accept(DEFAULT, Collections.emptySet());
  }

  @Override
  public Set<RelationalPath<?>> visit(Constant<?> expr, Set<RelationalPath<?>> known) {
    return known;
  }

  @Override
  public Set<RelationalPath<?>> visit(FactoryExpression<?> expr, Set<RelationalPath<?>> known) {
    for (Expression<?> arg : expr.getArgs()) {
      known = arg.accept(this, known);
    }
    return known;
  }

  @Override
  public Set<RelationalPath<?>> visit(Operation<?> expr, Set<RelationalPath<?>> known) {
    for (Expression<?> arg : expr.getArgs()) {
      known = arg.accept(this, known);
    }
    return known;
  }

  @Override
  public Set<RelationalPath<?>> visit(ParamExpression<?> expr, Set<RelationalPath<?>> known) {
    return known;
  }

  @Override
  public Set<RelationalPath<?>> visit(Path<?> expr, Set<RelationalPath<?>> known) {
    if (expr.getMetadata().isRoot()) {
      if (expr instanceof RelationalPath) {
        known = add(known, (RelationalPath<?>) expr);
      }
    } else {
      known = expr.getMetadata().getParent().accept(this, known);
    }
    return known;
  }

  @Override
  public Set<RelationalPath<?>> visit(SubQueryExpression<?> expr, Set<RelationalPath<?>> known) {
    Set<RelationalPath<?>> old = known;
    final QueryMetadata md = expr.getMetadata();
    known = visitJoins(md.getJoins(), known);
    if (md.getProjection() != null) {
      known = md.getProjection().accept(this, known);
    }
    for (OrderSpecifier<?> o : md.getOrderBy()) {
      known = o.getTarget().accept(this, known);
    }
    for (Expression<?> g : md.getGroupBy()) {
      known = g.accept(this, known);
    }
    if (md.getHaving() != null) {
      known = md.getHaving().accept(this, known);
    }
    if (md.getWhere() != null) {
      md.getWhere().accept(this, known);
    }
    return old;
  }

  @Override
  public Set<RelationalPath<?>> visit(TemplateExpression<?> expr, Set<RelationalPath<?>> known) {
    for (Object arg : expr.getArgs()) {
      if (arg instanceof Expression<?>) {
        known = ((Expression<?>) arg).accept(this, known);
      }
    }
    return known;
  }

  private Set<RelationalPath<?>> visitJoins(
      Iterable<JoinExpression> joins, Set<RelationalPath<?>> known) {
    for (JoinExpression j : joins) {
      known = j.getTarget().accept(this, known);
      if (j.getCondition() != null) {
        known = j.getCondition().accept(this, known);
      }
    }
    return known;
  }

  private RelationalPathExtractor() {}
}
