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

import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.Type;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.tool.EclipseFileManager;

/**
 * EvaluatorFactory is a factory implementation for creating Evaluator instances
 *
 * @author tiwe
 */
public class ECJEvaluatorFactory extends AbstractEvaluatorFactory {

  private final MemFileManager fileManager;

  private final ClassLoader parentClassLoader;

  private final List<String> problemList = new ArrayList<>();

  private final CompilerOptions compilerOptions;

  public static CompilerOptions getDefaultCompilerOptions() {
    var javaSpecVersion = System.getProperty("java.specification.version");
    // use Java 8 compliance to disable module support and avoid NPE in newer ECJ versions
    if (!javaSpecVersion.startsWith("1.")) {
      javaSpecVersion = "1.8";
    }
    Map<String, String> settings = new HashMap<>();
    settings.put(CompilerOptions.OPTION_Source, javaSpecVersion);
    settings.put(CompilerOptions.OPTION_TargetPlatform, javaSpecVersion);
    settings.put(CompilerOptions.OPTION_ReportDeprecation, CompilerOptions.IGNORE);
    return new CompilerOptions(settings);
  }

  public ECJEvaluatorFactory(ClassLoader parent) {
    this(parent, getDefaultCompilerOptions());
  }

  public ECJEvaluatorFactory(ClassLoader parent, CompilerOptions compilerOptions) {
    this.parentClassLoader = parent;
    this.fileManager =
        new MemFileManager(
            parent, new EclipseFileManager(Locale.getDefault(), Charset.defaultCharset()));
    this.loader = fileManager.getClassLoader(StandardLocation.CLASS_OUTPUT);
    this.compilerOptions = compilerOptions;
  }

  @Override
  protected void compile(
      String source,
      ClassType projectionType,
      String[] names,
      Type[] types,
      String id,
      Map<String, Object> constants)
      throws IOException {
    // create source
    source = createSource(source, projectionType, names, types, id, constants);

    // compile
    final var targetContents = source.toCharArray();
    final var targetName = id;
    final var targetCompilationUnits =
        new ICompilationUnit[] {
          new ICompilationUnit() {
            @Override
            public char[] getContents() {
              return targetContents;
            }

            @Override
            public char[] getMainTypeName() {
              int dot = targetName.lastIndexOf('.');
              if (dot > 0) {
                return targetName.substring(dot + 1).toCharArray();
              } else {
                return targetName.toCharArray();
              }
            }

            @Override
            public char[][] getPackageName() {
              StringTokenizer tok = new StringTokenizer(targetName, ".");
              char[][] result = new char[tok.countTokens() - 1][];
              for (int j = 0; j < result.length; j++) {
                result[j] = tok.nextToken().toCharArray();
              }
              return result;
            }

            @Override
            public char[] getFileName() {
              return CharOperation.concat(targetName.toCharArray(), ".java".toCharArray());
            }

            @Override
            public boolean ignoreOptionalProblems() {
              return true;
            }
          }
        };

    INameEnvironment env =
        new INameEnvironment() {

          private String join(char[][] compoundName, char separator) {
            if (compoundName == null) return "";

            var result = new StringBuilder();
            for (int i = 0; i < compoundName.length; i++) {
              result.append(compoundName[i]);
              if (i < compoundName.length - 1) {
                result.append(separator);
              }
            }
            return result.toString();
          }

          @Override
          public NameEnvironmentAnswer findType(char[][] compoundTypeName) {
            return findType(join(compoundTypeName, '.'));
          }

          @Override
          public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName) {
            return findType(CharOperation.arrayConcat(packageName, typeName));
          }

          private boolean isClass(String result) {
            if (result == null || result.isEmpty()) {
              return false;
            }

            // if it's the class we're compiling, then of course it's a class
            if (result.equals(targetName)) {
              return true;
            }
            InputStream is = null;
            try {
              // if this is a class we've already compiled, it's a class
              is = loader.getResourceAsStream(result);
              if (is == null) {
                // use our normal class loader now...
                var resourceName = result.replace('.', '/') + ".class";
                is = parentClassLoader.getResourceAsStream(resourceName);
                if (is == null && !result.contains(".")) {
                  // we couldn't find the class, and it has no package; is it a core class?
                  is = parentClassLoader.getResourceAsStream("java/lang/" + resourceName);
                }
              }
              return is != null;
            } finally {
              if (is != null) {
                try {
                  is.close();
                } catch (IOException ex) {
                }
              }
            }
          }

          @Override
          public boolean isPackage(char[][] parentPackageName, char[] packageName) {
            // if the parent is a class, the child can't be a package
            var parent = join(parentPackageName, '.');
            if (isClass(parent)) {
              return false;
            }

            // if the child is a class, it's not a package
            var qualifiedName = (parent.isEmpty() ? "" : parent + ".") + new String(packageName);
            return !isClass(qualifiedName);
          }

          @Override
          public void cleanup() {}

          private NameEnvironmentAnswer findType(String className) {
            var resourceName = className.replace('.', '/') + ".class";
            InputStream is = null;
            try {
              // we're only asking ECJ to compile a single class; we shouldn't need this
              if (className.equals(targetName)) {
                return new NameEnvironmentAnswer(targetCompilationUnits[0], null);
              }

              is = loader.getResourceAsStream(resourceName);
              if (is == null) {
                is = parentClassLoader.getResourceAsStream(resourceName);
              }

              if (is != null) {

                var buffer = new ByteArrayOutputStream();
                int nRead;
                var data = new byte[1024];
                while ((nRead = is.read(data, 0, data.length)) != -1) {
                  buffer.write(data, 0, nRead);
                }
                buffer.flush();

                var cfr = new ClassFileReader(buffer.toByteArray(), className.toCharArray(), true);
                return new NameEnvironmentAnswer(cfr, null);
              } else {
                return null;
              }
            } catch (ClassFormatException | IOException ex) {
              throw new RuntimeException(ex);
            } finally {
              if (is != null) {
                try {
                  is.close();
                } catch (IOException e) {
                }
              }
            }
          }
        };

    ICompilerRequestor requestor =
        new ICompilerRequestor() {

          @Override
          public void acceptResult(CompilationResult result) {
            if (result.hasErrors()) {
              for (CategorizedProblem problem : result.getProblems()) {
                if (problem.isError()) {
                  problemList.add(problem.getMessage());
                }
              }
            } else {
              for (ClassFile clazz : result.getClassFiles()) {
                try {
                  var jfo =
                      (MemJavaFileObject)
                          fileManager.getJavaFileForOutput(
                              StandardLocation.CLASS_OUTPUT,
                              new String(clazz.fileName()),
                              JavaFileObject.Kind.CLASS,
                              null);
                  var os = jfo.openOutputStream();
                  os.write(clazz.getBytes());
                } catch (IOException ex) {
                  throw new RuntimeException(ex);
                }
              }
            }
          }
        };

    problemList.clear();

    var policy = DefaultErrorHandlingPolicies.exitAfterAllProblems();
    IProblemFactory problemFactory = new DefaultProblemFactory(Locale.getDefault());

    try {
      // Compiler compiler = new Compiler(env, policy, getCompilerOptions(), requestor,
      // problemFactory, true);
      var compiler = new Compiler(env, policy, compilerOptions, requestor, problemFactory);
      compiler.compile(targetCompilationUnits);
      if (!problemList.isEmpty()) {
        var sb = new StringBuilder();
        for (String problem : problemList) {
          sb.append("\t").append(problem).append("\n");
        }
        throw new CodegenException(
            "Compilation of " + id + " failed:\n" + source + "\n" + sb.toString());
      }
    } catch (RuntimeException ex) {
      // if we encountered an IOException, unbox and throw it;
      // if we encountered a ClassFormatException, box it as an IOException and throw it
      // otherwise, it's a legit RuntimeException,
      //    not one of our checked exceptions boxed as unchecked; just rethrow
      var cause = ex.getCause();
      if (cause != null) {
        if (cause instanceof IOException exception) {
          throw exception;
        } else if (cause instanceof ClassFormatException) {
          throw new IOException(cause);
        }
      }
      throw ex;
    }
  }

  public CompilerOptions getCompilerOptions() {
    return compilerOptions;
  }
}
