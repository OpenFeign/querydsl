package com.querydsl.jpa.suites;

import com.querydsl.core.Target;
import com.querydsl.core.testutil.H2;
import com.querydsl.jpa.JPABase;
import com.querydsl.jpa.JPAIntegrationBase;
import com.querydsl.jpa.JPASQLBase;
import com.querydsl.jpa.Mode;
import com.querydsl.jpa.SerializationBase;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

@Ignore
@Category(H2.class)
public class H2OpenJPATest extends AbstractJPASuite {

  public static class JPA extends JPABase {}

  public static class JPASQL extends JPASQLBase {}

  public static class JPAIntegration extends JPAIntegrationBase {}

  public static class Serialization extends SerializationBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("h2-openjpa");
    Mode.target.set(Target.H2);
  }
}
