package com.querydsl.jpa.suites;

import com.querydsl.core.Target;
import com.querydsl.core.testutil.SQLServer;
import com.querydsl.jpa.HibernateBase;
import com.querydsl.jpa.JPABase;
import com.querydsl.jpa.JPAIntegrationBase;
import com.querydsl.jpa.Mode;
import com.querydsl.jpa.SerializationBase;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

@Category(SQLServer.class)
public class MSSQLSuiteTest extends AbstractSuite {

  public static class JPA extends JPABase {}

  public static class JPAIntegration extends JPAIntegrationBase {}

  public static class Serialization extends SerializationBase {}

  public static class Hibernate extends HibernateBase {}

  @BeforeClass
  public static void setUp() throws Exception {
    Mode.mode.set("mssql");
    Mode.target.set(Target.SQLSERVER);
  }
}
