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
package fluentq.jpa.hibernate;

import fluentq.core.JoinType;
import fluentq.core.dml.DeleteClause;
import fluentq.core.support.QueryMixin;
import fluentq.core.types.EntityPath;
import fluentq.core.types.Predicate;
import fluentq.jpa.HQLTemplates;
import fluentq.jpa.JPAQueryMixin;
import fluentq.jpa.JPQLSerializer;
import fluentq.jpa.JPQLTemplates;
import org.hibernate.Session;
import org.hibernate.StatelessSession;

/**
 * DeleteClause implementation for Hibernate
 *
 * @author tiwe
 */
public class HibernateDeleteClause implements DeleteClause<HibernateDeleteClause> {

  private final QueryMixin<?> queryMixin = new JPAQueryMixin<Void>();

  private final SessionHolder session;

  private final JPQLTemplates templates;

  public HibernateDeleteClause(Session session, EntityPath<?> entity) {
    this(new DefaultSessionHolder(session), entity, HQLTemplates.DEFAULT);
  }

  public HibernateDeleteClause(StatelessSession session, EntityPath<?> entity) {
    this(new StatelessSessionHolder(session), entity, HQLTemplates.DEFAULT);
  }

  public HibernateDeleteClause(Session session, EntityPath<?> entity, JPQLTemplates templates) {
    this(new DefaultSessionHolder(session), entity, templates);
  }

  public HibernateDeleteClause(
      SessionHolder session, EntityPath<?> entity, JPQLTemplates templates) {
    this.session = session;
    this.templates = templates;
    queryMixin.addJoin(JoinType.DEFAULT, entity);
  }

  @Override
  public long execute() {
    var serializer = new JPQLSerializer(templates, null);
    serializer.serializeForDelete(queryMixin.getMetadata());

    var query = session.createQuery(serializer.toString()).asMutationQuery();
    HibernateUtil.setConstants(
        query, serializer.getConstants(), queryMixin.getMetadata().getParams());
    return query.execute();
  }

  @Override
  public HibernateDeleteClause where(Predicate... o) {
    for (Predicate p : o) {
      queryMixin.where(p);
    }
    return this;
  }

  @Override
  public String toString() {
    var serializer = new JPQLSerializer(templates, null);
    serializer.serializeForDelete(queryMixin.getMetadata());
    return serializer.toString();
  }
}
