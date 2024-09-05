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

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.HQLTemplates;
import com.querydsl.jpa.JPQLTemplates;
import io.smallrye.mutiny.Uni;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.Session;

/**
 * Factory class for query and DML clause creation
 *
 * @author tiwe
 */
public class MutinyQueryFactory {

  private final JPQLTemplates templates;

  private final Mutiny.SessionFactory sessionFactory;

  public MutinyQueryFactory(Mutiny.SessionFactory sessionFactory) {
    this(HQLTemplates.DEFAULT, sessionFactory);
  }

  public MutinyQueryFactory(JPQLTemplates templates, final Mutiny.SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
    this.templates = templates;
  }

  HibernateDeleteClause delete(Session session, EntityPath<?> path) {
    return new HibernateDeleteClause(session, path, templates);
  }

  HibernateUpdateClause update(Session session, EntityPath<?> path) {
    return new HibernateUpdateClause(session, path, templates);
  }

  HibernateInsertClause insert(Session session, EntityPath<?> path) {
    return new HibernateInsertClause(session, path, templates);
  }

  HibernateQuery<?> query(Session session) {
    return new HibernateQuery<Void>(session, templates);
  }

  public <T> Uni<T> withQuery(Function<HibernateQuery<?>, Uni<T>> work) {
    return sessionFactory.withSession(session -> work.apply(query(session)));
  }

  public <T> Uni<T> withQuery(BiFunction<HibernateQuery<?>, MutinySessionContext, Uni<T>> work) {
    return sessionFactory.withSession(
        session -> work.apply(query(session), new MutinySessionContext(session)));
  }

  public <T> Uni<T> withSelectFrom(EntityPath<T> path, Function<HibernateQuery<T>, Uni<T>> work) {
    return sessionFactory.withSession(
        session -> work.apply(query(session).select(path).from(path)));
  }

  public <T> Uni<T> withSelectFrom(
      EntityPath<T> path, BiFunction<HibernateQuery<T>, MutinySessionContext, Uni<T>> work) {
    return sessionFactory.withSession(
        session ->
            work.apply(query(session).select(path).from(path), new MutinySessionContext(session)));
  }

  public Uni<Integer> withInsert(
      EntityPath<?> path, Function<HibernateInsertClause, Uni<Integer>> work) {
    return sessionFactory.withTransaction(
        (session, transaction) -> work.apply(insert(session, path)));
  }

  public Uni<Integer> withInsert(
      EntityPath<?> path,
      BiFunction<HibernateInsertClause, MutinyTransactionContext, Uni<Integer>> work) {
    return sessionFactory.withTransaction(
        (session, transaction) ->
            work.apply(insert(session, path), new MutinyTransactionContext(session, transaction)));
  }

  public Uni<Integer> withUpdate(
      EntityPath<?> path, Function<HibernateUpdateClause, Uni<Integer>> work) {
    return sessionFactory.withTransaction(
        (session, transaction) -> work.apply(update(session, path)));
  }

  public Uni<Integer> withUpdate(
      EntityPath<?> path,
      BiFunction<HibernateUpdateClause, MutinyTransactionContext, Uni<Integer>> work) {
    return sessionFactory.withTransaction(
        (session, transaction) ->
            work.apply(update(session, path), new MutinyTransactionContext(session, transaction)));
  }

  public Uni<Integer> withDelete(
      EntityPath<?> path, Function<HibernateDeleteClause, Uni<Integer>> work) {
    return sessionFactory.withTransaction(
        (session, transaction) -> work.apply(delete(session, path)));
  }

  public Uni<Integer> withDelete(
      EntityPath<?> path,
      BiFunction<HibernateDeleteClause, MutinyTransactionContext, Uni<Integer>> work) {
    return sessionFactory.withTransaction(
        (session, transaction) ->
            work.apply(delete(session, path), new MutinyTransactionContext(session, transaction)));
  }
}
