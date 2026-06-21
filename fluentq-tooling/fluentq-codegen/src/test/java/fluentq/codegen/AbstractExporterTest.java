package fluentq.codegen;

import java.io.File;
import org.junit.jupiter.api.Test;

public abstract class AbstractExporterTest {

  @Test
  public void test() {
    var exporter = new GenericExporter();
    exporter.setTargetFolder(new File("target/" + getClass().getSimpleName()));
    exporter.export(getClass().getClasses());
  }
}
