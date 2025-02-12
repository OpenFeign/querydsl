package com.querydsl.codegen.utils.support;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;

public class ClassLoaderWrapper extends URLClassLoader {
  public ClassLoaderWrapper(ClassLoader parent) {
    super(new URL[0], parent);
  }

  @Override
  public Enumeration<URL> findResources(String name) throws IOException {
    return Collections.emptyEnumeration();
  }
}
