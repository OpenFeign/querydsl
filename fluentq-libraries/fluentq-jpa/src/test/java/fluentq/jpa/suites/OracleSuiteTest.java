package fluentq.jpa.suites;

import fluentq.core.Target;
import fluentq.core.testutil.Oracle;
import fluentq.jpa.HibernateBase;
import fluentq.jpa.HibernateSQLBase;
import fluentq.jpa.JPABase;
import fluentq.jpa.JPAIntegrationBase;
import fluentq.jpa.JPASQLBase;
import fluentq.jpa.Mode;
import fluentq.jpa.SerializationBase;
import java.util.TimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(Oracle.class)
public class OracleSuiteTest extends AbstractSuite {

  public static class JPA extends JPABase {}

  public static class JPASQL extends JPASQLBase {}

  public static class JPAIntegration extends JPAIntegrationBase {}

  public static class Serialization extends SerializationBase {}

  public static class Hibernate extends HibernateBase {}

  public static class HibernateSQL extends HibernateSQLBase {}

  private static TimeZone defaultZone;

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("oracle");
    Mode.target.set(Target.ORACLE);

    // change time zone to work around ORA-01882
    // see https://gist.github.com/jarek-przygodzki/cbea3cedae3aef2bbbe0ff6b057e8321
    // the test may work fine on your machine without this, but it fails when the GitHub runner
    // executes it
    defaultZone = TimeZone.getDefault();
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @AfterClass
  public static void tearDown() {
    TimeZone.setDefault(defaultZone);
  }
}
