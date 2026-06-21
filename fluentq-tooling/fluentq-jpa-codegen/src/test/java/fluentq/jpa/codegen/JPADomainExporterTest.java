/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.jpa.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Persistence;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

public class JPADomainExporterTest {

  private final File origRoot =
      new File(
          "../../fluentq-libraries/fluentq-jpa/target/generated-test-sources/test-annotations");

  @TestFactory
  public Stream<DynamicTest> generatedFilesMatchReference() throws Exception {
    var emf = Persistence.createEntityManagerFactory("h2", new Properties());
    var outputFolder = Files.createTempDirectory("jpa-exporter-test");
    var exporter = new JPADomainExporter(outputFolder.toFile(), emf.getMetamodel());
    exporter.execute();

    var files = exporter.getGeneratedFiles();
    assertThat(files).isNotEmpty();

    return files.stream()
        .sorted(Comparator.comparing(File::getName))
        .map(
            file ->
                DynamicTest.dynamicTest(file.getName(), () -> assertMatches(outputFolder, file)));
  }

  private void assertMatches(Path outputFolder, File file) throws IOException {
    var relativeFile = outputFolder.relativize(file.toPath());
    var origFile = origRoot.toPath().resolve(relativeFile);
    var reference = Files.readString(origFile);
    var content = Files.readString(file.toPath());

    if (file.getName().equals("QCalendar.java")) {
      // The APT processor does not apply @Temporal(TemporalType.DATE) to @ElementCollection
      // map values, so it generates DateTimePath for java.util.Date. The JPADomainExporter
      // correctly reads @Temporal from the Hibernate metamodel and generates DatePath.
      // Hibernate 7.3+ fixed metamodel exposure of @Temporal for map attributes.
      reference = reference.replace("DateTimePath", "DatePath");
    }

    assertThat(content).as("Mismatch for " + file.getName() + "\n").isEqualTo(reference);
  }
}
