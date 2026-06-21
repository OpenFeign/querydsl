package fluentq.jpa.codegen.ant;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AntJPADomainExporterTest {

  @TempDir File folder;

  @Test
  public void test() throws IOException {
    var exporter = new AntJPADomainExporter();
    exporter.setNamePrefix("Q");
    exporter.setNameSuffix("");
    var outputFolder = folder.toPath();
    exporter.setTargetFolder(outputFolder.toFile().getAbsolutePath());
    exporter.setPersistenceUnitName("h2");
    exporter.execute();

    var origRoot =
        new File(
            "../../fluentq-libraries/fluentq-jpa/target/generated-test-sources/test-annotations");
    var files = exporter.getGeneratedFiles();
    assertThat(files).isNotEmpty();
    for (File file : files) {
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

      assertThat(content).withFailMessage("Mismatch for %s", file.getPath()).isEqualTo(reference);
    }
  }
}
