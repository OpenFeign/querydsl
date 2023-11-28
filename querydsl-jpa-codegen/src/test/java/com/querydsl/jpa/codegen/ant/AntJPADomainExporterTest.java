package com.querydsl.jpa.codegen.ant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TemporaryFolder;

public class AntJPADomainExporterTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Rule public ErrorCollector errors = new ErrorCollector();

  @Test
  public void test() throws IOException {
    AntJPADomainExporter exporter = new AntJPADomainExporter();
    exporter.setNamePrefix("Q");
    exporter.setNameSuffix("");
    Path outputFolder = folder.getRoot().toPath();
    exporter.setTargetFolder(outputFolder.toFile().getAbsolutePath());
    exporter.setPersistenceUnitName("h2");
    exporter.execute();

    File origRoot = new File("../querydsl-jpa/target/generated-test-sources/test-annotations");
    Set<File> files = exporter.getGeneratedFiles();
    assertThat(files).isNotEmpty();
    for (File file : files) {
      Path relativeFile = outputFolder.relativize(file.toPath());
      Path origFile = origRoot.toPath().resolve(relativeFile);
      String reference =
          new String(java.nio.file.Files.readAllBytes(origFile), StandardCharsets.UTF_8);
      String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
      errors.checkThat("Mismatch for " + file.getPath(), content, is(equalTo(reference)));
    }
  }
}
