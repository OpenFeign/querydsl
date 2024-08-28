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
package com.querydsl.jpa.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Persistence;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.Properties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class JPADomainExporterTest {

  @Rule public ErrorCollector errors = new ErrorCollector();

  @Parameters
  public static Collection<Object[]> generateFiles() throws Exception {
    var emf = Persistence.createEntityManagerFactory("h2", new Properties());
    var outputFolder = Files.createTempDirectory("jpa-exporter-test");
    var exporter = new JPADomainExporter(outputFolder.toFile(), emf.getMetamodel());
    exporter.execute();

    var files = exporter.getGeneratedFiles();
    assertThat(files).isNotEmpty();

    return files.stream()
        .sorted(Comparator.comparing(File::getName))
        .map(file -> new Object[] {outputFolder, file})
        .toList();
  }

  private File file;
  private Path outputFolder;
  private File origRoot =
      new File(
          "../../querydsl-libraries/querydsl-jpa/target/generated-test-sources/test-annotations");

  public JPADomainExporterTest(Path outputFolder, File file) {
    this.file = file;
    this.outputFolder = outputFolder;
  }

  @Test
  public void test() throws IOException {
    var relativeFile = outputFolder.relativize(file.toPath());
    var origFile = origRoot.toPath().resolve(relativeFile);
    var reference = new String(Files.readAllBytes(origFile), StandardCharsets.UTF_8);
    var content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    assertThat(content).as("Mismatch for " + file.getName() + "\n").isEqualTo(reference);
  }
}
