/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
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
package com.querydsl.apt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.querydsl.codegen.utils.SimpleCompiler;
import com.querydsl.core.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaCompiler;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;
import org.junit.Ignore;
import org.junit.Test;

public class EclipseCompilationTest {

  private static final String packagePath = "src/test/apt/com/querydsl/eclipse/";

  @Test
  @Ignore
  public void test() throws IOException {
    System.setProperty("jdt.compiler.useSingleThread", "true");
    // select classes
    List<String> classes = new ArrayList<>();
    for (File file : new File(packagePath).listFiles()) {
      if (file.getName().endsWith(".java")) {
        classes.add(file.getPath());
      }
    }

    // prepare output
    var out = new File("target/out-eclipse");
    FileUtils.delete(out);
    if (!out.mkdirs()) {
      fail("Creation of " + out.getPath() + " failed");
    }

    var classPath = SimpleCompiler.getClassPath(getClass().getClassLoader());
    JavaCompiler compiler = new EclipseCompiler();
    List<String> options = new ArrayList<>();
    options.add("-s");
    options.add("target/out-eclipse");
    options.add("-proc:only");
    options.add("-processor");
    options.add(QuerydslAnnotationProcessor.class.getName());
    options.add("-Aquerydsl.entityAccessors=true");
    options.add("-cp");
    options.add(classPath);
    options.add("-source");
    options.add("1.6");
    options.add("-verbose");
    options.addAll(classes);

    var compilationResult =
        compiler.run(null, System.out, System.err, options.toArray(new String[0]));
    if (compilationResult == 0) {
      System.out.println("Compilation is successful");
    } else {
      fail("Compilation Failed");
    }

    var resultFile = new File("target/out-eclipse/com/querydsl/eclipse/QSimpleEntity.java");
    assertThat(resultFile).exists();
    var result = new String(Files.readAllBytes(resultFile.toPath()), StandardCharsets.UTF_8);
    assertThat(result).contains("NumberPath<java.math.BigDecimal> bigDecimalProp");
    assertThat(result).contains("NumberPath<Integer> integerProp");
    assertThat(result).contains("NumberPath<Integer> intProp");
    assertThat(result).contains("StringPath stringProp");
  }
}
