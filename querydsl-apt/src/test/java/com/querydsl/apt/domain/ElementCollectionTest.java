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
package com.querydsl.apt.domain;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.querydsl.apt.jpa.JPAAnnotationProcessor;
import com.querydsl.core.types.dsl.ElementCollectionPath;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import java.io.File;
import java.util.Collection;
import org.junit.Test;

public class ElementCollectionTest {

  @Entity
  public static class EntityClass {

    @ElementCollection Collection<String> collectionOfElements;
  }

  private final File main = new File("src/test/java/").getAbsoluteFile();

  @Test
  public void properties() {
    assertThat(QElementCollectionTest_EntityClass.entityClass.collectionOfElements).isNotNull();
    assertThat(QElementCollectionTest_EntityClass.entityClass.collectionOfElements)
        .isInstanceOf(ElementCollectionPath.class);
  }

  @Test
  public void test() throws Exception {
    final Compilation compilation =
        javac()
            .withProcessors(new JPAAnnotationProcessor())
            .withClasspathFrom(this.getClass().getClassLoader())
            .compile(
                JavaFileObjects.forResource(
                    new File(main, "com/querydsl/apt/domain/ElementCollectionTest.java")
                        .toURI()
                        .toURL()));
    assertThat(compilation).succeeded();
    assertThat(compilation)
        .generatedSourceFile("com.querydsl.apt.domain.QElementCollectionTest_EntityClass")
        .contentsAsUtf8String()
        .contains("createElementCollection(\"collectionOfElements\"");
  }
}
