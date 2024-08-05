package com.querydsl.jpa.codegen.ant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TemporaryFolder;

public class AntJPADomainExporterTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Rule public ErrorCollector errors = new ErrorCollector();

  @Test
  public void test() throws IOException {
    var exporter = new AntJPADomainExporter();
    exporter.setNamePrefix("Q");
    exporter.setNameSuffix("");
    var outputFolder = folder.getRoot().toPath();
    exporter.setTargetFolder(outputFolder.toFile().getAbsolutePath());
    exporter.setPersistenceUnitName("h2");
    exporter.execute();

    var origRoot =
        new File(
            "../../querydsl-libraries/querydsl-jpa/target/generated-test-sources/test-annotations");
    var files = exporter.getGeneratedFiles();
    assertThat(files).isNotEmpty();
    for (File file : files) {
      var relativeFile = outputFolder.relativize(file.toPath());
      var origFile = origRoot.toPath().resolve(relativeFile);
      var reference =
          new String(java.nio.file.Files.readAllBytes(origFile), StandardCharsets.UTF_8);
      var content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
      errors.checkThat("Mismatch for " + file.getPath(), content, is(equalTo(reference)));
    }
  }
}
