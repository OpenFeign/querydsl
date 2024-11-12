package com.querydsl.jpa.suites;

import com.querydsl.core.Target;
import com.querydsl.core.testutil.PostgreSQL;
import com.querydsl.jpa.JPABase;
import com.querydsl.jpa.JPAIntegrationBase;
import com.querydsl.jpa.Mode;
import com.querydsl.jpa.SerializationBase;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(PostgreSQL.class)
public class PostgreSQLEclipseLinkSuiteTest extends AbstractJPASuite {

  public static class JPA extends JPABase {}

  public static class JPAIntegration extends JPAIntegrationBase {}

  public static class Serialization extends SerializationBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("postgresql-eclipselink");
    Mode.target.set(Target.POSTGRESQL);
  }
}
