package fluentq.sql.spatial.suites;

import fluentq.sql.Connections;
import fluentq.sql.spatial.MySQLSpatialTemplates;
import fluentq.sql.spatial.SpatialBase;
import fluentq.sql.suites.AbstractSuite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.MySQL")
public class MySQLLiteralsSuiteTest extends AbstractSuite {

  @Nested
  class Spatial extends SpatialBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Connections.initMySQL();
    Connections.initConfiguration(MySQLSpatialTemplates.builder().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
  }
}
