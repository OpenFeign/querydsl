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

@Tag("fluentq.core.testutil.PostgreSQL")
public class PostgreSQLSuiteTest extends AbstractSuite {

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

  @BeforeAll
  public static void setUp() throws Exception {
    Mode.mode.set("postgresql");
    Mode.target.set(Target.POSTGRESQL);
  }
}
