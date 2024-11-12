package com.querydsl.jpa.suites;

import com.querydsl.core.Target;
import com.querydsl.core.testutil.PostgreSQL;
import com.querydsl.jpa.JPASQLBase;
import com.querydsl.jpa.Mode;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(PostgreSQL.class)
public class PostgreSQLEclipseLinkSuiteTest extends AbstractJPASuite {

  public static class JPASQL extends JPASQLBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("postgresql-eclipselink");
    Mode.target.set(Target.POSTGRESQL);
  }
}
