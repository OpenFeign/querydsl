package fluentq.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.apt.AbstractProcessorTest;
import fluentq.apt.FluentQAnnotationProcessor;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class InnerExtensionsTest extends AbstractProcessorTest {

  private static final String packagePath = "src/test/apt/fluentq/";

  @Test
  public void process() throws IOException {
    List<String> sources =
        Arrays.asList(
            new File(packagePath, "InnerExtensions.java").getPath(),
            new File(packagePath, "ExampleEntity2.java").getPath());
    process(FluentQAnnotationProcessor.class, sources, "innerextensions");
    var qtypeContent =
        new String(
            Files.readAllBytes(
                Path.of("target", "innerextensions", "com", "fluentq", "QExampleEntity2.java")),
            StandardCharsets.UTF_8);
    assertThat(qtypeContent)
        .contains("return InnerExtensions.ExampleEntity2Extensions.isZero(this);");
  }
}
