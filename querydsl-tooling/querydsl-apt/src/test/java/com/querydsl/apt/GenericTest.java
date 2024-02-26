package com.querydsl.apt;

import com.querydsl.apt.jpa.JPAAnnotationProcessor;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class GenericTest extends AbstractProcessorTest {

  @Test
  public void test() throws IOException {
    List<String> classes =
        Collections.singletonList("src/test/java/com/querydsl/apt/domain/Generic7Test.java");
    process(QuerydslAnnotationProcessor.class, classes, "GenericTest");
  }

  @Test
  public void test2() throws IOException {
    List<String> classes =
        Collections.singletonList("src/test/java/com/querydsl/apt/domain/Generic9Test.java");
    process(JPAAnnotationProcessor.class, classes, "GenericTest2");
  }
}
