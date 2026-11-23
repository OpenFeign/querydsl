package fluentq.sql.spatial.suites;

import fluentq.sql.Connections;
import fluentq.sql.spatial.PostGISTemplates;
import fluentq.sql.spatial.SpatialBase;
import fluentq.sql.suites.AbstractSuite;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.PostgreSQL")
public class PostgreSQLLiteralsSuiteTest extends AbstractSuite {

  @Nested
  class Spatial extends SpatialBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Connections.initPostgreSQL();
    Connections.initConfiguration(
        PostGISTemplates.builder().quote().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
  }
}
