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
package com.querydsl.jpa.hibernate;

import com.querydsl.core.JoinType;
import com.querydsl.core.dml.mutiny.MutinyDeleteClause;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.HQLTemplates;
import com.querydsl.jpa.JPAQueryMixin;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;
import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;

/**
 * DeleteClause implementation for Hibernate
 *
 * @author tiwe
 */
public class HibernateDeleteClause implements MutinyDeleteClause<HibernateDeleteClause> {

  private final QueryMixin<?> queryMixin = new JPAQueryMixin<Void>();

  private final SessionHolder session;

  private final JPQLTemplates templates;

  public HibernateDeleteClause(Mutiny.Session session, EntityPath<?> entity) {
    this(new DefaultSessionHolder(session), entity, HQLTemplates.DEFAULT);
  }

  public HibernateDeleteClause(Mutiny.StatelessSession session, EntityPath<?> entity) {
    this(new StatelessSessionHolder(session), entity, HQLTemplates.DEFAULT);
  }

  public HibernateDeleteClause(
      Mutiny.Session session, EntityPath<?> entity, JPQLTemplates templates) {
    this(new DefaultSessionHolder(session), entity, templates);
  }

  public HibernateDeleteClause(
      SessionHolder session, EntityPath<?> entity, JPQLTemplates templates) {
    this.session = session;
    this.templates = templates;
    queryMixin.addJoin(JoinType.DEFAULT, entity);
  }

  @Override
  public Uni<Integer> execute() {
    var serializer = new JPQLSerializer(templates, null);
    serializer.serializeForDelete(queryMixin.getMetadata());

    var query = session.createMutationQuery(serializer.toString());
    HibernateUtil.setConstants(
        query, serializer.getConstants(), queryMixin.getMetadata().getParams());
    return query.executeUpdate();
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
