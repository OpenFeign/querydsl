package com.querydsl.jpa.suites;

import com.querydsl.core.Target;
import com.querydsl.core.testutil.MySQL;
import com.querydsl.jpa.JPASQLBase;
import com.querydsl.jpa.Mode;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(MySQL.class)
public class MySQLEclipseLinkTest extends AbstractJPASuite {

  public static class JPASQL extends JPASQLBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("mysql-eclipselink");
    Mode.target.set(Target.MYSQL);
  }
}
