package com.querydsl.example.sql.guice;

import jakarta.inject.Inject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class TransactionInterceptor implements MethodInterceptor {
  @Inject private ConnectionContext context;

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    var method = invocation.getMethod();
    var annotation = method.getAnnotation(Transactional.class);
    if (annotation == null || context.getConnection() != null) {
      return invocation.proceed();
    }
    var connection = context.getConnection(true);
    connection.setAutoCommit(false);
    try {
      var rv = invocation.proceed();
      connection.commit();
      return rv;
    } catch (Exception e) {
      connection.rollback();
      throw e;
    } finally {
      connection.close();
      context.removeConnection();
    }
  }
}
