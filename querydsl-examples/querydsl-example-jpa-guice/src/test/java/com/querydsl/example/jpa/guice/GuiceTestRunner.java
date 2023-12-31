package com.querydsl.example.jpa.guice;

import com.google.inject.Guice;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class GuiceTestRunner extends BlockJUnit4ClassRunner {

  public GuiceTestRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  @Override
  protected Object createTest() throws Exception {
    return Guice.createInjector(new ServiceModule()).getInstance(getTestClass().getJavaClass());
  }
}
