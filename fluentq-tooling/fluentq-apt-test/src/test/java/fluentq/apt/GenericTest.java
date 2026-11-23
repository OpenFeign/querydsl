package fluentq.apt;

import fluentq.apt.jpa.JPAAnnotationProcessor;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GenericTest extends AbstractProcessorTest {

  @Test
  public void test() throws IOException {
    List<String> classes =
        Collections.singletonList("src/test/java/fluentq/apt/domain/Generic7Test.java");
    process(FluentQAnnotationProcessor.class, classes, "GenericTest");
  }

  @Test
  public void test2() throws IOException {
    List<String> classes =
        Collections.singletonList("src/test/java/fluentq/apt/domain/Generic9Test.java");
    process(JPAAnnotationProcessor.class, classes, "GenericTest2");
  }
}
