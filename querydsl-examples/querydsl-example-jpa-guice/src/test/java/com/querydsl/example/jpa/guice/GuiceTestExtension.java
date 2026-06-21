package com.querydsl.example.jpa.guice;

import com.google.inject.Guice;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;

/** JUnit 5 replacement for the former {@code GuiceTestRunner}: builds test instances via Guice. */
public class GuiceTestExtension implements TestInstanceFactory {

  @Override
  public Object createTestInstance(
      TestInstanceFactoryContext factoryContext, ExtensionContext extensionContext) {
    return Guice.createInjector(new ServiceModule()).getInstance(factoryContext.getTestClass());
  }
}
