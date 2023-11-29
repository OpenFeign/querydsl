package com.querydsl.example.jpa.repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.example.jpa.model.Identifiable;
import com.querydsl.jpa.HQLTemplates;
import com.querydsl.jpa.hibernate.HibernateQuery;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

public abstract class AbstractRepository<T extends Identifiable> implements Repository<T, Long> {

  @Inject private Provider<EntityManager> em;

  protected <T> JPAQuery<T> selectFrom(EntityPath<T> entity) {
    return select(entity).from(entity);
  }

  protected <T> JPAQuery<T> select(Expression<T> select) {
    return new JPAQuery<>(em.get(), HQLTemplates.DEFAULT).select(select);
  }

  protected JPADeleteClause delete(EntityPath<?> entity) {
    return new JPADeleteClause(em.get(), entity, HQLTemplates.DEFAULT);
  }

  protected void detach(Object entity) {
    em.get().detach(entity);
  }

  protected <E> E find(Class<E> type, Long id) {
    return em.get().find(type, id);
  }

  protected void persist(Object entity) {
    em.get().persist(entity);
  }

  protected <E> E merge(E entity) {
    return em.get().merge(entity);
  }

  protected <E extends Identifiable> E persistOrMerge(E entity) {
    if (entity.getId() != null) {
      return merge(entity);
    }
    persist(entity);
    return entity;
  }

  protected void remove(Object entity) {
    em.get().remove(entity);
  }

  protected <T> HibernateQuery<T> selectFromHibernateQuery(EntityPath<T> entity) {
    return selectHibernateQuery(entity).from(entity);
  }

  protected <T> HibernateQuery<T> selectHibernateQuery(Expression<T> select) {
    return new HibernateQuery<>(em.get().unwrap(Session.class)).select(select);
  }
}
