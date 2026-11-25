package com.querydsl.example.jpa.guice;

import jakarta.inject.Provider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Thread-bound {@link EntityManager} with a local-transaction interceptor for {@link
 * Transactional}.
 */
public class JpaPersistence implements Provider<EntityManager>, MethodInterceptor {

  private final ThreadLocal<EntityManager> current = new ThreadLocal<>();

  private final EntityManagerFactory factory;

  public JpaPersistence(EntityManagerFactory factory) {
    this.factory = factory;
  }

  @Override
  public EntityManager get() {
    var em = current.get();
    if (em == null) {
      em = factory.createEntityManager();
      current.set(em);
    }
    return em;
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    var startedWork = current.get() == null;
    var tx = get().getTransaction();
    if (tx.isActive()) {
      return invocation.proceed();
    }
    tx.begin();
    try {
      var result = invocation.proceed();
      tx.commit();
      return result;
    } catch (Throwable e) {
      if (tx.isActive()) {
        tx.rollback();
      }
      throw e;
    } finally {
      if (startedWork) {
        current.get().close();
        current.remove();
      }
    }
  }
}
