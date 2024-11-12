package com.querydsl.jpa.suites;

import com.querydsl.core.Target;
import com.querydsl.core.testutil.Derby;
import com.querydsl.jpa.HibernateSQLBase;
import com.querydsl.jpa.JPASQLBase;
import com.querydsl.jpa.Mode;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(Derby.class)
public class DerbySuiteTest extends AbstractSuite {

  public static class JPASQL extends JPASQLBase {}

  public static class HibernateSQL extends HibernateSQLBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("derby");
    Mode.target.set(Target.DERBY);
  }
}
