package fluentq.sql.spatial.suites;

import fluentq.core.testutil.PostgreSQL;
import fluentq.sql.Connections;
import fluentq.sql.spatial.PostGISTemplates;
import fluentq.sql.spatial.SpatialBase;
import fluentq.sql.suites.AbstractSuite;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(PostgreSQL.class)
public class PostgreSQLLiteralsSuiteTest extends AbstractSuite {

  public static class Spatial extends SpatialBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initPostgreSQL();
    Connections.initConfiguration(
        PostGISTemplates.builder().quote().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
  }
}
