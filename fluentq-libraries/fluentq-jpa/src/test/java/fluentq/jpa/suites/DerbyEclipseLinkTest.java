package fluentq.jpa.suites;

import fluentq.core.Target;
import fluentq.jpa.JPABase;
import fluentq.jpa.JPAIntegrationBase;
import fluentq.jpa.JPASQLBase;
import fluentq.jpa.Mode;
import fluentq.jpa.SerializationBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;

@Tag("fluentq.core.testutil.Derby")
public class DerbyEclipseLinkTest extends AbstractJPASuite {

  @Nested
  class JPA extends JPABase {
    @Override
    public void order_stringValue() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void order_stringValue_to_integer() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void order_stringValue_toLong() {
      // not supported in MySQL/EclipseLink
    }

    @Override
    public void order_stringValue_toBigInteger() {
      // not supported in MySQL/EclipseLink
    }
  }

  @Nested
  class JPASQL extends JPASQLBase {}

  @Nested
  class JPAIntegration extends JPAIntegrationBase {}

  @Nested
  class Serialization extends SerializationBase {}

  @BeforeAll
  public static void setUp() throws Exception {
    Mode.mode.set("derby-eclipselink");
    Mode.target.set(Target.DERBY);
  }
}
