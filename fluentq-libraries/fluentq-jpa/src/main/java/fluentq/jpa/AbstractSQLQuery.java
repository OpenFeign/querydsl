/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.jpa;

import fluentq.core.QueryMetadata;
import fluentq.core.support.QueryMixin;
import fluentq.core.types.EntityPath;
import fluentq.core.types.Expression;
import fluentq.core.types.Operation;
import fluentq.core.types.TemplateExpression;
import fluentq.sql.Configuration;
import fluentq.sql.ProjectableSQLQuery;
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
    super(new NativeQueryMixin<>(metadata), configuration);
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
