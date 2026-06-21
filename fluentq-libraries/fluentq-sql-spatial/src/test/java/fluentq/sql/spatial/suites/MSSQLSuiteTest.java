package fluentq.sql.spatial.suites;

import fluentq.sql.Connections;
import fluentq.sql.spatial.SQLServer2008SpatialTemplates;
import fluentq.sql.spatial.SpatialBase;
import fluentq.sql.suites.AbstractSuite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.SQLServer")
public class MSSQLSuiteTest extends AbstractSuite {

  @Nested
  class Spatial extends SpatialBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Connections.initSQLServer();
    Connections.initConfiguration(
        SQLServer2008SpatialTemplates.builder().newLineToSingleSpace().build());
  }
}
