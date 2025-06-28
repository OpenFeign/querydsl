package com.querydsl.collections;

import static org.junit.Assert.assertTrue;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import org.junit.Test;

public class DefaultEvaluatorFactoryTest {

  @Test
  public void testNullPointerExceptionInFilter() throws IOException {
    // Given
    DefaultEvaluatorFactory factory = new DefaultEvaluatorFactory(CollQueryTemplates.DEFAULT);
    StringPath str = Expressions.stringPath("str");
    QueryMetadata metadata = new DefaultQueryMetadata();
    CollQuery<?> query = new CollQuery<Void>(metadata, new DefaultQueryEngine(factory));
    query.from(str, Collections.singletonList(null)).where(str.isEmpty());

    // When
    query.fetch();

    // Then
    File logFile = new File("target/test.log");
    assertTrue(logFile.exists());
    String logContent = new String(Files.readAllBytes(logFile.toPath()));
    assertTrue(logContent.contains("Caught NullPointerException, treating as false"));
  }
}
