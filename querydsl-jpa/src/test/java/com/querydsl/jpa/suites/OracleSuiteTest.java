package com.querydsl.jpa.suites;

import com.querydsl.core.Target;
import com.querydsl.core.testutil.Oracle;
import com.querydsl.jpa.*;
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

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("oracle");
    Mode.target.set(Target.ORACLE);
  }
}
