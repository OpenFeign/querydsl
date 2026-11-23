package fluentq.sql.spatial.suites;

import fluentq.sql.Connections;
import fluentq.sql.spatial.MariaDBSpatialTemplates;
import fluentq.sql.spatial.SpatialBase;
import fluentq.sql.suites.AbstractSuite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.MariaDB")
public class MariaDBSuiteTest extends AbstractSuite {

  @Nested
  class Spatial extends SpatialBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Connections.initMariaDB();
    Connections.initConfiguration(MariaDBSpatialTemplates.builder().newLineToSingleSpace().build());
  }
}
