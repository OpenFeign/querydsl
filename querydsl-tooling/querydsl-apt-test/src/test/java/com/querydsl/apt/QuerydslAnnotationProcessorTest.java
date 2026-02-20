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

import com.querydsl.apt.hibernate.HibernateAnnotationProcessor;
import com.querydsl.apt.jpa.JPAAnnotationProcessor;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class QuerydslAnnotationProcessorTest extends AbstractProcessorTest {

  private static final String PACKAGE_PATH = "src/test/java/com/querydsl/apt/domain/";

  private static final List<String> CLASSES = getFiles(PACKAGE_PATH);

  @Test
  public void process() throws IOException {
    var file = new File(PACKAGE_PATH, "AbstractEntityTest.java");
    process(QuerydslAnnotationProcessor.class, Collections.singletonList(file.getPath()), "qdsl");
  }

  @Test
  public void process_monitoredCompany() throws IOException {
    var path = new File(PACKAGE_PATH, "MonitoredCompany.java").getPath();
    process(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "MonitoredCompany");
  }

  @Test
  public void process_inheritance3() throws IOException {
    var path =
        new File("src/test/java/com/querydsl/apt/inheritance/Inheritance3Test.java").getPath();
    process(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "Inheritance3Test");
  }

  @Test
  public void process_inheritance8() throws IOException {
    var path =
        new File("src/test/java/com/querydsl/apt/inheritance/Inheritance8Test.java").getPath();
    process(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "Inheritance8Test");
  }

  @Test
  public void process_queryEmbedded3() throws IOException {
    var path = new File("src/test/java/com/querydsl/apt/domain/QueryEmbedded3Test.java").getPath();
    process(
        QuerydslAnnotationProcessor.class, Collections.singletonList(path), "QueryEmbedded3Test");
  }

  @Test
  public void process_queryEmbedded4() throws IOException {
    var path = new File("src/test/java/com/querydsl/apt/domain/QueryEmbedded4Test.java").getPath();
    process(
        QuerydslAnnotationProcessor.class, Collections.singletonList(path), "QueryEmbedded4Test");
  }

  @Test
  public void process_delegate() throws IOException {
    var path = new File("src/test/java/com/querydsl/apt/domain/DelegateTest.java").getPath();
    process(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "DelegateTest");
  }

  @Test
  public void process_abstractClasses() throws IOException {
    var path = new File("src/test/java/com/querydsl/apt/domain/AbstractClassesTest.java").getPath();
    process(JPAAnnotationProcessor.class, Collections.singletonList(path), "AbstractClassesTest");
  }

  @Test
  public void process_abstractClasses2() throws IOException {
    var path =
        new File("src/test/java/com/querydsl/apt/domain/AbstractClasses2Test.java").getPath();
    process(JPAAnnotationProcessor.class, Collections.singletonList(path), "abstractClasses2");
  }

  @Test
  public void process_genericSignature() throws IOException {
    var path =
        new File("src/test/java/com/querydsl/apt/domain/GenericSignatureTest.java").getPath();
    process(
        QuerydslAnnotationProcessor.class, Collections.singletonList(path), "GenericSignatureTest");
  }

  @Test
  public void process_abstractProperties2Test() throws IOException {
    var path =
        new File("src/test/java/com/querydsl/apt/domain/AbstractProperties2Test.java").getPath();
    process(
        QuerydslAnnotationProcessor.class,
        Collections.singletonList(path),
        "AbstractProperties2Test");
  }

  @Test
  public void process_inheritance2Test() throws IOException {
    var path =
        new File("src/test/java/com/querydsl/apt/inheritance/Inheritance2Test.java").getPath();
    process(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "InheritanceTest2");
  }

  @Test
  public void process_entityInheritanceTest() throws IOException {
    var path =
        new File("src/test/java/com/querydsl/apt/domain/EntityInheritanceTest.java").getPath();
    process(JPAAnnotationProcessor.class, Collections.singletonList(path), "EntityInheritanceTest");
  }

  @Test
  public void process_enum2Test() throws IOException {
    var path = new File("src/test/java/com/querydsl/apt/domain/Enum2Test.java").getPath();
    process(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "Enum2Test");
  }

  @Test
  public void process_externalEntityTest() throws IOException {
    var path = new File("src/test/java/com/querydsl/apt/domain/ExternalEntityTest.java").getPath();
    process(
        QuerydslAnnotationProcessor.class, Collections.singletonList(path), "ExternalEntityTest");
  }

  @Test
  public void process_generic13Test() throws IOException {
    var path = new File("src/test/java/com/querydsl/apt/domain/Generic13Test.java").getPath();
    process(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "Generic13Test");
  }

  @Test
  public void querydslAnnotationProcessor() throws IOException {
    process(QuerydslAnnotationProcessor.class, CLASSES, "querydsl");
  }

  @Test
  public void jpaAnnotationProcessor() throws IOException {
    process(JPAAnnotationProcessor.class, CLASSES, "jpa");
  }

  @Test
  public void hibernateAnnotationProcessor() throws IOException {
    process(HibernateAnnotationProcessor.class, CLASSES, "hibernate");
  }
}
