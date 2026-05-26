package fluentq.jpa.suites;

import fluentq.core.Target;
import fluentq.core.testutil.MySQL;
import fluentq.jpa.HibernateBase;
import fluentq.jpa.HibernateSQLBase;
import fluentq.jpa.JPABase;
import fluentq.jpa.JPAIntegrationBase;
import fluentq.jpa.JPASQLBase;
import fluentq.jpa.Mode;
import fluentq.jpa.SerializationBase;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(MySQL.class)
public class MySQLSuiteTest extends AbstractSuite {

  public static class JPA extends JPABase {
    @Override
    public void order_stringValue_toLong() {
      // not supported
    }
  }

  public static class JPASQL extends JPASQLBase {}

  public static class JPAIntegration extends JPAIntegrationBase {}

  public static class Serialization extends SerializationBase {}

  public static class Hibernate extends HibernateBase {
    @Override
    public void order_stringValue_toLong() {
      // not supported
    }
  }

  public static class HibernateSQL extends HibernateSQLBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("mysql");
    Mode.target.set(Target.MYSQL);
  }
}
