/*
 * Copyright 2010, Mysema Ltd
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
package com.querydsl.codegen.utils;

import io.github.classgraph.ClassGraph;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.*;
import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * SimpleCompiler provides a convenience wrapper of the JavaCompiler interface with automatic
 * classpath generation
 *
 * @author tiwe
 */
public class SimpleCompiler implements JavaCompiler {

  public static String getClassPath(ClassLoader cl) {
    return new ClassGraph().overrideClassLoaders(cl).getClasspath();
  }

  private final ClassLoader classLoader;

  private String classPath;

  private final JavaCompiler compiler;

  public SimpleCompiler() {
    this(ToolProvider.getSystemJavaCompiler(), Thread.currentThread().getContextClassLoader());
  }

  public SimpleCompiler(JavaCompiler compiler, ClassLoader classLoader) {
    this.compiler = compiler;
    this.classLoader = classLoader;
  }

  private String getClasspath() {
    if (classPath == null) {
      classPath = getClassPath(classLoader);
    }
    return classPath;
  }

  @Override
  public Set<SourceVersion> getSourceVersions() {
    return compiler.getSourceVersions();
  }

  @Override
  public StandardJavaFileManager getStandardFileManager(
      DiagnosticListener<? super JavaFileObject> diagnosticListener,
      Locale locale,
      Charset charset) {
    return compiler.getStandardFileManager(diagnosticListener, locale, charset);
  }

  @Override
  public CompilationTask getTask(
      Writer out,
      JavaFileManager fileManager,
      DiagnosticListener<? super JavaFileObject> diagnosticListener,
      Iterable<String> options,
      Iterable<String> classes,
      Iterable<? extends JavaFileObject> compilationUnits) {
    return compiler.getTask(
        out, fileManager, diagnosticListener, options, classes, compilationUnits);
  }

  @Override
  public int isSupportedOption(String option) {
    return compiler.isSupportedOption(option);
  }

  @Override
  public int run(InputStream in, OutputStream out, OutputStream err, String... arguments) {
    for (String a : arguments) {
      if (a.equals("-classpath")) {
        return compiler.run(in, out, err, arguments);
      }
    }

    // no classpath given
    List<String> args = new ArrayList<>(arguments.length + 2);
    args.add("-classpath");
    args.add(getClasspath());
    args.addAll(Arrays.asList(arguments));
    return compiler.run(in, out, err, args.toArray(String[]::new));
  }
}
