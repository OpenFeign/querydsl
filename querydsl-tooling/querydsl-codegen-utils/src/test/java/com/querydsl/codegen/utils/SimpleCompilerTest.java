/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils;

import static org.assertj.core.api.Assertions.fail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

public class SimpleCompilerTest {

  @After
  public void tearDown() {
    new File("src/test/java/com/querydsl/codegen/utils/SimpleCompilerTest.class").delete();
  }

  @Test
  @Ignore
  public void Run() throws UnsupportedEncodingException {
    new File("target/out").mkdir();
    var compiler = ToolProvider.getSystemJavaCompiler();
    var classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();

    // create classpath
    var path = new StringBuilder();
    for (URL url : classLoader.getURLs()) {
      if (path.length() > 0) {
        path.append(File.pathSeparator);
      }
      var decodedPath = URLDecoder.decode(url.getPath(), "UTF-8");
      path.append(new File(decodedPath).getAbsolutePath());
    }
    System.err.println(path);

    // compile
    List<String> options =
        Arrays.asList(
            "-classpath",
            path.toString(),
            "-s",
            "target/out",
            "src/test/java/com/querydsl/codegen/utils/SimpleCompilerTest.java");
    var compilationResult = compiler.run(null, null, null, options.toArray(new String[0]));
    if (compilationResult != 0) {
      fail("Compilation Failed");
    }
  }

  @Test
  public void Run2() {
    new File("target/out2").mkdir();
    JavaCompiler compiler = new SimpleCompiler();
    System.out.println(compiler.getClass().getName());
    List<String> options = new ArrayList<>(3);
    options.add("-s");
    options.add("target/out2");
    options.add("src/test/java/com/querydsl/codegen/utils/SimpleCompilerTest.java");
    var compilationResult = compiler.run(null, null, null, options.toArray(new String[0]));
    if (compilationResult != 0) {
      fail("Compilation Failed");
    }
  }
}
