package fluentq.jpa.suites;

import fluentq.core.Target;
import fluentq.core.testutil.PostgreSQL;
import fluentq.jpa.HibernateBase;
import fluentq.jpa.HibernateSQLBase;
import fluentq.jpa.JPABase;
import fluentq.jpa.JPAIntegrationBase;
import fluentq.jpa.JPASQLBase;
import fluentq.jpa.Mode;
import fluentq.jpa.SerializationBase;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(PostgreSQL.class)
public class PostgreSQLSuiteTest extends AbstractSuite {

  public static class JPA extends JPABase {}

  public static class JPASQL extends JPASQLBase {}

  public static class JPAIntegration extends JPAIntegrationBase {}

  public static class Serialization extends SerializationBase {}

  public static class Hibernate extends HibernateBase {}

  public static class HibernateSQL extends HibernateSQLBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("postgresql");
    Mode.target.set(Target.POSTGRESQL);
  }
}
