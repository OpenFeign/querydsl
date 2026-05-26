package fluentq.sql.spatial.suites;

import fluentq.core.testutil.H2;
import fluentq.sql.Connections;
import fluentq.sql.spatial.H2GISTemplates;
import fluentq.sql.spatial.SpatialBase;
import fluentq.sql.suites.AbstractSuite;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(H2.class)
public class H2LiteralsSuiteTest extends AbstractSuite {

  public static class Spatial extends SpatialBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Connections.initH2();
    Connections.initConfiguration(H2GISTemplates.builder().newLineToSingleSpace().build());
    Connections.getConfiguration().setUseLiterals(true);
  }
}
