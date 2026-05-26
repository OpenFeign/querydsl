package fluentq.jpa.suites;

import fluentq.core.Target;
import fluentq.core.testutil.PostgreSQL;
import fluentq.jpa.JPABase;
import fluentq.jpa.JPAIntegrationBase;
import fluentq.jpa.JPASQLBase;
import fluentq.jpa.Mode;
import fluentq.jpa.SerializationBase;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(PostgreSQL.class)
public class PostgreSQLEclipseLinkSuiteTest extends AbstractJPASuite {

  public static class JPA extends JPABase {}

  public static class JPASQL extends JPASQLBase {}

  public static class JPAIntegration extends JPAIntegrationBase {}

  public static class Serialization extends SerializationBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("postgresql-eclipselink");
    Mode.target.set(Target.POSTGRESQL);
  }
}
