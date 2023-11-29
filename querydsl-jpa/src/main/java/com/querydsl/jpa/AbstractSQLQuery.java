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

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.ProjectableSQLQuery;
import jakarta.persistence.Entity;

/**
 * Abstract super class for SQLQuery implementation for JPA and Hibernate
 *
 * @param <T> result type
 * @param <Q> concrete subtype
 * @author tiwe
 */
public abstract class AbstractSQLQuery<T, Q extends AbstractSQLQuery<T, Q>>
    extends ProjectableSQLQuery<T, Q> {

  private static final class NativeQueryMixin<T> extends QueryMixin<T> {
    private NativeQueryMixin(QueryMetadata metadata) {
      super(metadata, false);
    }

    @Override
    public <RT> Expression<RT> convert(Expression<RT> expr, Role role) {
      return Conversions.convertForNativeQuery(super.convert(expr, role));
    }
  }

  @SuppressWarnings("unchecked")
  public AbstractSQLQuery(QueryMetadata metadata, Configuration configuration) {
    super(new NativeQueryMixin<Q>(metadata), configuration);
    this.queryMixin.setSelf((Q) this);
  }

  protected boolean isEntityExpression(Expression<?> expr) {
    return expr instanceof EntityPath || expr.getType().isAnnotationPresent(Entity.class);
  }

  protected Expression<?> extractEntityExpression(Expression<?> expr) {
    if (expr instanceof Operation) {
      return ((Operation<?>) expr).getArg(0);
    } else if (expr instanceof TemplateExpression) {
      return (Expression<?>) ((TemplateExpression<?>) expr).getArg(0);
    } else {
      return expr;
    }
  }
}
