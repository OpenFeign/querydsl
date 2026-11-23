package fluentq.jpa.suites;

import fluentq.core.Target;
import fluentq.jpa.HibernateBase;
import fluentq.jpa.HibernateSQLBase;
import fluentq.jpa.JPABase;
import fluentq.jpa.JPAIntegrationBase;
import fluentq.jpa.JPASQLBase;
import fluentq.jpa.Mode;
import fluentq.jpa.SerializationBase;
import java.util.TimeZone;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.Oracle")
public class OracleSuiteTest extends AbstractSuite {

  @Nested
  class JPA extends JPABase {}

  @Nested
  class JPASQL extends JPASQLBase {}

  @Nested
  class JPAIntegration extends JPAIntegrationBase {}

  @Nested
  class Serialization extends SerializationBase {}

  @Nested
  class Hibernate extends HibernateBase {}

  @Nested
  class HibernateSQL extends HibernateSQLBase {}

  private static TimeZone defaultZone;

  @BeforeAll
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

  @AfterAll
  public static void tearDown() {
    TimeZone.setDefault(defaultZone);
  }
}
