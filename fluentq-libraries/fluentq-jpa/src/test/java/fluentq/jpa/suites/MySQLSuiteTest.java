package fluentq.jpa.suites;

import fluentq.core.Target;
import fluentq.jpa.HibernateBase;
import fluentq.jpa.HibernateSQLBase;
import fluentq.jpa.JPABase;
import fluentq.jpa.JPAIntegrationBase;
import fluentq.jpa.JPASQLBase;
import fluentq.jpa.Mode;
import fluentq.jpa.SerializationBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.MySQL")
public class MySQLSuiteTest extends AbstractSuite {

  @Nested
  class JPA extends JPABase {
    @Override
    public void order_stringValue_toLong() {
      // not supported
    }
  }

  @Nested
  class JPASQL extends JPASQLBase {}

  @Nested
  class JPAIntegration extends JPAIntegrationBase {}

  @Nested
  class Serialization extends SerializationBase {}

  @Nested
  class Hibernate extends HibernateBase {
    @Override
    public void order_stringValue_toLong() {
      // not supported
    }
  }

  @Nested
  class HibernateSQL extends HibernateSQLBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Mode.mode.set("mysql");
    Mode.target.set(Target.MYSQL);
  }
}
