package com.querydsl.core;

import com.querydsl.core.testutil.ThreadSafety;
import io.github.classgraph.ClassGraph;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class CycleClassInitDependencyTest {

  private static ClassLoader loader;

  @BeforeAll
  static void overrideClassLoader() {
    loader = Thread.currentThread().getContextClassLoader();
    Collection<URL> urls = new ClassGraph().getClasspathURLs();
    ClassLoader cl = URLClassLoader.newInstance(urls.toArray(new URL[0]), null /*no delegation*/);
    Thread.currentThread().setContextClassLoader(cl);
  }

  @AfterAll
  static void resetClassLoader() {
    Thread.currentThread().setContextClassLoader(loader);
  }

  @Test
  @Timeout(value = 2000, unit = TimeUnit.MILLISECONDS)
  void test() {

    // each thread wants to load one part of the dependency circle
    ThreadSafety.check(
        new LoadClassRunnable("com.querydsl.core.types.dsl.NumberExpression"),
        new LoadClassRunnable("com.querydsl.core.types.dsl.Expressions"));
  }

  private static class LoadClassRunnable implements Runnable {

    private final String classToLoad;

    LoadClassRunnable(String classToLoad) {
      this.classToLoad = classToLoad;
    }

    @Override
    public void run() {
      try {
        Class.forName(classToLoad, true, Thread.currentThread().getContextClassLoader());
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
