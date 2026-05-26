package fluentq.sql.spatial.suites;

import fluentq.core.testutil.MySQL;
import fluentq.sql.Connections;
import fluentq.sql.spatial.MySQLSpatialTemplates;
import fluentq.sql.spatial.SpatialBase;
import fluentq.sql.suites.AbstractSuite;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(MySQL.class)
public class MySQLSuiteTest extends AbstractSuite {

  public static class Spatial extends SpatialBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initMySQL();
    Connections.initConfiguration(MySQLSpatialTemplates.builder().newLineToSingleSpace().build());
  }
}
