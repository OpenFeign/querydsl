package com.querydsl.example.sql.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;

/** JUnit 5 replacement for the former {@code GuiceTestRunner}: builds test instances via Guice. */
public class GuiceTestExtension implements TestInstanceFactory {

  private static final Injector injector = Guice.createInjector(new ServiceModule());

  @Override
  public Object createTestInstance(
      TestInstanceFactoryContext factoryContext, ExtensionContext extensionContext) {
    return injector.getInstance(factoryContext.getTestClass());
  }
}
