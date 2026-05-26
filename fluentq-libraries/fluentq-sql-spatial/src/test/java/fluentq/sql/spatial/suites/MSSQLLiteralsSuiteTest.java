package fluentq.sql.spatial.suites;

import fluentq.core.testutil.SQLServer;
import fluentq.sql.Connections;
import fluentq.sql.spatial.SQLServer2008SpatialTemplates;
import fluentq.sql.spatial.SpatialBase;
import fluentq.sql.suites.AbstractSuite;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(SQLServer.class)
public class MSSQLLiteralsSuiteTest extends AbstractSuite {

  public static class Spatial extends SpatialBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initSQLServer();
    Connections.initConfiguration(
        SQLServer2008SpatialTemplates.builder().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
  }
}
